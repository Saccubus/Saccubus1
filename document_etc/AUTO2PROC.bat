@echo off
echo -----------------------------------------------------------------
echo �Q�v���Z�X�p�u������΂��v�����ϊ��o�b�`�t�@�C��    Ver.1.0
echo �@�K�C�_���X�t��
echo �@�}���`�R�ACPU�A�u���E�U���L�ݒ肪�K�{�ł��i�����j
echo -----------------------------------------------------------------

:���̍s�ɑ����s�͖������Č��\�ł��B
IF "%1"=="" GOTO Setup
IF NOT "%~1"=="0" GOTO RUN1START
:Setup
echo.
SET /P ANS=�u������΂��v�ŃI�v�V�����ݒ�E�u���E�U���L�ݒ�����܂������H[Y/N]
IF /I "%ANS%"=="Y" goto RUN0_START
echo.
ECHO �I�v�V�����ݒ�E�u���E�U���L�ݒ�����ĉ������B
echo.
SET /P DMY=��x�蓮�Ń_�E�����[�h�ƕϊ����m�F����̂𐄏����܂��BOK�H
java -jar Saccubus.jar
:RUN0_START
echo.
SET /P ANS=%~nx0 ��ҏW���܂������H[Y/N]
IF /I "%ANS%"=="Y" goto RUN0_END
echo.
SET /P DMY=%~nx0 ��ҏW���ĉ������BOK�H
EXIT
:RUN0_END
echo.
echo �r���Œ��f����ɂ́A�R�}���h�v�����v�g��ʂ��I����
echo �^�X�N�}�l�[�W���[����ffmpeg�v���Z�X�Q���I�������܂��B
echo.
echo �}���`�R�A�v���Z�b�T�[�����ł��B
echo.
echo �I����̃V���b�g�_�E���̐ݒ�͏o���܂���B�������������B
if "%1"=="0" (
echo.
echo ���O�� auto2log_[1,2].txt �ɋL�^����܂��B
)
echo.
SET /P ANS=�����ϊ������s���܂��B[Y/N]�H
IF /I "%ANS%"=="N" goto EOF
:RUN1START

:���������I����ɃV���b�g�_�E���̐ݒ�͏o���܂���B�������������B
:set SHUTDOWN=NO
:�������������[���A�h���X���u���E�U���L�ݒ�ς݂Ȃ�A���̂܂܂�OK
set MAILADDR=doremi@mahodo.co.jp
:�����������p�X���[�h���u���E�U���L�ݒ�ς݂Ȃ�A���̂܂܂�OK
set PASSWORD=steeki_tabetai
:���̍s�ɑ����s�͖������Č��\�ł��B
set CMD=java -jar Saccubus.jar %MAILADDR% %PASSWORD%
IF "%~1"=="" (
start %~nx0 1
java -jar sleep.jar 5
start %~nx0 2
EXIT
)
IF "%1"=="0" (
start AUTO2PROCDEBUG.BAT %~nx0 1
java -jar sleep.jar 5
start AUTO2PROCDEBUG.BAT %~nx0 2
EXIT
)
IF NOT "%~1"=="1" GOTO RUN1END

:�������������������L�̂������̎�����ύX���āA�_�u���N���b�N�I�ł���B��

:�ȍ~�A
:%CMD% <����ID> <�ߋ����O>
:�̂悤�Ȍ`�ŕϊ�������������w�肵�ď����Ă��������B�ߋ����O�͖����Ă��B
:������񉽍s�ł��\�ł��B
:�ȉ��͉ߋ����O�L��̗�
:%CMD% sm123456789 2011/7/1
:%CMD% sm123456789 "2011/1/1 10:10"

:�����������������������ȉ��̍s�̓v���Z�X�P�̏������w�肵�ĉ������B��������
:�L�q���2011/8/12�̃{�J�������L���O��ʓ���A�ߋ����O�Ȃ��ł�
:�v���Z�X�Q�Əd�����Ȃ��悤�ɒ��ӂ��ĉ�����
%CMD% sm15255755
:%CMD% sm15230821
:�����������������������ȏ�̍s�̓v���Z�X�P�̏������w�肵�ĉ������B��������


:���̍s�ɑ����s�͖������Č��\�ł��B
GOTO RUN_END
:RUN1END
IF NOT "%~1"=="2" GOTO RUN2END

:�����������������������ȉ��̍s�̓v���Z�X�Q�̏������w�肵�ĉ������B��������
:�L�q���2011/8/12�̃{�J�������L���O��ʓ���A�ߋ����O�Ȃ��ł�
:�v���Z�X�P�Əd�����Ȃ��悤�ɒ��ӂ��ĉ�����
%CMD% sm15261599
:%CMD% sm15172108
:�����������������������ȏ�̍s�̓v���Z�X�Q�̏������w�肵�ĉ������B��������

GOTO RUN_END
:RUN2END
:RUN_END
:����ł����B
echo.
echo ���ׂďI�����܂����B
Exit
:EOF
