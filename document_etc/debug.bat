:Ver.1.1 ��ƃt�H���_��debug.bat�̏ꏊ�ɕύX����
:Ver.1.2 Java�̃p�X�̐ݒ��s�v�ɂ���
@echo off
set local>nul
cd /d %~dp0
set log=log.txt

:patch20161105
PATH %~dp0;%PATH%
PATH
:default
for %%I in (javaw.exe) do @set java="%%~$PATH:I"
if not %java% == "" goto :javago
:Java8
set jhome=\Program Files\Java\jre8\bin;\Program Files (x86)\Java\jre8\bin;C:\Program Files\Java\jre8\bin;C:\Program Files (x86)\Java\jre8\bin
for %%I in (javaw.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
:Java7
set jhome=\Program Files\Java\jre7\bin;\Program Files (x86)\Java\jre7\bin;C:\Program Files\Java\jre7\bin;C:\Program Files (x86)\Java\jre7\bin
for %%I in (javaw.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
goto :error

:javago
echo %java%
echo %java% >%log% 2>&1
%java% -version
%java% -version >>%log% 2>>&1
:saccubusgo
%java% -jar Saccubus.jar 2>&1 | %java% -cp Bin.jar Tee -a %log%
goto :eof
:error
echo javaw.exe��������܂���B
echo readmeNew.txt���Q�Ƃ���Java���C���X�g�[�����ăp�X��ݒ肵�����������B
echo.
echo javaw.exe��������܂���B>>%log%
echo readmeNew.txt���Q�Ƃ���Java���C���X�g�[�����ăp�X��ݒ肵�����������B>>%log%
echo.
pause
:eof
