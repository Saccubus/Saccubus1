/*
 * avtool
 * copyright (c) 2008 ψ（プサイ）
 *
 * さきゅばす用に拡張されたFFmpegから
 * 使われるライブラリです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */
#ifndef SACCUBUS_AVINFO_H
#define SACCUBUS_AVINFO_H
#include "common/framehook_ext.h"
#include <libavformat/avformat.h>

int tool_registerInfo(AVFormatContext *in_file,int64_t rec_time);
const toolbox* tool_getToolBox(void);

#endif /* SACCUBUS_AVINFO_H */
