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

import tkinter
from saccubus.gui.configure.base import ConfigurePanel, IntegerConfigurePanel, ConfigureSectionPanel
import saccubus.gui.dialog

class FrontendConfigurePanel(ConfigurePanel):
	def __init__(self, master):
		ConfigurePanel.__init__(self, master)
		generalSection = ConfigureSectionPanel(self, "一般的な設定")
		IntegerConfigurePanel(generalSection, "同時変換数", "同時に変換する動画数を指定します。", "frontend", "task-limit", None,1).deploy()
		
		
		#設定項目のロード
		self.load()
	def toArgument(self, videoId):
		return ()
	def load(self):
		ConfigurePanel.load(self, saccubus.gui.FrontendConfigureFilename)
	def save(self):
		ConfigurePanel.save(self, saccubus.gui.FrontendConfigureFilename)

class FrontendConfigureWindow(saccubus.gui.dialog.Dialog):
	'''
	フロントエンドの設定です。変換手順を決定するオプションファイルの指定などを行います。
	'''
	def __init__(self, master):
		saccubus.gui.dialog.Dialog.__init__(self, master)
		'''
		コンストラクタ
		'''
		self.geometry("360x480")
		self.protocol("WM_DELETE_WINDOW", lambda: self.destroy())
		self.title("フロントエンド設定")

		'''
		設定項目パネル
		'''
		confPanel = FrontendConfigurePanel(self)
		'''
		配置
		'''
		confPanel.pack(expand=tkinter.YES, fill=tkinter.BOTH)
		self.confPanel = confPanel;
		self.initExitPanel()
		self.moveToCenter();
	
	def initExitPanel(self):
		frame=tkinter.Frame(self)
		tkinter.Button(frame, text="　　OK　　", command=self.onOkButtonClicked).pack(expand=tkinter.YES, fill=tkinter.X, side=tkinter.LEFT)
		tkinter.Button(frame, text="キャンセル", command=lambda *a: self.destroy()).pack(expand=tkinter.YES, fill=tkinter.X, side=tkinter.LEFT)
		frame.pack(expand=tkinter.NO, fill=tkinter.X)
	
	def onOkButtonClicked(self):
		self.confPanel.save()
		self.destroy()
