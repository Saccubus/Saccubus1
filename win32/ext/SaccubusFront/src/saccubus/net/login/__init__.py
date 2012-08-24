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

from . import chrome, firefox, own, ie;
from .util import isLoggedIn;
from ..error import SaccubusError;

'''
ニコニコ動画にログインし、CookieJarを返します。
ログインに失敗した場合はLoginErrorを送出します。
'''
def login(userid, password, method=None):
	jar = login_impl(userid, password, method);
	if not isLoggedIn(jar):
		raise SaccubusError("Failed to login with {0}. Maybe browser's cookie was already outdated.".format(method));
	return jar;

def login_impl(userid, password, method):
	if method == None:
		for _method in LOGIN_METHOD:
			try:
				return LOGIN_METHOD[_method](userid, password)
			except:
				pass
			raise SaccubusError("Failed to login.");
	else:
		if method not in LOGIN_METHOD:
			raise ValueError("Unknwon login method: {0}".format(method));
		else:
			return LOGIN_METHOD[method](userid, password)

'''
クッキーを取得する各メソッド。
シグネチャ：login(userid, password) => CookieJar
注意：　メソッド内でグローバル変数は使わない。
'''
LOGIN_METHOD={
	"ie": ie.login,
	"firefox": firefox.login,
	"chrome": chrome.login,
	"own": own.login
};

__all__=[
	'login',
];
