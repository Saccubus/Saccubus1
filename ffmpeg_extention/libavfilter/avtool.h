/*
 * avtool
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * ������΂��p�Ɋg�����ꂽFFmpeg����
 * �g���郉�C�u�����ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
 */
#ifndef SACCUBUS_AVINFO_H
#define SACCUBUS_AVINFO_H
#include "common/framehook_ext.h"
#include <libavformat/avformat.h>

int tool_registerInfo(AVFormatContext *in_file,int64_t rec_time);
const toolbox* tool_getToolBox(void);

#endif /* SACCUBUS_AVINFO_H */
