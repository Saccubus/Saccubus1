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
#include "avtool.h"
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "mp_msg.h"

//詳しくは下参照。
static int file_open(URLContext *h, const char *filename, int flags);
static int file_read(URLContext *h, unsigned char *buf, int size);
static int file_write(URLContext *h, unsigned char *buf, int size);
static offset_t file_seek(URLContext *h, offset_t pos, int whence);
static int file_close(URLContext *h);
static URLProtocol my_file_protocol = {
    "file",
    file_open,
    file_read,
    file_write,
    file_seek,
    file_close,
};

//vhookに渡すツールボックス。
static toolbox Box = {
	.version = TOOLBOX_VERSION,
	.video_length = 0.0f
};

/* こちらはMEncoder側から呼ばれる関数 */

int tool_registerInfo(m_time_size_t *end_at,const char* filename){
	double length;
	AVFormatContext *pFormatCtx;
	av_register_all();
	register_protocol(&my_file_protocol);//ファイルプロトコルを登録。
	if(av_open_input_file(&pFormatCtx, filename, NULL, 0, NULL)!=0){
		mp_msg(MSGT_CPLAYER,MSGL_ERR,"avtool: failed to open video(%s).\n",filename);
		return 0;
	}
	if(av_find_stream_info(pFormatCtx)<0){
		mp_msg(MSGT_CPLAYER,MSGL_ERR,"avtool: failed to find stream info(%s).\n",filename);
		return 0;
	}
	//長さを取得。
	length = ((double)pFormatCtx->duration)/AV_TIME_BASE;
	mp_msg(MSGT_CPLAYER,MSGL_INFO,"avtool: length: %5.3f\n",length);
	//閉じる
	av_close_input_file(pFormatCtx);
	//以下video長の決定
	if(end_at->type == END_AT_TIME && length > end_at->pos){
		Box.video_length = end_at->pos;
	}else{
		Box.video_length = length;
	}
	return 0;
}

const toolbox* tool_getToolBox(){
	return &Box;
}

/**
 * コンパイルオプションの影響か何かでファイルプロトコルを自力で登録しなきゃいけないっぽい
 * 再実装するのも息をするのもめんどくさいのでコピペ。
 * GPLだから大丈夫だよね？
 */

/*
 * Buffered file io for ffmpeg system
 * Copyright (c) 2001 Fabrice Bellard
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
#include "libavformat/avformat.h"
#include "libavutil/avstring.h"
#include <fcntl.h>
#include <unistd.h>
#include <sys/time.h>
#include <stdlib.h>

/* standard file protocol */

static int file_open(URLContext *h, const char *filename, int flags)
{
    int access;
    int fd;

    av_strstart(filename, "file:", &filename);

    if (flags & URL_RDWR) {
        access = O_CREAT | O_TRUNC | O_RDWR;
    } else if (flags & URL_WRONLY) {
        access = O_CREAT | O_TRUNC | O_WRONLY;
    } else {
        access = O_RDONLY;
    }
#ifdef O_BINARY
    access |= O_BINARY;
#endif
    fd = open(filename, access, 0666);
    if (fd < 0)
        return -1;
    h->priv_data = (void *)(size_t)fd;
    return 0;
}

static int file_read(URLContext *h, unsigned char *buf, int size)
{
    int fd = (size_t)h->priv_data;
    return read(fd, buf, size);
}

static int file_write(URLContext *h, unsigned char *buf, int size)
{
    int fd = (size_t)h->priv_data;
    return write(fd, buf, size);
}

/* XXX: use llseek */
static offset_t file_seek(URLContext *h, offset_t pos, int whence)
{
    int fd = (size_t)h->priv_data;
    return lseek(fd, pos, whence);
}

static int file_close(URLContext *h)
{
    int fd = (size_t)h->priv_data;
    return close(fd);
}
