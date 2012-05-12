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

import sqlite3;
from .error import LoginError;
from . import constant;
import http.cookiejar;
import os;

def login(userid, password):
	
	return searchProfile(
		# in windows vista
		os.path.join(os.getenv('APPDATA', ''), 'Mozilla','Firefox','Profiles'),
		# in fedora 16
		os.path.join(os.getenv('HOME', ''), '.mozilla','firefox')
	);

def searchProfile(*prof_dirs):
	for d in prof_dirs:
		if os.path.isdir(d) and os.path.exists(d):
			for pdir in os.listdir(d):
				print("searching. ", pdir);
				fcookie = os.path.join(d, pdir, 'cookies.sqlite')
				if os.path.isfile(fcookie):
					try:
						return readDatabase(fcookie)
					except LoginError:
						pass
				print("could not found ", fcookie);
	raise LoginError("Could not find firefox cookie in {0}".format(repr(prof_dirs)));

'''
データベースファイルを読んで、データが存在すればクッキジャーに変換して返す。
読み取れなければエラーを送出。
@param fname: sqliteデータベースへのパス
'''
def readDatabase(fname):
	jar = http.cookiejar.CookieJar();
	con = sqlite3.connect(fname)
	con.row_factory = sqlite3.Row
	try:
		cur = con.execute('select * from moz_cookies where host=?', [constant.COOKIE_DOMAIN])
		rowcount=0;
		for item in cur:
			rowcount+=1;
			cookie = http.cookiejar.Cookie(
					0,
					item['name'],
					item['value'],
					None,
					False,
					item['host'],
					item['host'].startswith('.'),
					item['host'].startswith('.'),
					item['path'],
					False,
					item['isSecure']!=0,
					int(item['expiry']),
					False,
					None,
					None,
					{});
			jar.set_cookie(cookie);
		if rowcount <= 0:
			raise LoginError("Failed to read firefox Cookie! => {0}", cur.rowcount);
		return jar;
	finally:
		con.close()
	
