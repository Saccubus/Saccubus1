/*
 * avtool
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * ������΂��p�Ɋg�����ꂽVhook���C�u��������
 * �g���郉�C�u�����ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
 */
#include <stdio.h>
#include "common/framehook_ext.h"
#include "avtool.h"

static toolbox Box = {
	.version = TOOLBOX_VERSION,
	.video_length = 0.0f
};

/* �������ffmpeg������Ă΂��֐� */

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
