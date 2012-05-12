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
　デフォルトの設定です。可もなく不可もなくってとこ。
'''

def cmdline(info):
	return "{ffmpeg} -y {sacc} -sameq -aspect 4:3 \"{resource_path}/{out}.flv\"".format(ffmpeg=info['ffmpeg-file'], sacc=info['saccubus-opts'], resource_path=info['resource-path'], out=info['out-file-base'])
