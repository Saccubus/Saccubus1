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
	echo �A�b�v�f�[�^�̃R�s�[��
	xcopy /S /E /Y %~dp0updater %TEMP%\updater\
	echo ���̃Z�b�g�A�b�v
	call %TEMP%\updater\init.bat
	svn update
	echo �A�b�v�f�[�g���������܂����B
	pause

:exit
	exit