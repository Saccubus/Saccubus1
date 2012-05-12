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
	return "{ffmpeg} -y {sacc} -threads 0 -vcodec libx264 -coder 1 -sws_flags lanczos -s 480x270 -r 29970/1001 -aspect 16:9 -crf 21 -b:v 256k -bt 5120k -me_range 32 -sc_threshold 85 -bf 2 -direct-pred 3 -partitions i4x4+p8x8+b8x8 -refs 3 -bufsize 2000k -g 300 -qcomp 0.8 -subq 7 -qmin 18 -qmax 34 -qdiff 4 -maxrate 1152k -flags +loop+mv4+mv0 -mixed-refs 0 -wpredp 1 -mbtree 1 -qns 3 -b_strategy 1 -direct-pred 3 -i_qoffset 1.40 -i_qfactor 0.71 -b_qoffset 1 -b_qfactor 1.30 -trellis 2 -qsquish 1 -mbd rd -bidir_refine 4 -deblock 0:0 -cmp chroma -subcmp 3 -keyint_min 30 -me_method 4 -nr 100 -async 1000 -acodec libvo_aacenc -ac 2 -ar 44100 -ab 96k -absf aac_adtstoasc -f psp -level 13 \"{resource_path}/{out}.mp4\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
