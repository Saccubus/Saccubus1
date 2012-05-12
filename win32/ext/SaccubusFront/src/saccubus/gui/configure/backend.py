#! python3
# -*- coding: utf-8 -*-
'''
	Copyright (C) 2012 psi

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''
BACKEND_SEP='#'

import tkinter
from saccubus.gui.configure.base import\
	ConfigurePanel, ConfigureSectionPanel,\
	SelectionConfigurePanel, StringConfigurePanel,\
	FileConfigurePanel, IntegerConfigurePanel, FileSelectConfigurePanel,\
	PluginConfigurePanel
import saccubus.gui.dialog

class UnconfigurablePluginConfigurePanel(ConfigureSectionPanel):
	def __init__(self, master, sectionTitle):
		ConfigureSectionPanel.__init__(self, master, sectionTitle)
		tkinter.Label(self, text="設定項目はありません").pack(fill=tkinter.BOTH, expand=tkinter.YES)

class BackendConfigurePanel(ConfigurePanel):
	def __init__(self, master):
		ConfigurePanel.__init__(self, master)
		resolveSection = ConfigureSectionPanel(self, "ダウンローダ")
		SelectionConfigurePanel(resolveSection, "ログイン方法", "ニコニコ動画へのログイン方法を指定します。", 'sacc', 'resolve-cookie', [
						('ユーザーID＆パスワード', "--resolve-cookie", "own"),
						('Firefox', "--resolve-cookie", "firefox"),
						('Chrome', "--resolve-cookie", "chrome"),
						('InternetExplorer', "--resolve-cookie", "ie")], None).deploy()
		StringConfigurePanel(resolveSection, "ユーザーID", "上でブラウザを選択した場合は入力しなくて大丈夫です。", "sacc", "resolve-user", "--resolve-user", "udon@example.com").deploy()
		StringConfigurePanel(resolveSection, "パスワード", "上でブラウザを選択した場合は入力しなくて大丈夫です。", "sacc", "resolve-password", "--resolve-password", "udonudon", show="*").deploy()

		FileConfigurePanel(resolveSection, "ダウンロード先", "動画のダウンロード先を指定します","sacc", "resolve-resource-path", FileConfigurePanel.Directory, "--resolve-resource-path", "./__download__").deploy()


		generalSection = ConfigureSectionPanel(self, "一般的な設定")
		FileConfigurePanel(generalSection, "FFmpegパス", "FFmpegの場所を指定します。", "ffmpeg", "ffmpeg-path", FileConfigurePanel.OpenFile, '-i', "ext/ffmpeg/bin/ffmpeg.exe").deploy()
		FileConfigurePanel(generalSection, "FFmpegフィルタパス", "FFmpegフィルタの場所を指定します。", "input", "adapterfile", FileConfigurePanel.OpenFile, '-i', "ext/Saccubus/Saccubus.dll").deploy()
		FileSelectConfigurePanel(generalSection, "変換レシピ", "変換に使うFFmpegオプションのレシピを指定します", "ffmpeg", "recipe", None, "./recipe", 'PC_default.py', False).deploy()

		SelectionConfigurePanel(generalSection, "ログレベル", "出力ログのログレベルを設定します", "sacc", 'log-level',[
						("トレース", "--trace"),
						("詳細", "--verbose"),
						("デバッグ", "--trace"),
						("情報", "--info"),
						("警告", "--warning"),
						("エラー", "--error"),
						],'警告').deploy()
		

		videoSection = ConfigureSectionPanel(self, "動画設定")
		IntegerConfigurePanel(videoSection, "横幅", "この縦幅・横幅に短辺を合わせて拡大されます。", "input-opt", "width", "-width", 640).deploy()
		IntegerConfigurePanel(videoSection, "縦幅", "この縦幅・横幅に短辺を合わせて拡大されます。", "input-opt", "height", "-height", 480).deploy()
		IntegerConfigurePanel(videoSection, "最低FPS", "このFPS以上になるように出力されます。\nコメントがかくかくする場合などにお試し下さい。", "input-opt", "minfps", "-minfps", 25).deploy()
		SelectionConfigurePanel(videoSection, "TASモード", "TASのように変換中に１フレームずつ操作できます。\nスペースキーで次のフレームです。", 'sacc', 'controll-mode', [
					('TASモードにしない', ),
					('TASモードにする', "--enable-tas")],None).deploy()

		commentSection = ConfigureSectionPanel(self, "コメント")
		IntegerConfigurePanel(commentSection, "コメント取得数", "コメント取得件数を指定します。", "sacc", "resolve-comment-back", "--resolve-comment-back", 500).deploy()
		FileSelectConfigurePanel(commentSection, "NGスクリプトファイル", "変換しないコメントを決定するスクリプトを指定します。","sacc", "ng-script", "--ng-script", "./ng-script", None, True).deploy()
		PluginConfigurePanel(commentSection, "コメント描画プラグイン", "コメント描画プラグインを選択します", "sacc", "comment-factory", "--plugin-font", [
					('シンプル', 'simple', UnconfigurablePluginConfigurePanel)
				], "シンプル").deploy()
		PluginConfigurePanel(commentSection, "コメント配置プラグイン", "コメント配置プラグインを選択します", "sacc", "comment-factory", "--plugin-deploy", [
					('シンプル', 'simple', UnconfigurablePluginConfigurePanel)
				], "シンプル").deploy()
		
		self.load()
	def load(self):
		ConfigurePanel.load(self, saccubus.gui.BackendConfigureFilename)
	def save(self):
		ConfigurePanel.save(self, saccubus.gui.BackendConfigureFilename)
	def toArgument(self, videoId):
		if not videoId:
			return []
		lstDic = {}
		for key in self.children:
			self.children[key].toArgument(lstDic);
		argList = [];
		argList.extend( ('-f', 'saccubus') )
		argList.extend( lstDic['input-opt'] )
		argList.append("-sacc")
		saccArg = list(lstDic['sacc']);
		saccArg.insert(0, videoId)
		argList.append( BACKEND_SEP.join(saccArg) )
		argList.extend(lstDic['input'])
		return argList;

class BackendConfigureWindow(saccubus.gui.dialog.Dialog):
	'''
	バックエンドの設定ダイアログです。
	'''
	def __init__(self, master, videoId=None):
		saccubus.gui.dialog.Dialog.__init__(self, master)
		self.saveFlag = tkinter.IntVar(self, 1)
		self.videoId = videoId;
		'''
		コンストラクタ
		'''
		self.geometry("360x540")
		self.protocol("WM_DELETE_WINDOW", lambda: self.destroy())
		if self.videoId:
			self.saveFlag.set(0)
			self.title("変換設定：{videoId}".format(videoId = self.videoId))
		else:
			self.title("バックエンド設定")
		
		'''
		設定項目パネル
		'''
		confPanel = BackendConfigurePanel(self)

		'''
		最後に配置
		'''
		confPanel.pack(expand=tkinter.YES, fill=tkinter.BOTH)
		self.confPanel = confPanel;
		self.initExitPanel()
		self.moveToCenter();

		self.conf = None
		self.argument = None;
	
	def initExitPanel(self):
		if self.videoId:
			vframe = tkinter.Frame(self)
			tkinter.Checkbutton(vframe, variable=self.saveFlag, text='これを以降のデフォルト設定にする').pack(expand=tkinter.NO, side=tkinter.LEFT)
			
			vframe.pack(expand=tkinter.NO, fill=tkinter.X)
		frame=tkinter.Frame(self)
		tkinter.Button(frame, text="　　OK　　", command=self.onOkButtonClicked).pack(expand=tkinter.YES, fill=tkinter.X, side=tkinter.LEFT)
		tkinter.Button(frame, text="キャンセル", command=self.onCancelButtonClicked).pack(expand=tkinter.YES, fill=tkinter.X, side=tkinter.LEFT)
		frame.pack(expand=tkinter.NO, fill=tkinter.X, side=tkinter.BOTTOM)
	def onCancelButtonClicked(self):
		self.destroy()
	def onOkButtonClicked(self):
		if self.saveFlag.get() != 0:
			self.confPanel.save()
		self.conf = self.confPanel.serialize()
		self.argument = self.confPanel.toArgument(self.videoId)
		self.destroy()
	
	def show(self):
		self.master.wait_window(self)
		return self.conf ,self.argument
