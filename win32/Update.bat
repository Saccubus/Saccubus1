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
	call %~dp0updater\init.bat
	svn update
	echo アップデートが完了しました。
	pause

:exit
	exit