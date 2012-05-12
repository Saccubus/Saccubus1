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
import urllib;
import http;
from .error import LoginError;
from .constant import COOKIE_DOMAIN, LOGIN_URL;

def login(userid, password):
	auth = {
		"mail":userid,
		"password":password
	};
	jar = http.cookiejar.CookieJar()
	authPayload = urllib.parse.urlencode(auth).encode('utf-8')
	request = urllib.request.Request(LOGIN_URL, authPayload)
	opener = urllib.request.build_opener(
		urllib.request.HTTPCookieProcessor(jar),
		)
	try:
		resp = opener.open(request)
	except urllib.error.HTTPError:
		raise LoginError("Failed to login Nicovideo. Please check your Email and password.");
	resp.close()
	for cookie in jar:
		if cookie.domain == COOKIE_DOMAIN:
			return jar
	raise LoginError("Failed to login Nicovideo.");
