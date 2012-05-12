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

INFO_API_URL="http://flapi.nicovideo.jp/api/getflv/{0}?watch_harmful=1&as3=1"

import urllib;
from ..resource import rule;
import os;
'''
動画DLのための情報を取得する。
meta_indoより、もっと動的な情報が手に入る。
DLサーバや、メッセージサーバのURLなどが含まれる。
'''
def downloadPlayInfo(jar, videoid, resDir):
	url = INFO_API_URL.format(videoid);
	resp = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar)).open(url);
	file = os.path.join(resDir, rule.formatPlayInfoFilename(videoid));
	data = resp.read();
	with open(file, "wb") as f:
		f.write(data)
	content = data.decode('ascii')
	resp.close();
	return file, parsePlayInfo(content);


def parsePlayInfo(content):
	_info = dict( urllib.parse.parse_qsl(content, encoding='utf-8', errors='replace') )
	return _info