@echo off
@cd /d %~dp0
:Ver.1.3 
echo ----------------------------------------------------
echo 「さきゅばす」自動変換バッチファイル 1.3
echo ----------------------------------------------------
:下記のいくつかの事項を変更して、ダブルクリック！ですよ。

:ログファイルのサイズ制限を1000万文字(12Mbyteくらい)に増やしたいとき↓の1文字目:を削除
:set logsize=10m

:終了後にシャットダウンする？YESならする、それ以外ならしない。
set SHUTDOWN=NO

:メールアドレスとパスワード設定　ブラウザ情報共有の設定済みならそのままでOK
set MAILADDR=doremi@mahodo.co.jp
set PASSWORD=steeki_tabetai

:この行は無視して結構です。
call :default

:以降、記述法(1)1.64以前と同じ　または　記述法(2)マルチ変換実行 
:のどちらかを使ってください。行頭の:はコメント行なのでどちらかの:を削除。現在は記述(2)

:記述法(1)
:%CMD% <動画ID> <過去ログ>
:のような形で変換したい動画を指定して書いてください。過去ログは無くても可。
:もちろん何行でも可能です。
:この↓のよう動画IDを指定する。過去ログを使う場合は000000を変更
:%CMD% sm1 000000

:記述法(2)
:↓のままでOK。過去ログは000000を変更。
%CMD% auto?watch_harmful=1 000000 @PUP

:(2)では、ここには動画IDを書かないで auto.txt に記述してください。
:動画IDだけを1行ごとに何行でも記述

:上の行は複数行書くことも可能。
:その場合2行目以降autoをauto2など(autoから始まる英数字)と変更して
:動画IDリストもauto2.txtにしてください。拡張子以外の部分を上に指定します。

:(1)(2)ともさきゅばす本体の設定を使います。

:これでおわり。
echo ----------------------------------------------------
echo すべて終了しました。
:シャットダウンする
if %SHUTDOWN%==YES shutdown -s -t 30 -c "さきゅばす自動変換バッチファイル"
goto :exit

:default
set local>nul
set log=log.txt
for %%I in (java.exe) do @set java="%%~$PATH:I"
if not %java% == "" goto :javago
:Java8
set jhome=\Program Files\Java\jre8\bin;\Program Files (x86)\Java\jre8\bin;C:\Program Files\Java\jre8\bin;C:\Program Files (x86)\Java\jre8\bin
for %%I in (java.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
:Java7
set jhome=\Program Files\Java\jre7\bin;\Program Files (x86)\Java\jre7\bin;C:\Program Files\Java\jre7\bin;C:\Program Files (x86)\Java\jre7\bin
for %%I in (java.exe) do @set java="%%~$jhome:I"
if not %java% == "" goto :javago
goto :error
:javago
echo %java%
echo %java% >>%log% 2>>&1
%java% -version
%java% -version >>%log% 2>>&1
set CMD=%java% -jar Saccubus.jar %MAILADDR% %PASSWORD%
exit /b

:error
echo java.exeが見つかりません。
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。
echo.
echo java.exeが見つかりません。>>%log%
echo readmeNew.txtを参照してJavaをインストールしてパスを設定したください。>>%log%
echo.
:exit
pause
exit

:　さきゅばす Character User Interface　詳細編
:auto.batとは違う使い方もできます。

:　①基本の使い方
:　　(1)　　java -jar Saccubus.jar Mail@address.com password 動画ID 日時 追加オプション
:　　(2)　　java -jar Saccubus.jar Mail@address.com password auto 日時 追加オプション 
:　　　(2)の場合はauto.txtに動画IDリストを指定 並列実行可能
:　　auto.batの場合　%CMD% sm9999 "2009/7/7 7:7"
:　　　日時は省略可能、現在の場合は0を指定

:　②オプションファイルの変更（2passエンコードの例）
:　　　最初に2pass用のオプションファイルを1pass目、2pass目と2組（ｘアスペクト比2種）用意する
:　　　1pass目ファイル名　[PC][4：3].xml　　 [PC][16：9].xml　　　 とし
:　　　2pass目ファイル名　p2[PC][4：3].xml　p2[PC][16：9].xml　として（p2は変更可能）
:　　auto.batの場合次のように2行指定する
:　　　%CMD% sm9999
:　　　%CMD% sm9999 0 p2　　　　　日時(0)は省略不可、p2はオプションファイルの接頭辞

:　③設定(saccubus.xml)のオーバーライド
:　　指定方法　key名=オーバーライド値
:　　　設定値をなくす場合は=で終わる。
:　　オーバーライド可能なkey名（英字で開始）はsaccubus.xmlを参照して下さい。
:　　　key名の例　<entry key="key名">設定値</entry>と記述されています。
:　　　　FontPath　　　　　　フォントファイルのパス（%WINDIR%\Fonts\msgothic.ttc）
:　　　　FontIndex 　　　　　フォントインデックス（1）
:　　　　SaveVideoFile 　　　動画を保存する（true）
:　　　　SaveCommentFile 　　コメントを保存する（true）
:　　　　CMD_EXT 　　　　　　直接入力時の従来の変換後の拡張子（.avi）
:　　　　WideCMD_EXT 　　　　直接入力時のワイドの変換後の拡張子（.mp4）
:　　　　EnableCA　　　　　　CA用のフォントに強制変更する（false）
:　　例えばsm8628149をCA用のフォントに強制変更する場合は
:　　auto.batの場合　%CMD% sm8628149 0 EnableCA=true

:　④FFmpegのオプション値（-で開始）(オプション.xml内の）の変更
:　　指定方法　-オプション=設定値
:　　例えば、出力サイズを1280x720に変更する場合は
:　　auto.batの場合　%CMD% sm8628149 0 -s=1280x720
:　　ConvListの場合　sm8628149 0 -s=1280x720

:　⑤組み合わせ　②③④は同時に組み合わせて使用可能
:　　例えばsm9を2passで2pass目は動画コメントを保存（ダウンロード）しない場合は
:　　auto.batの場合
:　　　%CMD% sm9
:　　　%CMD% sm9 0 p2 SaveVideoFile=false SaveCommentFile=false

:　⑥５番目以降の引数（過去ログ日時の後）の＠指定キーワード（半角英大文字）
:　@NDL
:　　動画・コメントをダウンロードしない。（変換は設定ファイル通り）
:　@DLO 
:　　動画・コメントを強制ダウンロードし、変換を行わない。
:　@DLC 
:　　コメントのみを強制ダウンロードし、変換を行わない。
:　@PUP
:　　PC画面の左上にauto.bat中止用のボタン・ステータスを表示する。
:　@SET=設定ファイルパス.xml （修正）
:　　saccubus.xmlの代わりに設定ファイル.xmlを使用する。
:　@ADD=追加設定ファイルパス.xml （修正）
:　　追加設定ファイルパス.xmlをファイルメニューの追加で指定するのと同じ
:　例）sm9を2passで2pass目は動画コメントを保存（ダウンロード）しない場合は
:　　オプション設定を2pass用の1pass目指定で、ファイル名が
:　　　1pass目　オプション.xml　　2pass目　p2オプション.xmlとすると
:　　auto.batの場合
:　　　%CMD% sm9 0 @PUP
:　　　%CMD% sm9 0 @PUP p2 @NDL

:　auto.bat実行時にはlog.txtを出力します

:　おまけ
:　コマンドプロンプトで　SCHTASKS /?　と入力したり
:　SCHTASKSをネットで検索してみてください。使用は自己責任で
:SCHTASKS /Create /SC daily /ST 02:10:00 /TN saccubus /TR C:\saccubus\saccubus\auto.bat
