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

#FIXME: 明らかにこういうのはシステムに任せるべき処理なのだけど、APIが無いのだから仕方ない
FILENAME_FROM = '!?:;/\|,*"><'
FILENAME_TO = '！？：；／＼｜，＊”＞＜'
TRANS_TABLE=str.maketrans(FILENAME_FROM, FILENAME_TO)
# FIXME: WindowsはUnicodeに対応してる、でもFFmpeg(MinGW)は対応してない。
#import sys;
FILENAME_ENCODING='ms932'#sys.getfilesystemencoding();

def escapeFilename(_str):
	_bstr = _str.encode(encoding=FILENAME_ENCODING, errors='ignore')
	_str = _bstr.decode(encoding=FILENAME_ENCODING, errors='ignore')
	return _str.translate(TRANS_TABLE)

