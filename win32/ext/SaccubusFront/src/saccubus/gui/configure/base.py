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
from saccubus.gui.edit_menu import EditMenu
import tkinter.ttk
import tkinter.filedialog;
import pickle;
import os.path;
import re

EncodedSep = os.path.sep.encode('unicode_escape').decode('ascii')
def toNativePath(val):
	return re.sub(r'/', EncodedSep, val);

class ConfigurePanel(tkinter.ttk.Notebook):
	'''
	複数の設定項目のセクションをまとめてタブとして表示する
	'''
	def __init__(self, master):
		tkinter.ttk.Notebook.__init__(self, master)
	def serialize(self):
		'''
		設定を永続化するために、dictionaryを作る。
		子供はそのディクショナリに自分の設定項目を入れていく
		'''
		conf = {}
		for key in self.children:
			self.children[key].serialize(conf);
		return conf;
	def deserialize(self, conf):
		'''
		永続化されたディクショナリから、自分の設定項目をロードする。
		'''
		for key in self.children:
			self.children[key].deserialize(conf);
	def toArgument(self, lstDic):
		'''
		設定項目からコマンドライン引数を作る。Backend設定でのみ用いる。
		'''
		for key in self.children:
			self.children[key].toArgument(self, lstDic)
			
	def load(self, filename):
		'''
		ファイル名を指定して永続化されたディクショナリをロード
		'''
		conf={}
		if os.path.exists(filename):
			with open(filename, "rb") as f:
				conf = pickle.load(f)
		self.deserialize(conf);
	def save(self, filename):
		'''
		ファイル名を指定して永続化されたディクショナリを保存
		'''
		with open(filename, "wb") as f:
			conf = self.serialize()
			pickle.dump(conf, f, pickle.HIGHEST_PROTOCOL)
			return conf;

class ConfigureSectionPanel(tkinter.Frame):
	'''
	各設定項目のセクションのパネル一枚。
	'''
	def __init__(self, master, sectionTitle):
		tkinter.Frame.__init__(self, master)
		master.add(self, text=sectionTitle)
	def serialize(self, conf):
		for key in self.children:
			if hasattr(self.children[key], 'serialize'):
				self.children[key].serialize(conf);
	def deserialize(self, conf):
		for key in self.children:
			if hasattr(self.children[key], 'deserialize'):
				self.children[key].deserialize(conf);
	def toArgument(self, lstDic):
		for key in self.children:
			if hasattr(self.children[key], 'toArgument'):
				self.children[key].toArgument(lstDic);

class BaseConfigurePanel(tkinter.Frame):
	def __init__(self, master, title, desc, typeName, uniq):
		'''
		各設定項目一つ一つを表すクラスのベースクラス。
		ここではタイトルと説明を表示する。
		'''
		tkinter.Frame.__init__(self, master)
		self.typeName = typeName;
		self.uniq = uniq
		tkinter.Label(self, text=title, font=tkinter.font.BOLD).pack(fill=tkinter.X, expand=tkinter.NO);
		tkinter.Label(self, text=desc).pack(fill=tkinter.X, expand=tkinter.NO);
	def deploy(self):
		self.pack(fill=tkinter.X, expand=tkinter.NO, side=tkinter.TOP)
	def serialize(self, conf):
		if not self.typeName in conf:
			conf[self.typeName] = dict()
		try:
			conf[self.typeName][self.uniq] = self.cfgDump();
		except Exception as e:
			print("Error while serializing configure: ", e);
	def deserialize(self, conf):
		if not self.typeName in conf:
			conf[self.typeName] = dict()
		if self.typeName in conf and self.uniq in conf[self.typeName]:
			try:
				self.cfgLoad(conf[self.typeName][self.uniq]);
			except Exception as e:
				print("Error while deserializing configure: ", e);
	def toArgument(self, lstDic):
		val = self.cfg2Arg()
		if not val:
			return
		if not self.typeName in lstDic:
			lstDic[self.typeName] = []
		lstDic[self.typeName].extend(val)

	def cfgDump(self):
		raise Exception("Please implement");
	def cfgLoad(self, obj):
		raise Exception("Please implement");
	def cfg2Arg(self):
		raise Exception("Please implement");

'''
　以下、データタイプに合わせたBaseConfigurePanelのサブクラスが並ぶ。
'''

