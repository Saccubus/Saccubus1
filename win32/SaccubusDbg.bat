@cd "%~dp0"
@call "%~dp0ext\init.bat"
@cls
@echo -------------------------------------------------------------------------------
@echo さきゅばす　実行開始
@echo -------------------------------------------------------------------------------

start /wait /b python.exe "%~dp0ext\SaccubusFront\src\launch.py" | tee "%~dp0__log__launcher.log"
