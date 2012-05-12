#! python3
# -*- coding: utf-8 -*-
'''
	Copyright (C) 2012 orz, psi

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

'''
	Wizpy 09_03_21変換設定 ※音声再変換しない版	 fps、qscale、音声等お好みで弄って下さい。
'''

def cmdline(info):
	return "{ffmpeg} -y {sacc} -s 160x128 -acodec copy -vcodec libxvid -r 20 -qscale 8 -flags +bitexact -threads 0 -aspect 5:4 \"{resource_path}/{out}.mp4\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
