@echo off
IF "%*"=="" (
AUTO2PROC.BAT 0
EXIT
)
%1 %2 | java -cp Bin.jar Tee auto2log_%2.txt
EXIT
