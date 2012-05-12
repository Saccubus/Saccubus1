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

from xml.dom import minidom;
import os;
import shutil;
import urllib
from ..resource import rule;

'''
コメントをDLします。
ファイル名の配列を返します。
'''
def downloadThreads(jar, video_id, play_info, commentOpt, resDir):
	files = [];
	if 'thread_id' in play_info:
		files.append(downloadThread(jar, video_id, play_info, 'thread_id', commentOpt, resDir))
	if 'optional_thread_id' in play_info:
		files.append(downloadThread(jar, video_id, play_info, 'optional_thread_id', commentOpt, resDir))
	return files;

def downloadThread(jar, video_id, play_info, thread_id_key, commentOpt, resDir):
	payload = constructCommand(jar, play_info, thread_id_key, commentOpt)
	resp = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar)).open(play_info['ms'], payload)
	fname = os.path.join(resDir, rule.formatThreadFilename(video_id, play_info[thread_id_key]))
	if os.path.exists(fname):
		os.remove(fname)
	with open(fname, 'wb') as f, resp:
		shutil.copyfileobj(resp, f)
	return fname;

THREAD_KEY_API_URL='http://flapi.nicovideo.jp/api/getthreadkey?thread={0}'
def getThreadKey(jar, thread_id):
	url=THREAD_KEY_API_URL.format(thread_id)
	resp = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar)).open(url)
	return dict(urllib.parse.parse_qsl(resp.read().decode('utf-8')))

def constructCommand(jar, play_info, thread_id_key, commentOpt):
	lst = minidom.NodeList();
	#デフォルトコメント
	th = minidom.Element('thread')
	th.setAttribute('thread', play_info[thread_id_key])
	th.setAttribute('version', '20090904')
	th.setAttribute('user_id', play_info['user_id'])
	th.setAttribute('scores', '1')
	lst.append(th)
	leave = minidom.Element('thread_leaves')
	leave.setAttribute('thread', play_info[thread_id_key])
	leave.setAttribute('user_id', play_info['user_id'])
	leave.setAttribute('scores', '1')
	txt=minidom.Text();
	txt.data = "0-{to}:100,{back}".format(back=str(commentOpt['comment-back']), to=str(int(play_info['l'])+59//60))
	leave.appendChild(txt)
	lst.append(leave)
	#投稿者コメント
	fth = minidom.Element('thread')
	fth.setAttribute('thread', play_info[thread_id_key])
	fth.setAttribute('version', '20061206')
	fth.setAttribute('res_from', '-1000')
	fth.setAttribute('fork', '1')
	fth.setAttribute('user_id', play_info['user_id'])
	fth.setAttribute('scores', '1')
	fth.setAttribute('click_revision', '-1')
	lst.append(fth)
	# スレッドキーが必要な場合は取得
	if 'needs_key' in play_info:
		key_dict = getThreadKey(jar, play_info[thread_id_key])
		for k,v in key_dict.items():
			th.setAttribute(k, v)
			fth.setAttribute(k, v)
			leave.setAttribute(k, v)
	return constructPacketPayload(lst)

'''
コマンドのリストから、取得用のPOSTデータを取得する
'''
def constructPacketPayload(commandNodes):
	doc = minidom.Document()
	packet = doc.createElement("packet");
	doc.appendChild(packet)
	for node in commandNodes:
		packet.appendChild(node)
	return doc.toxml('utf-8')

