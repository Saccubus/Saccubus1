@echo off
@cd /d %~dp0
:Ver.1.3 
echo ----------------------------------------------------
echo �u������΂��v�����ϊ��o�b�`�t�@�C�� 1.3
echo ----------------------------------------------------
:���L�̂������̎�����ύX���āA�_�u���N���b�N�I�ł���B

:���O�t�@�C���̃T�C�Y������1000������(12Mbyte���炢)�ɑ��₵�����Ƃ�����1������:���폜
:set logsize=10m

:�I����ɃV���b�g�_�E������HYES�Ȃ炷��A����ȊO�Ȃ炵�Ȃ��B
set SHUTDOWN=NO

:���[���A�h���X�ƃp�X���[�h�ݒ�@�u���E�U��񋤗L�̐ݒ�ς݂Ȃ炻�̂܂܂�OK
set MAILADDR=doremi@mahodo.co.jp
set PASSWORD=steeki_tabetai

:���̍s�͖������Č��\�ł��B
call :default

:�ȍ~�A�L�q�@(1)1.64�ȑO�Ɠ����@�܂��́@�L�q�@(2)�}���`�ϊ����s 
:�̂ǂ��炩���g���Ă��������B�s����:�̓R�����g�s�Ȃ̂łǂ��炩��:���폜�B���݂͋L�q(2)

:�L�q�@(1)
:%CMD% <����ID> <�ߋ����O>
:�̂悤�Ȍ`�ŕϊ�������������w�肵�ď����Ă��������B�ߋ����O�͖����Ă��B
:������񉽍s�ł��\�ł��B
:���́��̂悤����ID���w�肷��B�ߋ����O���g���ꍇ��000000��ύX
:%CMD% sm1 000000

:�L�q�@(2)
:���̂܂܂�OK�B�ߋ����O��000000��ύX�B
%CMD% auto?watch_harmful=1 000000 @PUP

:(2)�ł́A�����ɂ͓���ID�������Ȃ��� auto.txt �ɋL�q���Ă��������B
:����ID������1�s���Ƃɉ��s�ł��L�q

:��̍s�͕����s�������Ƃ��\�B
:���̏ꍇ2�s�ڈȍ~auto��auto2�Ȃ�(auto����n�܂�p����)�ƕύX����
:����ID���X�g��auto2.txt�ɂ��Ă��������B�g���q�ȊO�̕�������Ɏw�肵�܂��B

:(1)(2)�Ƃ�������΂��{�̂̐ݒ���g���܂��B

:����ł����B
echo ----------------------------------------------------
echo ���ׂďI�����܂����B
:�V���b�g�_�E������
if %SHUTDOWN%==YES shutdown -s -t 30 -c "������΂������ϊ��o�b�`�t�@�C��"
goto :exit

