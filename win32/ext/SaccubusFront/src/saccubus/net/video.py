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

import os;
import urllib;
import sys
from ..resource.rule import formatVideoFilename

WATCH_PAGE_URL='http://www.nicovideo.jp/watch/{0}'

'''
infoとjarを用いて、指定したディレクトリに動画を保存する。
拡張子は適当に決定する。
取得したファイルの保存先をstrで返します。
'''
def downloadVideo(jar, play_info, meta_info, resDir):
	touchWatchPage(jar, meta_info['video_id'])
	resp = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar)).open(play_info['url'])
	#FIXME: 見苦しい。
	orig_fname = resp.info()['Content-Disposition'].split('filename=')[1].replace('"', '').replace("'", "");
	_, ext = os.path.splitext(orig_fname)
	fname = formatVideoFilename(meta_info['video_id'], meta_info['title'], ext);
	fname = os.path.join(resDir, fname)
	if os.path.exists(fname):
		os.remove(fname)
	fsize = int(resp.info()['Content-Length'])
	with open(fname,'wb') as file, resp:
		while 1:
			buf = resp.read(65536)
			if not buf:
				break
			file.write(buf)
			sys.stdout.flush()
			print("Now downloading... {0} of {1}bytes ({2}%)".format(file.tell(), fsize, file.tell()*100//fsize));
	return fname

'''
事前にwatchページを見ないと、DLできないらしいので
touchしておきます。
'''
def touchWatchPage(jar, video_id):
	resp = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar)).open(WATCH_PAGE_URL.format(video_id))
	resp.close();
