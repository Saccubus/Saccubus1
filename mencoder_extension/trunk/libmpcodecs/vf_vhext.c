//�V�X�e���w�b�_�C���N���[�h
#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
//MEncoder�w�b�_�C���N���[�h
#include "mp_image.h"
#include "mp_msg.h"
#include "img_format.h"
#include "vf.h"
//���O�w�b�_�C���N���[�h
#include "common/framehook_ext.h"
#include "avtool.h"
//�_�C�i�~�b�N���[�h�p�w�b�_�C���N���[�h
#include <dlfcn.h>
//dlfcn.h�̖���Windows Mingw���p
//#define <windows.h>
//#define dlopen(a) ((void*)LoadLibrary(a))
//#define dlsym(a,b) ((void*)GetProcAddress((HMODULE)(a),(b)))
//#define dlclose(a) FreeLibrary((HMODULE)(a));

#include "libvo/fastmemcpy.h"
#include "libavutil/common.h"

//�f���~�^�BFFmpeg���Ƃ͓����ɂ��Ƃ��������悳�����B
#define VHEXT_DELIM '|'

/**
 * �t�B���^�ɕK�v�ȕϐ����܂Ƃ߂��\���́B
 */
typedef struct Context{
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

/**
 * ���̒��ł����g���֐���`
 */
char** split(char* str,int str_len,int* argc,char delim);
int decode(char* s,int len);

/**
 * ���܂����悭������Ȃ��B
 */
static int config(struct vf_instance_s* vf,
       int width, int height, int d_width, int d_height,
       unsigned int flags, unsigned int outfmt){
	//���ɂ��邱�Ƃ͖����B
    return vf_next_config(vf, width, height, d_width, d_height, flags, outfmt);
}

/**
 * �t�H�[�}�b�g���T�|�[�g���Ă��邩�ǂ������`�F�b�N�B
 * ���Ԃ񂱂�Ȋ�����OK�H
 */
static int query_format(struct vf_instance_s* vf, unsigned int fmt){
	if(fmt == IMGFMT_RGB24){//�߂�ǂ����������ꂾ���T�|�[�g�B�T�[�Z����������
		return vf_next_query_format(vf, fmt);
	}
	return 0;
}
/**
 * �����Ŏ��ۂɃC���[�W���Ă΂��B
 */
static int put_image(struct vf_instance_s* vf, mp_image_t* mpi, double pts){
	//�|�C���^�͊�{
    Context *ctx = (Context*)vf->priv;
    //�Ǝ��\���̂ɑ��
    vhext_frame frame;
    frame.data = mpi->planes[0];
    frame.linesize = mpi->stride[0];
    frame.w = mpi->w;
    frame.h = mpi->h;
    frame.pts = pts;
	//���C�u�������Ăяo���B
	ctx->ExtProcess(ctx->Context,ctx->Box,&frame);
    return vf_next_put_image(vf, mpi, pts);
}

/**
 * �I�����ɌĂ΂��֐��B
 */
static void uninit(struct vf_instance_s* vf){
	Context *ctx = (Context*)vf->priv;
    //�J������B
   	ctx->ExtRelease(ctx->Context,ctx->Box);
    //�������J������B
    if(!ctx->args){
    	av_free(ctx->args);
    }
    if(!ctx->argv){
    	av_free(ctx->argv);
    }
    //DLL������
    dlclose(ctx->Dynamic);
}

/**
 * �ŏ��ɌĂ΂�鏉�����֐�
 * FFmpeg������̃R�s�[�����B�ł����ʉ��������o���̂߂�ǂ������c�B
 * �G���[���b�Z�[�W�̕\���Ƃ��������Ɉ������߂�ǂ������B���b�p�[�����Ηǂ��񂾂낤���ǁc�B
 */
static int
open(vf_instance_t* vf, char* args) {
	//vfilters�p�֐����w��
    vf->config = config;
    vf->put_image = put_image;
	vf->query_format = query_format;
	vf->uninit = uninit;
	//�R���e�L�X�g���m��
    vf->priv = malloc(sizeof(Context));
	Context *ctx = (Context*)vf->priv;
	if(!ctx){//���������`�F�b�N
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to malloc context.\n");
		exit(-1);
		//return 0;
	}
	//�������������ăt�B���^������
	if(!args){//������NULL�͂��������ł���B
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Invalid arguments. args is NULL.\n");
		exit(-1);
		//return 0;
	}
	//�ϐ��̒����𒲂ׂ�B
	int arg_len = strlen(args);
    //�����̃R�s�[�̂��߂Ƀ��������m��
    ctx->args = (char*)malloc(arg_len+1);
    if(!ctx->args){//�������m�ۂɎ��s�H
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to malloc memory for args.\n");
		exit(-1);
		//return 0;
    }
	//���ۂɃR�s�[����
    memcpy(ctx->args,args,arg_len);
    ctx->args[arg_len]='\0';//NULL�ōŌ�𖄂߂�B
	//�f�R�[�h
	decode(ctx->args,arg_len);
    mp_msg(MSGT_VFILTER, MSGL_INFO, "called with args = %s.\n",args);
	//�����̓W�J
	ctx->argv = split(ctx->args,arg_len,&ctx->argc,VHEXT_DELIM);
	if(!ctx->argv){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to split args.\n");
		exit(-1);
		//return 0;
	}

	//�c�[���{�b�N�X���擾
	ctx->Box = tool_getToolBox();
	if(!ctx->Box){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to get ToolBox.\n");
		exit(-1);
		//return 0;
	}
	mp_msg(MSGT_VFILTER, MSGL_INFO, "vf_vhext: video length: %f\n",ctx->Box->video_length);

	//DLL�ǂݍ���
    ctx->Dynamic = dlopen(ctx->argv[0], RTLD_NOW);
    if (!ctx->Dynamic) {
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to open lib: %s\nERROR:%s\n",ctx->argv[0], dlerror());
		exit(-1);
		//return 0;
    }
	//�e�֐����擾
	ctx->ExtConfigure = dlsym(ctx->Dynamic, "ExtConfigure");
	ctx->ExtProcess = dlsym(ctx->Dynamic, "ExtProcess");
	ctx->ExtRelease = dlsym(ctx->Dynamic, "ExtRelease");
	if(!ctx->ExtConfigure){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to get ExtConfigure.\n");
		exit(-1);
		//return 0;
	}
	if(!ctx->ExtProcess){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to get ExtProcess.\n");
		exit(-1);
		//return 0;
	}
	if(!ctx->ExtRelease){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to get ExtRelease.\n");
		exit(-1);
		//return 0;
	}

	//Configure���Ăяo��
	int code;
	if((code = ctx->ExtConfigure(&ctx->Context,ctx->Box,ctx->argc,ctx->argv))){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to configure.Code:%d\n",code);
		exit(-1);
		//return 0;
	}
    return 1;
}

/**
 * �t�B���^���̍\����
 */
const vf_info_t vf_info_vhext = {
    "vhook ext",
    "vhext",
    "psi",
    "This is used for saccubus.",
    open,
    NULL
};

/**
 * ���̒��ł̂ݎg����֐�
 * �߂�ǂ������AFFmpeg����R�s�y�ł�����B
 * �C��������FFmpeg�̂��C�����ĂˁB
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

