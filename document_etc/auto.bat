@echo off
echo ----------------------------------------------------
echo �u������΂��v�����ϊ��o�b�`�t�@�C��
echo ----------------------------------------------------
:���L�̂������̎�����ύX���āA�_�u���N���b�N�I�ł���B

:�I����ɃV���b�g�_�E������HYES�Ȃ炷��A����ȊO�Ȃ炵�Ȃ��B
set SHUTDOWN=NO

:���[���A�h���X
set MAILADDR=doremi@mahodo.co.jp
:�p�X���[�h
set PASSWORD=steeki_tabetai

:���̍s�͖������Č��\�ł��B
set CMD=java -jar Saccubus.jar %MAILADDR% %PASSWORD%

:�ȍ~�A
:%CMD% <����ID> <�ߋ����O>
:�̂悤�Ȍ`�ŕϊ�������������w�肵�ď����Ă��������B�ߋ����O�͖����Ă��B
:������񉽍s�ł��\�ł��B

%CMD% sm1 000000

:����ł����B
echo ----------------------------------------------------
echo ���ׂďI�����܂����B
:�V���b�g�_�E������
if %SHUTDOWN%==YES shutdown -s -t 30 -c "������΂������ϊ��o�b�`�t�@�C��"
pause
