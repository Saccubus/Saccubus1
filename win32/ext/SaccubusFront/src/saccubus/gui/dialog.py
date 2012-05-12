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

class Dialog(tkinter.Toplevel):
	'''
	classdocs
	'''
	def __init__(self, master, cnf={}, **kw):
		'''
		Constructor
		'''
		tkinter.Toplevel.__init__(self, master, cnf, **kw)

	def moveToCenter(self):
		self.focus_set()
		self.grab_set()
		self.transient(self.master)
		self.update();
		relX = int((self.master.winfo_width()-self.winfo_width())/2)
		relY = int((self.master.winfo_height()-self.winfo_height())/2)
		self.geometry(
			"{w}x{h}+{x}+{y}".format(
				w=self.winfo_width(),
				h=self.winfo_height(),
				x=self.master.winfo_x()+relX,
				y=self.master.winfo_y()+relY
		));
