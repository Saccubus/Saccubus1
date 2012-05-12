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

import tkinter.font;
import saccubus.gui.dialog;

class VersionInfoWindow(saccubus.gui.dialog.Dialog):
	'''
	バージョン情報などを表示します。
	'''
	def __init__(self, master, cnf={}, **kw):
		saccubus.gui.dialog.Dialog.__init__(self, master, cnf, **kw)
		self.title("バージョン情報")
		self.protocol("WM_DELETE_WINDOW", lambda: self.destroy())
		self.resizable(False, False)
		
		self.panel = self.initPanel(self)
		self.panel.pack(fill=tkinter.BOTH, expand=tkinter.YES)
		
		self.exitPanel = self.initExitPanel(self)
		self.moveToCenter();
	
	def initPanel(self, master):
		panel = tkinter.Frame(master)
		titleLabel = tkinter.Label(panel, text="さきゅばす", font=tkinter.font.Font(size=36,weight="bold"));
		versionLabel = tkinter.Label(panel, text="version: 2.0");
		authorLabel = tkinter.Label(panel, text="""Developed by
PSI 2007〜
orz 2011〜""");
		panel.columnconfigure(0, weight=1)
		titleLabel.grid(column=0, row=0, sticky=tkinter.W+tkinter.E);
		versionLabel.grid(column=0, row=1, sticky=tkinter.W+tkinter.E);
		authorLabel.grid(column=0, row=2, sticky=tkinter.W+tkinter.E);
		return panel;
	
	def initExitPanel(self, master):
		panel=tkinter.Frame(master)
		tkinter.Button(panel, text="OK", command=lambda *a:self.destroy()).pack(expand=tkinter.YES, fill=tkinter.X, side=tkinter.LEFT)
		panel.pack(expand=tkinter.NO, fill=tkinter.X)
		return panel;
	