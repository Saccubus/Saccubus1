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

META_API_URL = "http://ext.nicovideo.jp/api/getthumbinfo/{0}"
import xml.dom.minidom;
import urllib;
from . import error;
from ..resource import rule;
import os;

__all__=['downloadMetaInfo']

'''
動画のメタ情報を取得し、ファイルを書き出す。
タイトルなどが含まれる
ファイル名と、パースした結果のタプルを返します
'''
def downloadMetaInfo(video_id, resDir):
	fname=os.path.join(resDir, rule.formatMetaInfoFilename(video_id))
	if os.path.exists(fname):
		dom = xml.dom.minidom.parse(fname).documentElement
		return fname, parseMetaInfo(dom, video_id)
	resp = urllib.request.urlopen(META_API_URL.format(video_id))
	data = resp.read();
	with open(fname, "wb") as f:
		f.write(data)
	dom = xml.dom.minidom.parseString(data).documentElement;
	resp.close();
	return fname, parseMetaInfo(dom, video_id)

'''
DOMを解析する
'''
def parseMetaInfo(dom, video_id):
	if dom.attributes['status'].value != 'ok':
		raise error.LoadError("Video {0} not found", video_id);
	thumb_node = dom.getElementsByTagName('thumb')[0];
	info = {};
	node = thumb_node._get_firstChild();
	for node in thumb_node.childNodes:
		if node.nodeType == node.ELEMENT_NODE and node.hasChildNodes():
			info[node.nodeName] = node.childNodes[0].data;
	return info;
