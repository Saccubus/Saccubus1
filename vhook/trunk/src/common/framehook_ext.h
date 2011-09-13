/*
 * 拡張Vhookフィルタ
 * copyright (c) 2008 ψ（プサイ）
 *
 * さきゅばす用に拡張されたVhookライブラリを
 * ビルドするためのヘッダです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */
#ifndef SACCUBUS_VF_VHEXT_H
#define SACCUBUS_VF_VHEXT_H
/*
 * ツールボックスのバージョン
 * DLLの中で確認しといた方がいい。
 */
#define TOOLBOX_VERSION 2

/*
 * 呼ばれるときに一緒についてくるtoolbox.
 * ここから動画の情報なんかも取得できる。
 */
typedef struct toolbox{
	//バージョン
	int version;
	double video_length;
} toolbox;

typedef struct vhext_frame{
	void *data;
	int linesize;
	int w;
	int h;
	double pts;
} vhext_frame;


/*
 * 拡張vhookライブラリ用関数群定義
 */

//configure用
typedef int (FrameHookExtConfigure)(void **ctxp,const toolbox *tbox, int argc, char *argv[]);
typedef FrameHookExtConfigure *FrameHookExtConfigureFn;
extern FrameHookExtConfigure ExtConfigure;

//フレーム用
typedef void (FrameHookExtProcess)(void *ctx,const toolbox *tbox,vhext_frame *pict);
typedef FrameHookExtProcess *FrameHookExtProcessFn;
extern FrameHookExtProcess ExtProcess;

//終了時に呼ぶ
typedef void (FrameHookExtRelease)(void *ctx,const toolbox *tbox);
typedef FrameHookExtRelease *FrameHookExtReleaseFn;
extern FrameHookExtRelease ExtRelease;

#endif /* SACCUBUS_VF_VHEXT_H */
