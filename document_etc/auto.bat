@echo off
@cd /d %~dp0
:Ver.1.2 Java�̃p�X�̐ݒ��s�v�ɂ���
echo ----------------------------------------------------
echo �u������΂��v�����ϊ��o�b�`�t�@�C�� 1.2
echo ----------------------------------------------------
:���L�̂������̎�����ύX���āA�_�u���N���b�N�I�ł���B

:�I����ɃV���b�g�_�E������HYES�Ȃ炷��A����ȊO�Ȃ炵�Ȃ��B
set SHUTDOWN=NO

:���[���A�h���X�ƃp�X���[�h�ݒ�@�u���E�U��񋤗L�̐ݒ�ς݂Ȃ炻�̂܂܂�OK
set MAILADDR=doremi@mahodo.co.jp
set PASSWORD=steeki_tabetai

:���̍s�͖������Č��\�ł��B
call :default

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
goto :exit

:default
set local>nul
set log=log.txt
for %%I in (java.exe) do @set java="%%~$PATH:I"
if not %java% == "" goto :javago
:Java7
set jhome=\Program Files\Java\jre7\bin;\Program Files (x86)\Java\jre7\bin;C:\Program Files\Java\jre7\bin;C:\Program Files (x86)\Java\jre7\bin
for %%I in (java.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
:Java8
set jhome=\Program Files\Java\jre8\bin;\Program Files (x86)\Java\jre8\bin;C:\Program Files\Java\jre8\bin;C:\Program Files (x86)\Java\jre8\bin
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