:default
set local>nul
set log=log.txt
for %%I in (java.exe) do @set java="%%~$PATH:I"
if not %java% == "" goto :javago
:Java8
set jhome=\Program Files\Java\jre8\bin;\Program Files (x86)\Java\jre8\bin;C:\Program Files\Java\jre8\bin;C:\Program Files (x86)\Java\jre8\bin
for %%I in (java.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
:Java7
set jhome=\Program Files\Java\jre7\bin;\Program Files (x86)\Java\jre7\bin;C:\Program Files\Java\jre7\bin;C:\Program Files (x86)\Java\jre7\bin
for %%I in (java.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
goto :error
:javago
echo %java%
echo %java% >>%log% 2>>&1
%java% -version
%java% -version >>%log% 2>>&1
set CMD=%java% -jar Saccubus.jar %MAILADDR% %PASSWORD%
exit /b

:error
echo java.exe��������܂���B
echo readmeNew.txt���Q�Ƃ���Java���C���X�g�[�����ăp�X��ݒ肵�����������B
echo.
echo java.exe��������܂���B>>%log%
echo readmeNew.txt���Q�Ƃ���Java���C���X�g�[�����ăp�X��ݒ肵�����������B>>%log%
echo.
:exit
pause
exit

:�@������΂� Character User Interface�@�ڍו�
:auto.bat�Ƃ͈Ⴄ�g�������ł��܂��B

:�@�@��{�̎g����
:�@�@(1)�@�@java -jar Saccubus.jar Mail@address.com password ����ID ���� �ǉ��I�v�V����
:�@�@(2)�@�@java -jar Saccubus.jar Mail@address.com password auto ���� �ǉ��I�v�V���� 
:�@�@�@(2)�̏ꍇ��auto.txt�ɓ���ID���X�g���w�� ������s�\
:�@�@auto.bat�̏ꍇ�@%CMD% sm9999 "2009/7/7 7:7"
:�@�@�@�����͏ȗ��\�A���݂̏ꍇ��0���w��

:�@�A�I�v�V�����t�@�C���̕ύX�i2pass�G���R�[�h�̗�j
:�@�@�@�ŏ���2pass�p�̃I�v�V�����t�@�C����1pass�ځA2pass�ڂ�2�g�i���A�X�y�N�g��2��j�p�ӂ���
:�@�@�@1pass�ڃt�@�C�����@[PC][4�F3].xml�@�@ [PC][16�F9].xml�@�@�@ �Ƃ�
:�@�@�@2pass�ڃt�@�C�����@p2[PC][4�F3].xml�@p2[PC][16�F9].xml�@�Ƃ��āip2�͕ύX�\�j
:�@�@auto.bat�̏ꍇ���̂悤��2�s�w�肷��
:�@�@�@%CMD% sm9999
:�@�@�@%CMD% sm9999 0 p2�@�@�@�@�@����(0)�͏ȗ��s�Ap2�̓I�v�V�����t�@�C���̐ړ���

:�@�B�ݒ�(saccubus.xml)�̃I�[�o�[���C�h
:�@�@�w����@�@key��=�I�[�o�[���C�h�l
:�@�@�@�ݒ�l���Ȃ����ꍇ��=�ŏI���B
:�@�@�I�[�o�[���C�h�\��key���i�p���ŊJ�n�j��saccubus.xml���Q�Ƃ��ĉ������B
:�@�@�@key���̗�@<entry key="key��">�ݒ�l</entry>�ƋL�q����Ă��܂��B
:�@�@�@�@FontPath�@�@�@�@�@�@�t�H���g�t�@�C���̃p�X�i%WINDIR%\Fonts\msgothic.ttc�j
:�@�@�@�@FontIndex �@�@�@�@�@�t�H���g�C���f�b�N�X�i1�j
:�@�@�@�@SaveVideoFile �@�@�@�����ۑ�����itrue�j
:�@�@�@�@SaveCommentFile �@�@�R�����g��ۑ�����itrue�j
:�@�@�@�@CMD_EXT �@�@�@�@�@�@���ړ��͎��̏]���̕ϊ���̊g���q�i.avi�j
:�@�@�@�@WideCMD_EXT �@�@�@�@���ړ��͎��̃��C�h�̕ϊ���̊g���q�i.mp4�j
:�@�@�@�@EnableCA�@�@�@�@�@�@CA�p�̃t�H���g�ɋ����ύX����ifalse�j
:�@�@�Ⴆ��sm8628149��CA�p�̃t�H���g�ɋ����ύX����ꍇ��
:�@�@auto.bat�̏ꍇ�@%CMD% sm8628149 0 EnableCA=true

:�@�CFFmpeg�̃I�v�V�����l�i-�ŊJ�n�j(�I�v�V����.xml���́j�̕ύX
:�@�@�w����@�@-�I�v�V����=�ݒ�l
:�@�@�Ⴆ�΁A�o�̓T�C�Y��1280x720�ɕύX����ꍇ��
:�@�@auto.bat�̏ꍇ�@%CMD% sm8628149 0 -s=1280x720
:�@�@ConvList�̏ꍇ�@sm8628149 0 -s=1280x720

:�@�D�g�ݍ��킹�@�A�B�C�͓����ɑg�ݍ��킹�Ďg�p�\
:�@�@�Ⴆ��sm9��2pass��2pass�ڂ͓���R�����g��ۑ��i�_�E�����[�h�j���Ȃ��ꍇ��
:�@�@auto.bat�̏ꍇ
:�@�@�@%CMD% sm9
:�@�@�@%CMD% sm9 0 p2 SaveVideoFile=false SaveCommentFile=false

:�@�E�T�Ԗڈȍ~�̈����i�ߋ����O�����̌�j�́��w��L�[���[�h�i���p�p�啶���j
:�@@NDL
:�@�@����E�R�����g���_�E�����[�h���Ȃ��B�i�ϊ��͐ݒ�t�@�C���ʂ�j
:�@@DLO 
:�@�@����E�R�����g�������_�E�����[�h���A�ϊ����s��Ȃ��B
:�@@DLC 
:�@�@�R�����g�݂̂������_�E�����[�h���A�ϊ����s��Ȃ��B
:�@@PUP
:�@�@PC��ʂ̍����auto.bat���~�p�̃{�^���E�X�e�[�^�X��\������B
:�@@SET=�ݒ�t�@�C���p�X.xml �i�C���j
:�@�@saccubus.xml�̑���ɐݒ�t�@�C��.xml���g�p����B
:�@@ADD=�ǉ��ݒ�t�@�C���p�X.xml �i�C���j
:�@�@�ǉ��ݒ�t�@�C���p�X.xml���t�@�C�����j���[�̒ǉ��Ŏw�肷��̂Ɠ���
:�@��jsm9��2pass��2pass�ڂ͓���R�����g��ۑ��i�_�E�����[�h�j���Ȃ��ꍇ��
:�@�@�I�v�V�����ݒ��2pass�p��1pass�ڎw��ŁA�t�@�C������
:�@�@�@1pass�ځ@�I�v�V����.xml�@�@2pass�ځ@p2�I�v�V����.xml�Ƃ����
:�@�@auto.bat�̏ꍇ
:�@�@�@%CMD% sm9 0 @PUP
:�@�@�@%CMD% sm9 0 @PUP p2 @NDL

:�@auto.bat���s���ɂ�log.txt���o�͂��܂�

:�@���܂�
:�@�R�}���h�v�����v�g�Ł@SCHTASKS /?�@�Ɠ��͂�����
:�@SCHTASKS���l�b�g�Ō������Ă݂Ă��������B�g�p�͎��ȐӔC��
:SCHTASKS /Create /SC daily /ST 02:10:00 /TN saccubus /TR C:\saccubus\saccubus\auto.bat
