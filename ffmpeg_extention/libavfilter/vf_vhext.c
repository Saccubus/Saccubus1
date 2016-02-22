/*
 * �g��Vhook�t�B���^
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * ������΂��p�Ɋg�����ꂽVhook���C�u������
 * �쓮�����邽�߂̃t�B���^�ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
 */
#include "libavutil/opt.h"
#include "avfilter.h"
#include "internal.h"
#include "common/framehook_ext.h"
#include <string.h>
#include <stdio.h>
#include <ctype.h>

#if HAVE_DLFCN_H
#include <dlfcn.h>
#else
//dlfcn.h�̖���Windows Mingw���p
#include <windows.h>
#define dlopen(a,b) ((void*)LoadLibrary(a))
#define dlsym(a,b) ((void*)GetProcAddress((HMODULE)(a),(b)))
#define dlclose(a) FreeLibrary((HMODULE)(a));
#define dlerror() "dlerror()"
#define RTLD_NOW 0
#endif

//�f���~�^�BMEncoder���Ƃ͓����ɂ��Ƃ��������悳�����B
#define VHEXT_DELIM '|'

typedef struct{
	const AVClass *class;
	//�����͂��Ƃ��Ǝg���̂Ŋm�ہB
	char* vhextargs;
	char* args;
	char** argv;
	int argc;
	//�_�C�i�~�b�N���C�u�����ւ̃|�C���^
	void* Dynamic;
	//�֐��ւ̃|�C���^
	FrameHookExtConfigureFn ExtConfigure;
	FrameHookExtProcessFn ExtProcess;
	FrameHookExtReleaseFn ExtRelease;
	//FrameHook�̎g���|�C���^
	void* Context;
} Context;

/*
 * ���̒��ł����g���֐���`
 */

char** split(char* str,int str_len,int* argc,char delim);
int decode(char* s,int len);

/*
 *  AVFilter�\���̂Ɋi�[�����֐��Q
 */

static av_cold int init(AVFilterContext *ctx){
    //Context���Ƃ肠�����m��
    Context* const context= ctx->priv;
    int code;
    char* const args = context->vhextargs;
    int const arg_len = strlen(args);
    av_log(ctx, AV_LOG_INFO, "called with args = %s.\n",args);

    //������NULL�Ȃ̂͂�������
    if(!args) {
        av_log(ctx, AV_LOG_ERROR, "Invalid arguments.\n");
        return -1;
    }
    //�����̃R�s�[
    context->args = (char*)av_malloc(arg_len+1);
    if(!context->args){
        av_log(ctx, AV_LOG_ERROR, "Failed to malloc memory for args.\n");
        return -1;
    }
    memcpy(context->args,args,arg_len);
    context->args[arg_len]='\0';//NULL�ōŌ�𖄂߂�B

	//�f�R�[�h
	decode(context->args,arg_len);
	//�����̓W�J
	context->argv = split(context->args,arg_len,&context->argc,VHEXT_DELIM);
	if(!context->argv){
            av_log(ctx, AV_LOG_ERROR, "Failed to split args.\n");
            return -1;
	}

    //DLL�ǂݍ���
    context->Dynamic = dlopen(context->argv[0], RTLD_NOW);
    if (!context->Dynamic) {
        av_log(ctx, AV_LOG_ERROR, "Failed to open lib: %s\nMSG:%s\n",context->argv[0],context->argv[0], dlerror());
        return -1;
    }
	//�e�֐����擾
	context->ExtConfigure = dlsym(context->Dynamic, "ExtConfigure");
	context->ExtProcess = dlsym(context->Dynamic, "ExtProcess");
	context->ExtRelease = dlsym(context->Dynamic, "ExtRelease");
	if(!context->ExtConfigure){
        av_log(ctx, AV_LOG_ERROR, "Failed to get ExtConfigure.\n");
        return -1;
	}
	if(!context->ExtProcess){
        av_log(ctx, AV_LOG_ERROR, "Failed to get ExtProcess.\n");
        return -1;
	}
	if(!context->ExtRelease){
        av_log(ctx, AV_LOG_ERROR, "Failed to get ExtRelease.\n");
        return -1;
	}

	//Configure���Ăяo��
	if((code = context->ExtConfigure(&context->Context,context->argc,context->argv))){
        av_log(ctx, AV_LOG_ERROR, "Failed to configure.Code:%d\n",code);
        return -1;
	}
    return 0;
}

static void uninit(AVFilterContext *ctx){
    //Context���Ƃ肠�����m��
    Context *context= ctx->priv;
    //�J������B
    context->ExtRelease(context->Context);
    //�������J������B
    if(!context->args){
        av_free(context->args);
    }
    if(!context->argv){
        av_free(context->argv);
    }
    //DLL������
    dlclose(context->Dynamic);
}

