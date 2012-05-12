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
import re
from saccubus.gui.edit_menu import EditMenu;
from saccubus.gui.main_window.version_info import VersionInfoWindow;
from saccubus.gui.main_window.convert_list import ConvertList;
from saccubus.gui.configure.frontend import FrontendConfigureWindow
from saccubus.gui.configure.backend import BackendConfigureWindow
import saccubus.gui

class MainWindow(tkinter.Tk):
	'''
	メインウィンドウです。
	'''
	def __init__(self):
		cfg = saccubus.gui.loadFrontendConfigure()
		'''
		Constructor
		'''
		tkinter.Tk.__init__(self)
		self.geometry("360x480")
		self.title("Saccubus");
		self.configure(menu=self.initMenu(self))
		
		mainFrame = tkinter.ttk.Frame(self);
		videoAddPanel = self.initVideoAddPanel(self)
		statusbar = self.initStatusBar(self);

		convertList = ConvertList(self, mainFrame, cfg);
		convertList.pack(fill=tkinter.BOTH, expand=tkinter.YES)

		#配置
		self.columnconfigure(0, weight=1)
		self.rowconfigure(1, weight=1)
		videoAddPanel.grid(column=0, row=0, sticky=tkinter.W+tkinter.E);
		mainFrame.grid(column=0, row=1, sticky=tkinter.W + tkinter.N+tkinter.E + tkinter.S);
		statusbar.grid(column=0, row=2, sticky=tkinter.W);

		self.convertList = convertList;
		self.statusbar = statusbar;
		self.setStatus("Initialized.")
	
	def setStatus(self, msg):
		self.statusbar['text']=msg;
	
	def initVideoAddPanel(self, master):
		panel = tkinter.ttk.Frame(master)
		panel.columnconfigure(1, weight=1)
		tkinter.ttk.Label(panel, text="動画を変換：").grid(column=0, row=0)

		scrollbar = tkinter.Scrollbar(panel)
		scrollbar.grid(column=2, row=0, sticky=tkinter.S+tkinter.N)
		videoIdText=tkinter.Text(panel, height=3)
		videoIdText.grid(column=1, row=0, sticky=tkinter.W + tkinter.E)
		videoIdText.config(yscrollcommand=scrollbar.set)
		scrollbar.config(command=videoIdText.yview)

		EditMenu(videoIdText)
		tkinter.ttk.Button(panel, text="変換", command=lambda: self.onConvertButtonClicked(videoIdText)).grid(column=3, row=0, sticky=tkinter.S + tkinter.N)
		return panel;
	
	def initStatusBar(self, master):
		statusbar = tkinter.ttk.Label(master);
		statusbar['text']="status"
		return statusbar;
	
	def initMenu(self, master):
		menuRoot = tkinter.Menu(master);
		
		menuFile = tkinter.Menu(menuRoot);
		menuRoot.add_cascade(label="ファイル", menu=menuFile);
		menuFile.add_command(label="終了", command=exit)
		
		menuConfig = tkinter.Menu(menuRoot)
		menuRoot.add_cascade(label="設定", menu=menuConfig);
		menuConfig.add_command(label="フロントエンド設定", command=self.onFrontendConfigMenuClicked)
		menuConfig.add_command(label="バックエンド設定", command=self.onBackendConfigMenuClicked)
		
		menuHelp = tkinter.Menu(menuRoot)
		menuRoot.add_cascade(label="ヘルプ", menu=menuHelp)
		menuHelp.add_command(label="バージョン情報", command=self.onVersionInfoMenuClicked)
		
		return menuRoot;
	def onFrontendConfigMenuClicked(self):
		self.wait_window(FrontendConfigureWindow(self));
		cfg = saccubus.gui.loadFrontendConfigure()
		self.convertList.reloadConfig(cfg)
	def onBackendConfigMenuClicked(self):
		self.wait_window(BackendConfigureWindow(self));
	def onVersionInfoMenuClicked(self):
		self.wait_window(VersionInfoWindow(self));
	def onConvertButtonClicked(self, widget):
		videoIds = []
		rawVideoIdStr = widget.get(1.0, tkinter.END)
		rawVideoIds = re.split("[\n\r]*", rawVideoIdStr.strip())
		for videoId in rawVideoIds:
			match = re.match("(?:http://www.nicovideo.jp/watch/)?([0-9a-zA-Z]+)", videoId)
			if videoId and match:
				videoIds.append(match.group(1));
			else:
				self.setStatus("無効な動画ID,もしくはURLです： "+videoId)
				return
		self.convertList.registTasksFromUser(videoIds)
		widget.delete(1.0, tkinter.END)
		
	def mainloop(self):
		tkinter.Tk.mainloop(self);
