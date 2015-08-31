/*
 * �g��Vhook�t�B���^
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * ������΂��p�Ɋg�����ꂽVhook���C�u������
 * �r���h���邽�߂̃w�b�_�ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
 */
#ifndef SACCUBUS_VF_VHEXT_H
#define SACCUBUS_VF_VHEXT_H

/*
 * �Ă΂��Ƃ��Ɉꏏ�ɂ��Ă���toolbox.
 * �������瓮��̏��Ȃ񂩂��擾�ł���B
 */
typedef struct toolbox{
	//�o�[�W����
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
 * �g��vhook���C�u�����p�֐��Q��`
 */

//configure�p
typedef int (FrameHookExtConfigure)(void **ctxp,int argc, char *argv[]);
typedef FrameHookExtConfigure *FrameHookExtConfigureFn;
extern FrameHookExtConfigure ExtConfigure;

//�t���[���p
typedef void (FrameHookExtProcess)(void *ctx,vhext_frame *pict);
typedef FrameHookExtProcess *FrameHookExtProcessFn;
extern FrameHookExtProcess ExtProcess;

//�I�����ɌĂ�
typedef void (FrameHookExtRelease)(void *ctx);
typedef FrameHookExtRelease *FrameHookExtReleaseFn;
extern FrameHookExtRelease ExtRelease;

#endif /* SACCUBUS_VF_VHEXT_H */
