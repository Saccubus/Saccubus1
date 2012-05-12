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
	return "{ffmpeg} -y {sacc} -threads 4 -vcodec libx264 -aspect 16:9 -s 640x480 -crf 21 -b 2000k -bt 2000k -maxrate 2000k -bufsize 2000k -acodec libvo_aacenc -ac 2 -ar 44100 -ab 128k -coder 1 -sws_flags lanczos -flags +loop -cmp +chroma -partitions +i4x4+p8x8+b8x8 -me_method umh -subq 8 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 2 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 3 -direct-pred 3 -trellis 1 -wpredp 1 -mixed-refs 1 -8x8dct 1 -fast-pskip 1 \"{resource_path}/{out}.mp4\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
