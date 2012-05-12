@cd %~dp0
@call %~dp0ext\init.bat
@cls
start /B pythonw.exe %~dp0ext\SaccubusFront\src\launch.py %~dp0__log__launcher.log
