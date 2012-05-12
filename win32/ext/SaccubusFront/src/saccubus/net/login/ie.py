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

from . import util, error;
import os;

'''
IEのクッキーでログインし、CookieJarを返す
読み取れなければエラーを送出
@param userid: 無視されます。
@param password: 無視されます。
'''
def login(userid, password):
	return readCookieFile(
			# in windows vista
			os.path.join(os.getenv("APPDATA", ''), "Microsoft","Windows","Cookies")
		);
	pass

'''
指定されたディレクトリ内からクッキーを読み取ります
'''
def readCookieFile(d):
	jar = util.searchNicoSessionFrom(d)
	if jar == None:
		raise error.LoginError("Could not Internet Explorer cookie.: {0}", str(d));
	return jar
