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
#include "avfilter.h"
#include "common/framehook_ext.h"
#include "avtool.h"
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
	//�����͂��Ƃ��Ǝg���̂Ŋm�ہB
	char* args;
	char** argv;
	int argc;
	//�c�[���{�b�N�X
	const toolbox* Box;
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

static int init(AVFilterContext *ctx, const char *args, void *opaque){
	//Context���Ƃ肠�����m��
    Context *context= ctx->priv;
    av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]called with args = %s.\n",args);

	//������NULL�Ȃ̂͂�������
    if(!args) {
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Invalid arguments.\n");
        return -1;
    }
    int arg_len = strlen(args);
    //�����̃R�s�[
    context->args = (char*)av_malloc(arg_len+1);
    if(!context->args){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to malloc memory for args.\n");
        return -1;
    }
    memcpy(context->args,args,arg_len);
    context->args[arg_len]='\0';//NULL�ōŌ�𖄂߂�B

	//�f�R�[�h
	decode(context->args,arg_len);
	//�����̓W�J
	context->argv = split(context->args,arg_len,&context->argc,VHEXT_DELIM);
	if(!context->argv){
            av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to split args.\n");
            return -1;
	}

	//�c�[���{�b�N�X���擾
	context->Box = tool_getToolBox();
	if(!context->Box){
            av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to get ToolBox.\n");
            return -1;
	}

	//DLL�ǂݍ���
    context->Dynamic = dlopen(context->argv[0], RTLD_NOW);
    if (!context->Dynamic) {
        av_log(NULL, AV_LOG_ERROR, "[libavfilter/VhookExt Filter][Lib:%s]Failed to open lib: %s\nMSG:%s\n",context->argv[0],context->argv[0], dlerror());
        return -1;
    }
	//�e�֐����擾
	context->ExtConfigure = dlsym(context->Dynamic, "ExtConfigure");
	context->ExtProcess = dlsym(context->Dynamic, "ExtProcess");
	context->ExtRelease = dlsym(context->Dynamic, "ExtRelease");
	if(!context->ExtConfigure){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to get ExtConfigure.\n");
        return -1;
	}
	if(!context->ExtProcess){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to get ExtProcess.\n");
        return -1;
	}
	if(!context->ExtRelease){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to get ExtRelease.\n");
        return -1;
	}

	//Configure���Ăяo��
	int code;
	if((code = context->ExtConfigure(&context->Context,context->Box,context->argc,context->argv))){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to configure.Code:%d\n",code);
        return -1;
	}
    return 0;
}

static void uninit(AVFilterContext *ctx){
    //Context���Ƃ肠�����m��
    Context *context= ctx->priv;
    //�J������B
    context->ExtRelease(context->Context,context->Box);
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
    enum PixelFormat pix_fmts[] = { PIX_FMT_RGB24, PIX_FMT_NONE };
    avfilter_set_common_pixel_formats(ctx,avfilter_make_format_list(pix_fmts));
    return 0;
}

/*
 * AVFilterPad��Input���ɌĂ΂��֐�
 */

static void start_frame(AVFilterLink *link, AVFilterBufferRef *bufref){
	//���܂��Ȃ�
    avfilter_start_frame(link->dst->outputs[0], bufref);
}

static void end_frame(AVFilterLink *link){
	//�|�C���^�͊�{
    Context *context = link->dst->priv;
    //�悭�킩��Ȃ����ǂƂ肠�������܂��Ȃ��i���[
    AVFilterLink* output = link->dst->outputs[0];
    AVFilterBufferRef *buf = link->cur_buf;
    //�Ǝ��\���̂ɑ��
    vhext_frame frame;
    frame.data = buf->data[0];
    frame.linesize = buf->linesize[0];
    frame.w = buf->video->w;
    frame.h = buf->video->h;
    frame.pts = ((double)buf->pts) / AV_TIME_BASE;
	//���C�u�������Ăяo���B
	context->ExtProcess(context->Context,context->Box,&frame);

    //���Ȃ������Ȃ��܂��i������
    avfilter_draw_slice(output, 0, buf->video->h, 1);
    avfilter_end_frame(output);
}

AVFilter avfilter_vf_vhext=
{
    .name      = "vhext",

    .priv_size = sizeof(Context),

    .init      = init,
    .uninit    = uninit,

    .query_formats   = query_formats,
    .inputs    = (AVFilterPad[]) {{ .name            = "default",
                                    .type            = AVMEDIA_TYPE_VIDEO,
                                    .start_frame     = start_frame,
                                    .end_frame       = end_frame,
                                    .min_perms       = AV_PERM_WRITE |
                                                       AV_PERM_READ,
                                    .rej_perms       = AV_PERM_REUSE |
                                                       AV_PERM_REUSE2},
                                  { .name = NULL}},
    .outputs   = (AVFilterPad[]) {{ .name            = "default",
                                    .type            = AVMEDIA_TYPE_VIDEO, },
                                  { .name = NULL}},
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

