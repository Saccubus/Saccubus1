@echo off
echo -----------------------------------------------------------------
echo ２プロセス用「さきゅばす」自動変換バッチファイル    Ver.1.0
echo 　ガイダンス付き
echo 　マルチコアCPU、ブラウザ共有設定が必須です（多分）
echo -----------------------------------------------------------------

:この行に続く行は無視して結構です。
IF "%1"=="" GOTO Setup
IF NOT "%~1"=="0" GOTO RUN1START
:Setup
echo.
SET /P ANS=「さきゅばす」でオプション設定・ブラウザ共有設定をしましたか？[Y/N]
IF /I "%ANS%"=="Y" goto RUN0_START
echo.
ECHO オプション設定・ブラウザ共有設定をして下さい。
echo.
SET /P DMY=一度手動でダウンロードと変換を確認するのを推奨します。OK？
java -jar Saccubus.jar
:RUN0_START
echo.
SET /P ANS=%~nx0 を編集しましたか？[Y/N]
IF /I "%ANS%"=="Y" goto RUN0_END
echo.
SET /P DMY=%~nx0 を編集して下さい。OK？
EXIT
:RUN0_END
echo.
echo 途中で中断するには、コマンドプロンプト画面を終了後
echo タスクマネージャーからffmpegプロセス２つを終了させます。
echo.
echo マルチコアプロセッサー推奨です。
echo.
echo 終了後のシャットダウンの設定は出来ません。ご了承下さい。
if "%1"=="0" (
echo.
echo ログは auto2log_[1,2].txt に記録されます。
)
echo.
SET /P ANS=自動変換を実行します。[Y/N]？
IF /I "%ANS%"=="N" goto EOF
:RUN1START

:■■■■終了後にシャットダウンの設定は出来ません。ご了承下さい。
:set SHUTDOWN=NO
:■■■■↓メールアドレス↓ブラウザ共有設定済みなら、そのままでOK
set MAILADDR=doremi@mahodo.co.jp
:■■■■↓パスワード↓ブラウザ共有設定済みなら、そのままでOK
set PASSWORD=steeki_tabetai
:この行に続く行は無視して結構です。
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

:■■■■■■■■下記のいくつかの事項を変更して、ダブルクリック！ですよ。■

:以降、
:%CMD% <動画ID> <過去ログ>
:のような形で変換したい動画を指定して書いてください。過去ログは無くても可。
:もちろん何行でも可能です。
:以下は過去ログ有りの例
:%CMD% sm123456789 2011/7/1
:%CMD% sm123456789 "2011/1/1 10:10"

:■■■■■■■■↓↓↓以下の行はプロセス１の処理を指定して下さい。↓↓↓■
:記述例は2011/8/12のボカロランキング上位動画、過去ログなしです
:プロセス２と重複しないように注意して下さい
%CMD% sm15255755
:%CMD% sm15230821
:■■■■■■■■↑↑↑以上の行はプロセス１の処理を指定して下さい。↑↑↑■


:この行に続く行は無視して結構です。
GOTO RUN_END
:RUN1END
IF NOT "%~1"=="2" GOTO RUN2END

:■■■■■■■■↓↓↓以下の行はプロセス２の処理を指定して下さい。↓↓↓■
:記述例は2011/8/12のボカロランキング上位動画、過去ログなしです
:プロセス１と重複しないように注意して下さい
%CMD% sm15261599
:%CMD% sm15172108
:■■■■■■■■↑↑↑以上の行はプロセス２の処理を指定して下さい。↑↑↑■

GOTO RUN_END
:RUN2END
:RUN_END
:これでおわり。
echo.
echo すべて終了しました。
Exit
:EOF
