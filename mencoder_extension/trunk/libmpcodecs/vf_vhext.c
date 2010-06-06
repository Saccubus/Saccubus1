//システムヘッダインクルード
#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
//MEncoderヘッダインクルード
#include "mp_image.h"
#include "mp_msg.h"
#include "img_format.h"
#include "vf.h"
//自前ヘッダインクルード
#include "common/framehook_ext.h"
#include "avtool.h"
//ダイナミックロード用ヘッダインクルード
#include <dlfcn.h>
//dlfcn.hの無いWindows Mingw環境用
//#define <windows.h>
//#define dlopen(a) ((void*)LoadLibrary(a))
//#define dlsym(a,b) ((void*)GetProcAddress((HMODULE)(a),(b)))
//#define dlclose(a) FreeLibrary((HMODULE)(a));

#include "libvo/fastmemcpy.h"
#include "libavutil/common.h"

//デリミタ。FFmpeg側とは同じにしといた方がよさそう。
#define VHEXT_DELIM '|'

/**
 * フィルタに必要な変数をまとめた構造体。
 */
typedef struct Context{
	//引数はあとあと使うので確保。
	char* args;
	char** argv;
	int argc;
	//ツールボックス
	const toolbox* Box;
	//ダイナミックライブラリへのポインタ
	void* Dynamic;
	//関数へのポインタ
	FrameHookExtConfigureFn ExtConfigure;
	FrameHookExtProcessFn ExtProcess;
	FrameHookExtReleaseFn ExtRelease;
	//FrameHookの使うポインタ
	void* Context;
} Context;

/**
 * この中でだけ使う関数定義
 */
char** split(char* str,int str_len,int* argc,char delim);
int decode(char* s,int len);

/**
 * いまいちよく分からない。
 */
static int config(struct vf_instance_s* vf,
       int width, int height, int d_width, int d_height,
       unsigned int flags, unsigned int outfmt){
	//特にすることは無い。
    return vf_next_config(vf, width, height, d_width, d_height, flags, outfmt);
}

/**
 * フォーマットをサポートしているかどうかをチェック。
 * たぶんこんな感じでOK？
 */
static int query_format(struct vf_instance_s* vf, unsigned int fmt){
	if(fmt == IMGFMT_RGB24){//めんどくさいしこれだけサポート。サーセンｗｗｗｗ
		return vf_next_query_format(vf, fmt);
	}
	return 0;
}
/**
 * ここで実際にイメージが呼ばれる。
 */
static int put_image(struct vf_instance_s* vf, mp_image_t* mpi, double pts){
	//ポインタは基本
    Context *ctx = (Context*)vf->priv;
    //独自構造体に代入
    vhext_frame frame;
    frame.data = mpi->planes[0];
    frame.linesize = mpi->stride[0];
    frame.w = mpi->w;
    frame.h = mpi->h;
    frame.pts = pts;
	//ライブラリを呼び出す。
	ctx->ExtProcess(ctx->Context,ctx->Box,&frame);
    return vf_next_put_image(vf, mpi, pts);
}

/**
 * 終了時に呼ばれる関数。
 */
static void uninit(struct vf_instance_s* vf){
	Context *ctx = (Context*)vf->priv;
    //開放する。
   	ctx->ExtRelease(ctx->Context,ctx->Box);
    //引数も開放する。
    if(!ctx->args){
    	av_free(ctx->args);
    }
    if(!ctx->argv){
    	av_free(ctx->argv);
    }
    //DLLも閉じる
    dlclose(ctx->Dynamic);
}

/**
 * 最初に呼ばれる初期化関数
 * FFmpeg側からのコピー多し。でも共通化処理取り出すのめんどくさい…。
 * エラーメッセージの表示とかが微妙に違ったりめんどくさい。ラッパー書けば良いんだろうけど…。
 */
static int
open(vf_instance_t* vf, char* args) {
	//vfilters用関数を指定
    vf->config = config;
    vf->put_image = put_image;
	vf->query_format = query_format;
	vf->uninit = uninit;
	//コンテキストを確保
    vf->priv = malloc(sizeof(Context));
	Context *ctx = (Context*)vf->priv;
	if(!ctx){//メモリをチェック
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to malloc context.\n");
		exit(-1);
		//return 0;
	}
	//引数を処理してフィルタを準備
	if(!args){//引数がNULLはおかしいでしょ。
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Invalid arguments. args is NULL.\n");
		exit(-1);
		//return 0;
	}
	//変数の長さを調べる。
	int arg_len = strlen(args);
    //引数のコピーのためにメモリを確保
    ctx->args = (char*)malloc(arg_len+1);
    if(!ctx->args){//メモリ確保に失敗？
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to malloc memory for args.\n");
		exit(-1);
		//return 0;
    }
	//実際にコピーする
    memcpy(ctx->args,args,arg_len);
    ctx->args[arg_len]='\0';//NULLで最後を埋める。
	//デコード
	decode(ctx->args,arg_len);
    mp_msg(MSGT_VFILTER, MSGL_INFO, "called with args = %s.\n",args);
	//引数の展開
	ctx->argv = split(ctx->args,arg_len,&ctx->argc,VHEXT_DELIM);
	if(!ctx->argv){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to split args.\n");
		exit(-1);
		//return 0;
	}

	//ツールボックスを取得
	ctx->Box = tool_getToolBox();
	if(!ctx->Box){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to get ToolBox.\n");
		exit(-1);
		//return 0;
	}
	mp_msg(MSGT_VFILTER, MSGL_INFO, "vf_vhext: video length: %f\n",ctx->Box->video_length);

	//DLL読み込み
    ctx->Dynamic = dlopen(ctx->argv[0], RTLD_NOW);
    if (!ctx->Dynamic) {
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to open lib: %s\nERROR:%s\n",ctx->argv[0], dlerror());
		exit(-1);
		//return 0;
    }
	//各関数を取得
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

	//Configureを呼び出す
	int code;
	if((code = ctx->ExtConfigure(&ctx->Context,ctx->Box,ctx->argc,ctx->argv))){
		mp_msg(MSGT_VFILTER, MSGL_ERR, "vf_vhext: Failed to configure.Code:%d\n",code);
		exit(-1);
		//return 0;
	}
    return 1;
}

/**
 * フィルタ情報の構造体
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
 * この中でのみ使われる関数
 * めんどくせえ、FFmpegからコピペでいいや。
 * 修正したらFFmpegのも修正してね。
 */

//文字列を特定の文字によって分けます。
char** split(char* str,int str_len,int* argc,char delim){
	//チェック
	if(!str || delim=='\0' || str_len < 0){
		return 0;
	}
	//確保
	char** argv = av_malloc(sizeof(char*));
	if(!argv){
		return 0;
	}
	//ループ開始
	int last = 0;
	int i;
	int arg_cnt = 0;
	for(i=0;i<str_len;i++){
		if(str[i] == delim){//デリミタに達した
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

//URエンコード記法が使えます。
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

