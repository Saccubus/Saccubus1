/*
 * avtool
 * copyright (c) 2008 ψ（プサイ）
 *
 * さきゅばす用に拡張されたVhookライブラリから
 * 使われるライブラリです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */
#include <stdio.h>
#include "common/framehook_ext.h"
#include "avtool.h"

static toolbox Box = {
	.version = TOOLBOX_VERSION,
	.video_length = 0.0f
};

/* こちらはffmpeg側から呼ばれる関数 */

int tool_registerInfo(AVFormatContext *in_file,int64_t rec_time){
	if(in_file->duration > rec_time && rec_time > 0){
		Box.video_length = ((double)rec_time) / AV_TIME_BASE;
	}
	Box.video_length = ((double)in_file->duration)/AV_TIME_BASE;
	return 0;
}

const toolbox* tool_getToolBox(){
	return &Box;
}