static int query_formats(AVFilterContext *ctx){
	//SDL�Ŏg���₷�����邽�߂�RGB24�t�H�[�}�b�g��v������B
    static const enum AVPixelFormat pix_fmts[] = { AV_PIX_FMT_RGB24, AV_PIX_FMT_NONE };

    ff_set_common_formats(ctx, ff_make_format_list(pix_fmts));
    return 0;
}

/*
 * AVFilterPad��Input���ɌĂ΂��֐�
 */

static int filter_frame(AVFilterLink *inlink, AVFrame *frame)
{
    //�|�C���^�͊�{
    AVFilterContext* const ctx = inlink->dst;
    AVFilterLink* const outlink = ctx->outputs[0];
    Context* const context = (Context*)ctx->priv;
    //�Ǝ��\���̂ɑ��
    vhext_frame vframe;
    vframe.data = frame->data[0];
    vframe.linesize = frame->linesize[0];
    vframe.w = inlink->w;
    vframe.h = inlink->h;
    vframe.pts = inlink->current_pts_us / (double)AV_TIME_BASE;
    //���C�u�������Ăяo���B
    context->ExtProcess(context->Context,&vframe);

    return ff_filter_frame(outlink, frame);
}

#define OFFSET(x) offsetof(Context, x)
#define FLAGS AV_OPT_FLAG_VIDEO_PARAM|AV_OPT_FLAG_FILTERING_PARAM

#if CONFIG_VHEXT_FILTER

static const AVOption vhext_options[] = {
    { "args",	"vhext options (all args)",	OFFSET(vhextargs),	AV_OPT_TYPE_STRING,	{ .str=NULL },	CHAR_MIN,	CHAR_MAX,	FLAGS },
//  { "dll",	"external dll path (nicovideo)",	OFFSET(dllpath),	AV_OPT_TYPE_STRING,	{ .str=NULL },	CHAR_MIN,	CHAR_MAX,	FLAGS },
    { NULL }
};

AVFILTER_DEFINE_CLASS(vhext);

static const AVFilterPad vhext_inputs[] = {
    {
        .name             = "default",
        .type             = AVMEDIA_TYPE_VIDEO,
        .filter_frame     = filter_frame,
        .needs_writable   = 1,
    //  .min_perms        = AV_PERM_READ | AV_PERM_WRITE
    //  .rej_perms       = AV_PERM_REUSE | AV_PERM_REUSE2,
    },
    { NULL }
};

static const AVFilterPad vhext_outputs[] = {
    {
        .name = "default",
        .type = AVMEDIA_TYPE_VIDEO,
    },
    { NULL }
};
#endif /* CONFIG_VHEXT_FILTER */
AVFilter ff_vf_vhext=
{
    .name      = "vhext",
    .description   = NULL_IF_CONFIG_SMALL("video hook for external dll (nicovideo)."),
    .priv_size = sizeof(Context),
    .priv_class	= &vhext_class,

    .init  = init,
    .query_formats = query_formats,
    .inputs = vhext_inputs,
    .outputs = vhext_outputs,
    .uninit = uninit,
};

/*
 * ���̒��ł̂ݎg����֐�
 */

//����������̕����ɂ���ĕ����܂��B
char** split(char* str,int str_len,int* argc,char delim){
	//�`�F�b�N
	if(!str || delim=='\0' || str_len < 0){
		return 0;
	}
	//�m��
	char** argv = av_malloc(sizeof(char*));
	if(!argv){
		return 0;
	}
	//���[�v�J�n
	int last = 0;
	int i;
	int arg_cnt = 0;
	for(i=0;i<str_len;i++){
		if(str[i] == delim){//�f���~�^�ɒB����
			str[i] = '\0';
			argv[arg_cnt] = &str[last];
			arg_cnt++;
			last = i+1;
			argv = av_realloc(argv,sizeof(char*) * (arg_cnt+1));
		}
	}
	argv[arg_cnt] = &str[last];
	*argc = arg_cnt + 1;
	return argv;
}

//UR�G���R�[�h�L�@���g���܂��B
int decode(char* s,int len){
        int i,j;
        char buf,*s1;
        if(len==0)return(-1);
        s1=(char*)av_malloc(len);
        for(i=0,j=0;i<len;i++,j++)
        {
                if(s[i]=='+'){s1[j]=' ';continue;}
                if(s[i]!='%') {s1[j]=s[i];continue;}
                buf=((s[++i]>='A') ? s[i]-'A'+10 : s[i]-'0');
                buf*=16;
                buf+=((s[++i]>='A') ? s[i]-'A'+10 : s[i]-'0');
                s1[j]=buf;
        }
        for(i=0;i<j;i++) s[i]=s1[i];
        s[i]='\0';
        av_free(s1);
        return(0);
}

