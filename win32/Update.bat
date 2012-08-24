@echo off
echo さきゅばすのアップデートを行いますか？[yN]

set /p choice=
set choice=%choice:y=Y%
if "%choice%" == "y" (
	goto update
) else if "%choice%" == "Y" (
	goto update
) else (
	goto exit
)
:update
	echo アップデータのコピー中
	xcopy /S /E /Y %~dp0updater %TEMP%\updater\
	echo 環境のセットアップ
	call %TEMP%\updater\init.bat
	svn update
	echo アップデートが完了しました。
	pause

:exit
	exit