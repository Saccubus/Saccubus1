@echo off
echo ������΂��̃A�b�v�f�[�g���s���܂����H[yN]

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
	echo �A�b�v�f�[�g���������܂����B
	pause

:exit
	exit