:Ver.1.1 作業フォルダをdebug.batの場所に変更する
@cd /d %~dp0
java -jar Saccubus.jar 2>&1 | java -cp Bin.jar Tee log.txt