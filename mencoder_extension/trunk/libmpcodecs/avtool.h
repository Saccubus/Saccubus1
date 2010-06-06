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
#include "../m_option.h"

int tool_registerInfo(m_time_size_t *end_at,const char* filename);
const toolbox* tool_getToolBox();

#endif /* SACCUBUS_AVINFO_H */
