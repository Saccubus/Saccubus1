
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
'''

def cmdline(info):
	return "{ffmpeg} -y {sacc} -flags +bitexact -vcodec libx264 -coder 1 -bufsize 128k -g 250 -s 480x272 -r 29.97 -qscale 2 -maxrate 3000k -acodec libvo_aacenc -ac 2 -ar 44100 -ab 192k -absf aac_adtstoasc -f psp -level 21 -aspect 16:9 -qcomp 0.7 -qmin 10 -qmax 51 -qdiff 4 -subq 6 -me_range 16 -i_qfactor 0.714286 \"{resource_path}/{out}.mp4\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
