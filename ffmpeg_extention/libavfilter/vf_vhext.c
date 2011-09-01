/*
 * 拡張Vhookフィルタ
 * copyright (c) 2008 ψ（プサイ）
 *
 * さきゅばす用に拡張されたVhookライブラリを
 * 駆動させるためのフィルタです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
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
//dlfcn.hの無いWindows Mingw環境用
#include <windows.h>
#define dlopen(a,b) ((void*)LoadLibrary(a))
#define dlsym(a,b) ((void*)GetProcAddress((HMODULE)(a),(b)))
#define dlclose(a) FreeLibrary((HMODULE)(a));
#define dlerror() "dlerror()"
#define RTLD_NOW 0
#endif

//デリミタ。MEncoder側とは同じにしといた方がよさそう。
#define VHEXT_DELIM '|'

typedef struct{
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

/*
 * この中でだけ使う関数定義
 */

char** split(char* str,int str_len,int* argc,char delim);
int decode(char* s,int len);

/*
 *  AVFilter構造体に格納される関数群
 */

static int init(AVFilterContext *ctx, const char *args, void *opaque){
	//Contextをとりあえず確保
    Context *context= ctx->priv;
    av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]called with args = %s.\n",args);

	//引数がNULLなのはおかしい
    if(!args) {
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Invalid arguments.\n");
        return -1;
    }
    int arg_len = strlen(args);
    //引数のコピー
    context->args = (char*)av_malloc(arg_len+1);
    if(!context->args){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to malloc memory for args.\n");
        return -1;
    }
    memcpy(context->args,args,arg_len);
    context->args[arg_len]='\0';//NULLで最後を埋める。

	//デコード
	decode(context->args,arg_len);
	//引数の展開
	context->argv = split(context->args,arg_len,&context->argc,VHEXT_DELIM);
	if(!context->argv){
            av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to split args.\n");
            return -1;
	}

	//ツールボックスを取得
	context->Box = tool_getToolBox();
	if(!context->Box){
            av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to get ToolBox.\n");
            return -1;
	}

	//DLL読み込み
    context->Dynamic = dlopen(context->argv[0], RTLD_NOW);
    if (!context->Dynamic) {
        av_log(NULL, AV_LOG_ERROR, "[libavfilter/VhookExt Filter][Lib:%s]Failed to open lib: %s\nMSG:%s\n",context->argv[0],context->argv[0], dlerror());
        return -1;
    }
	//各関数を取得
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

	//Configureを呼び出す
	int code;
	if((code = context->ExtConfigure(&context->Context,context->Box,context->argc,context->argv))){
        av_log(ctx, AV_LOG_ERROR, "[libavfilter/VhookExt Filter]Failed to configure.Code:%d\n",code);
        return -1;
	}
    return 0;
}

static void uninit(AVFilterContext *ctx){
    //Contextをとりあえず確保
    Context *context= ctx->priv;
    //開放する。
    context->ExtRelease(context->Context,context->Box);
    //引数も開放する。
    if(!context->args){
        av_free(context->args);
    }
    if(!context->argv){
        av_free(context->argv);
    }
    //DLLも閉じる
    dlclose(context->Dynamic);
}

static int query_formats(AVFilterContext *ctx){
	//SDLで使いやすくするためにRGB24フォーマットを要求する。
    enum PixelFormat pix_fmts[] = { PIX_FMT_RGB24, PIX_FMT_NONE };
    avfilter_set_common_pixel_formats(ctx,avfilter_make_format_list(pix_fmts));
    return 0;
}

/*
 * AVFilterPadのInput側に呼ばれる関数
 */

static void start_frame(AVFilterLink *link, AVFilterBufferRef *bufref){
	//おまじない
    avfilter_start_frame(link->dst->outputs[0], bufref);
}

static void end_frame(AVFilterLink *link){
	//ポインタは基本
    Context *context = link->dst->priv;
    //よくわからないけどとりあえずおまじない（えー
    AVFilterLink* output = link->dst->outputs[0];
    AVFilterBufferRef *buf = link->cur_buf;
    //独自構造体に代入
    vhext_frame frame;
    frame.data = buf->data[0];
    frame.linesize = buf->linesize[0];
    frame.w = buf->video->w;
    frame.h = buf->video->h;
    frame.pts = ((double)buf->pts) / AV_TIME_BASE;
	//ライブラリを呼び出す。
	context->ExtProcess(context->Context,context->Box,&frame);

    //おなじくおなじまい（えええ
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
 * この中でのみ使われる関数
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

