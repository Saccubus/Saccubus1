@echo off
echo =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
echo ������΂������A�b�v�f�[�^
echo                                             ver 2012/08/24
echo -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
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
	echo ===========================================================
	echo �A�b�v�f�[�^�̃R�s�[��
	xcopy /S /E /Y %~dp0updater %TEMP%\updater\
	echo ===========================================================
	echo �A�b�v�f�[�^�̏�����
	call %TEMP%\updater\init.bat
	echo ===========================================================
	echo �A�b�v�f�[�g�J�n
	svn update
	echo ===========================================================
	echo �A�b�v�f�[�g���������܂����B
	pause

:exit
	exit