class StringConfigurePanel(BaseConfigurePanel):
	def __init__(self, master, title, desc, typeName, uniq, argname, default, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq)
		self.argname=argname;
		self.val = tkinter.StringVar(self, default)
		e = tkinter.Entry(self, textvariable=self.val, **kw)
		e.pack(fill=tkinter.X, expand=tkinter.NO)
		EditMenu(e)
	def cfgDump(self):
		return self.val.get()
	def cfgLoad(self, obj):
		self.val.set(obj);
	def cfg2Arg(self):
		val = self.val.get()
		if val:
			return (self.argname, str(self.val.get()))
		else:
			return ()

class IntegerConfigurePanel(BaseConfigurePanel):
	def __init__(self, master, title, desc, typeName, uniq, argname, default, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq)
		self.argname=argname;
		self.val = tkinter.IntVar(self, default)
		entry=tkinter.Entry(self, textvariable=self.val, **kw)
		entry.pack(fill=tkinter.X, expand=tkinter.NO)
		EditMenu(entry)
	def cfgDump(self):
		return self.val.get()
	def cfgLoad(self, obj):
		self.val.set(obj);
	def cfg2Arg(self):
		return (self.argname, str(self.val.get()))

class FileConfigurePanel(BaseConfigurePanel):
	OpenFile, SaveFile, Directory = range(3)
	def __init__(self, master, title, desc, typeName, uniq, openType, argname, default, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq)
		self.argname=argname;
		self.val = tkinter.StringVar(self, default)
		self.openType = openType;

		frame = tkinter.Frame(self)
		entry=tkinter.Entry(frame, textvariable=self.val, state='readonly', **kw)
		entry.pack(fill=tkinter.X, expand=tkinter.YES, side=tkinter.LEFT)
		tkinter.Button(frame, text="参照", command=self.onClicked).pack(expand=tkinter.NO, side=tkinter.LEFT)
		frame.pack(expand=tkinter.YES, fill=tkinter.X)
		EditMenu(entry)
	def onClicked(self):
		val=None
		if self.openType == FileConfigurePanel.OpenFile:
			val = tkinter.filedialog.askopenfilename();
		elif self.openType == FileConfigurePanel.SaveFile:
			val = tkinter.filedialog.asksaveasfilename();
		elif self.openType == FileConfigurePanel.Directory:
			val = tkinter.filedialog.askdirectory()
		else:
			pass
		if val:
			self.val.set(val)
	def cfgDump(self):
		return toNativePath(str(self.val.get()))
	def cfgLoad(self, obj):
		self.val.set(obj);
	def cfg2Arg(self):
		return (self.argname, toNativePath(str(self.val.get())))

class FileSelectConfigurePanel(BaseConfigurePanel):
	def __init__(self, master, title, desc, typeName, uniq, argname=None, defaultDir=None, defaultFile=None, emptyAllowed=False, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq)
		self.argname = argname;
		self.emptyAllowed = emptyAllowed;
		self.defaultFile = defaultFile
		
		self.dval = tkinter.StringVar(self, defaultDir)
		dframe = tkinter.Frame(self)
		dentry=tkinter.Entry(dframe, textvariable=self.dval, state='readonly', **kw)
		dentry.pack(fill=tkinter.X, expand=tkinter.YES, side=tkinter.LEFT)
		tkinter.Button(dframe, text="参照", command=self.onDirectorySelect).pack(expand=tkinter.NO, side=tkinter.LEFT)
		EditMenu(dentry)
		dframe.pack(expand=tkinter.YES, fill=tkinter.X)
		
		self.fval = tkinter.StringVar(self, self.defaultFile)
		fframe = tkinter.Frame(self)
		self.fbox = tkinter.ttk.Combobox(fframe, textvariable=self.fval, state='readonly', **kw)
		self.fbox.pack(fill=tkinter.X, expand=tkinter.YES, side=tkinter.LEFT)
		tkinter.Button(fframe, text="再読み込み", command=lambda *a:self.reloadDirectory()).pack(expand=tkinter.NO, side=tkinter.LEFT)
		fframe.pack(expand=tkinter.YES, fill=tkinter.X)
		self.reloadDirectory()
	def onDirectorySelect(self, *event):
		newDir = tkinter.filedialog.askdirectory();
		if not newDir:
			return
		if (not os.path.exists(newDir)) or (not os.path.isdir(newDir)):
			tkinter.messagebox.showerror('エラー', 'ディレクトリは存在しません')
			return
		self.dval.set(newDir)
		self.reloadDirectory()
	def reloadDirectory(self):
		if os.path.exists(self.dval.get()) and os.path.isdir(self.dval.get()):
			values = []
			if self.emptyAllowed:
				values.append('<指定しない>')
			files = []
			for item in os.listdir(self.dval.get()):
				if os.path.isfile(os.path.join(self.dval.get(), item)):
					files.append(item)
			files.sort();
			values.extend(files)
			self.fbox['values'] = values
			f = os.path.join(self.dval.get(), self.fval.get())
			if not os.path.exists(f) or not os.path.isfile(f):
				if len(values) > 0:
					if self.defaultFile in values:
						self.fbox.current(values.index(self.defaultFile))
					else:
						self.fbox.current(0)
				else:
					self.fval.set('')
			else:
				self.fbox.current(values.index(self.fval.get()))
		else:
			self.fbox['values'] = ("フォルダが存在しません。",)
			self.fbox.current(0)
	def cfgDump(self):
		return (self.dval.get(), toNativePath( self.fval.get() ) )
	def cfgLoad(self, obj):
		d,f = obj;
		self.dval.set(d);
		self.fval.set(f);
		self.reloadDirectory()
	def cfg2Arg(self):
		fpath = os.path.join(self.dval.get(), self.fval.get());
		if self.argname and os.path.exists(fpath) and os.path.isfile(fpath):
			return (self.argname,  toNativePath(fpath))
		else:
			return ()

