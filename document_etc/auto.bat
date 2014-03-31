@echo off
@cd /d %~dp0
:Ver.1.2 Javaのパスの設定を不要にする
echo ----------------------------------------------------
echo 「さきゅばす」自動変換バッチファイル 1.2
echo ----------------------------------------------------
:下記のいくつかの事項を変更して、ダブルクリック！ですよ。

:終了後にシャットダウンする？YESならする、それ以外ならしない。
set SHUTDOWN=NO

:メールアドレスとパスワード設定　ブラウザ情報共有の設定済みならそのままでOK
set MAILADDR=doremi@mahodo.co.jp
set PASSWORD=steeki_tabetai

:この行は無視して結構です。
call :default

:以降、
:%CMD% <動画ID> <過去ログ>
:のような形で変換したい動画を指定して書いてください。過去ログは無くても可。
:もちろん何行でも可能です。

%CMD% sm1 000000

:これでおわり。
echo ----------------------------------------------------
echo すべて終了しました。
:シャットダウンする
if %SHUTDOWN%==YES shutdown -s -t 30 -c "さきゅばす自動変換バッチファイル"
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
echo java.exeが見つかりません。
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。
echo.
echo java.exeが見つかりません。>>%log%
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。>>%log%
echo.
:exit
pause
