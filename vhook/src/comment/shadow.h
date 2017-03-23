#ifndef SHADOW_H_
#define SHADOW_H_

#define SHADOW_MAX 8
#define SHADOW_DEFAULT 1
#include <SDL/SDL.h>

//�ėp�V���h�E
// �f�[�^5�g �J���}(,)�ŋ�؂� ���͏ȗ��\�B
// 0:�e�̃r�b�g��=slide(0~15)
// 1:�����������ɔ{�����|���Z���Ȃ������邩:0(���Ȃ�)/1(����)(�ȗ���0���Ȃ�)
// 2:�e�̕���=����,��,�E��,�E,�E��,��,����,����8�p�^�[����L��1,����0��8�����g�ݍ��킹
//    16�i��2�����ɃG���R�[�h���Ă��悢�B�S����=FF,�㉺���E=55(�ȗ��l55),�E���̂�=08
// 3:�e�̃O���f�[�V�����r�b�g��=���l�܂��̓p�[�Z���g�l(0%~200%)(0�̓O���f�[�V��������)(�ȗ��l0)
// 4:�e�̃O���f�[�V����max=(�p�[�Z���g�l)->0%~100% (�O���f�[�V���������Ȃ�e�̔Z��)(�ȗ��l100%)
// 5:�e�̃O���f�[�V����min=(�p�[�Z���g�l)->0%~100% (0�͍��ݕ��ŏ��l�ɂ��遁�ȗ��l)
// 6:�t�H���g�̍׎������Ȃ������邩:0(���Ȃ�)/1(����)(�ȗ��l0���Ȃ�)
typedef struct shadow_data {
	unsigned slide: 4;
	unsigned autoresize: 1;
	unsigned fontnormal: 1;
	unsigned pattern: 8;
	unsigned char gradation;
	unsigned char grad_max;
	unsigned char grad_min;
} struct_shadow_data;
// �`�F�b�N�͉e�Ɣ��Ε���������B
#define SHADOW_UPLEFT	0x08
#define SHADOW_UP		0x04
#define SHADOW_UPRIGHT	0x02
#define SHADOW_RIGHT	0x01
#define SHADOW_DOWNRIGHT	0x80
#define SHADOW_DOWN		0x40
#define SHADOW_DOWNLEFT	0x20
#define SHADOW_LEFT		0x10

#include "../main.h"
SDL_Surface* (*ShadowFunc[SHADOW_MAX+1])(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data);
void setting_shadow(const char* datastr, DATA* data);
#include "../mydef.h"
#endif /*SHADOW_H_*/