class SelectionConfigurePanel(BaseConfigurePanel):
	def __init__(self, master, title, desc, typeName, uniq, choices, default, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq)
		self.val = tkinter.StringVar(self, default)

		box=tkinter.ttk.Combobox(self, textvariable=self.val, state='readonly', **kw)
		box.pack(fill=tkinter.X, expand=tkinter.NO)
		values=[]
		self.choices = {};
		idx=0;
		for i, choice in enumerate(choices):
			values.append(choice[0])
			self.choices[choice[0]] = choice[1:];
			if choice[0] == default:
				idx=i;
			i+=1
		box['values']=values;
		box.current(idx)
	def cfgDump(self):
		return self.val.get()
	def cfgLoad(self, obj):
		if obj in self.choices:
			self.val.set(obj);
	def cfg2Arg(self):
		return self.choices[self.val.get()]

class PluginConfigurePanel(BaseConfigurePanel):
	def __init__(self, master, title, desc, typeName, uniq, argname, plugins, default=None, **kw):
		BaseConfigurePanel.__init__(self, master, title, desc, typeName, uniq);
		self.val = tkinter.StringVar(self, default);
		self.argname = argname
		self.box = tkinter.ttk.Combobox(self, textvariable=self.val, state='readonly', **kw)
		self.box.pack(fill=tkinter.X, expand=tkinter.NO, side=tkinter.TOP)
		self.pluginPanel = ConfigurePanel(self)
		self.pluginPanel.pack(fill=tkinter.BOTH, expand=tkinter.YES, side=tkinter.TOP)
		self.plugins = {}
		self.nowFrame = None;
		for title, argval, klass in plugins:
			self.box.insert(tkinter.END, title)
			panel = klass(self.pluginPanel, title)
			self.plugins[title] = (argval, panel)
	def cfgDump(self):
		cfg = {}
		# すべての設定をちゃんと保存するため、子階層をつくる
		for title in self.plugins:
			cfg[title] = {}
			self.plugins[title][1].serialize(cfg[title])
		return cfg
	def cfgLoad(self, obj):
		for title in self.plugins:
			self.plugins[title][1].deserialize(obj.get(title, {}))
	def cfg2Arg(self):
		title = self.val.get()
		if title in self.plugins:
			return (self.argname, self.plugins[title][0])
	def toArgument(self, lstDic):
		# 自分のを加えてから
		BaseConfigurePanel.toArgument(self, lstDic);
		# 設定項目の引数を加える
		for title in self.plugins:
			self.plugins[title][1].toArgument(lstDic)
	def deploy(self):
		self.pack(fill=tkinter.BOTH, expand=tkinter.YES, side=tkinter.TOP)
