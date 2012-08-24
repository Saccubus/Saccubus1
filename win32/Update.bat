@echo off
echo =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo さきゅばす自動アップデータ
echo                                             ver 2012/08/24
echo -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
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
	echo ===========================================================
	echo アップデータのコピー中
	xcopy /S /E /Y %~dp0updater %TEMP%\updater\
	echo ===========================================================
	echo アップデータの初期化
	call %TEMP%\updater\init.bat
	echo ===========================================================
	echo アップデート開始
	svn update
	echo ===========================================================
	echo アップデートが完了しました。
	pause

:exit
	exit
