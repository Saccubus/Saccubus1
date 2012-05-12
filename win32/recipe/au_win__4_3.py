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
	INFOBAR2で動作確認済み。他のAUのWIN端末でも動作すると思います。
	-flags bitexact -trell -aic → -flags +bitexact+aic            ■orz
'''

def cmdline(info):
	return "{ffmpeg} -y {sacc} -flags +bitexact+aic -vcodec libxvid -mbd 2 -s 320x240 -r 29.97 -b 768k -acodec libvo_aacenc -ac 2 -ar 44100 -ab 192k -f 3g2 -aspect 4:3 \"{resource_path}/{out}.3g2\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
