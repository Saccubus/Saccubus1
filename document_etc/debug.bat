:Ver.1.1 作業フォルダをdebug.batの場所に変更する
:Ver.1.2 Javaのパスの設定を不要にする
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
echo javaw.exeが見つかりません。
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。
echo.
echo javaw.exeが見つかりません。>>%log%
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。>>%log%
echo.
pause
:eof
