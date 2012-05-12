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

import tkinter;

class EditMenu(tkinter.Menu):
	def __init__(self, master):
		'''
		メニューを作成し、右クリックのハンドリングも行います。
		'''
		tkinter.Menu.__init__(self, master, tearoff=False);
		self.add_command(label="切り取り (Ctrl+X)", command=lambda *a:master.event_generate("<<Cut>>"))
		self.add_command(label="コピー(Ctrl+C)", command=lambda *a:master.event_generate("<<Copy>>"))
		self.add_command(label="貼り付け (Ctrl+V)", command=lambda *a:master.event_generate("<<Paste>>"))
		master.bind('<Button-3>', lambda event: self.post(event.x_root,event.y_root))
		# FIXME: 全部選択がうまくいかない
		# master.bind('<Control-Key-a>', lambda event: master.select_range ( 0, tkinter.END ))
		

