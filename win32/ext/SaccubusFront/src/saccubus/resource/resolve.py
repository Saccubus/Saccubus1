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

from saccubus.error import SaccubusError;
from . import rule;
import os;
from ..net import login, video, thread, meta_info, play_info;

'''
	オプションはタプルのシーケンスで渡してください。
	-video-id:<string>　解決する動画IDを渡してください
	-resource-path:<string> リソースの置いてある場所を指定
	-override-video: <string>([video_id]:[filename])　命名規則を無視したい場合に。（最後の引数優先）
	-override-thread: <string>([video_id]:[filename])　命名規則を無視したい場合に。（複数OK）
	-user: メールアドレス
	-password: パスワード
	-cookie: クッキー取得方法（デフォルト：own;　上記のIDとパスワードを用いる）
	-comment-back: コメント取得数　デフォルト：1000
'''
def fromNative(*opts):
	optDict = dict(opts);
	
	auth = {
		"user": optDict.get("user"),
		"password": optDict.get("password"),
		"cookie": optDict.get('cookie')
	}

	override_table = {'video':{}, 'thread':{}};
	if 'resource-path' not in optDict:
		raise SaccubusError("Invalid arguments! 'resource-path' is required.");
	if 'video-id' not in optDict:
		raise SaccubusError("Invalid arguments! 'video-id' is required.")
	for k,v in opts:
		if k=='override-video':
			vid, file = splitOverride(v);
			override_table['video'][vid]=file
		elif k=='override-thread':
			vid, file = splitOverride(v);
			if vid in override_table['thread']:
				override_table['thread'][vid].append(file)
			else:
				override_table['thread'][vid] = [file];
		else:
			pass
	commentOpt = {}
	commentOpt['comment-back'] = int(optDict.get("comment-back", 500));
	resolver = Resolver(optDict['resource-path'], auth, override_table);
	resolved = resolver.resolve(optDict['video-id']);
	resolved = resolver.download(optDict['video-id'], commentOpt, resolved);
	#TODO: Nativeへは、str->strの辞書しか返さない約束なので、ここで変換してしまう。ここでいいの？
	#見苦しい。
	resolved['thread'] = '\n'.join(resolved['thread']);
	return resolved;

def splitOverride(val):
	vid=val[:val.index(':')]
	file = val[val.index(':')+1:]
	return vid, file

class Resolver(object):
	'''
	ニコニコ動画の
	・動画
	・ユーザーコメント・投稿者コメント・オプショナルコメント
	・getflvで手に入る動画情報
	・thumbinfoで手に入る、タイトルなどの情報
	を集め、所定のフォルダに格納します。
	さきゅばす本体から呼ばれる他、GUIからも呼んでも可
	'''
	def __init__(self, resource_path, auth=dict(), override_table={'video':{}, 'thread':{}}):
		'''
		コンストラクタ。
		'''
		self.auth = auth;
		self.resource_path = resource_path;
		self.override_table = override_table;
		
		if not os.path.exists(self.resource_path):
			raise SaccubusError("Resource Path: {0} not exists!", self.resource_path);
		if not os.path.isdir(self.resource_path):
			raise SaccubusError("Resource Path: {0} is not a directory!", self.resource_path);
		
	def resolve(self, video_id):
		'''
		動画IDから、動画とコメントのファイルを解決して絶対パスを辞書で返します。
		'''
		files = filter(lambda f: f.startswith(video_id), os.listdir(self.resource_path))
		resolved = {'thread':[]};
		
		play_info_prefix=rule.formatPlayInfoFilename(video_id)
		meta_info_prefix=rule.formatMetaInfoFilename(video_id)
		video_prefix=rule.formatVideoPrefix(video_id)
		thread_prefix=rule.formatThreadPrefix(video_id)
		
		for fname in files:
			if fname.startswith(video_prefix):
				resolved['video'] = os.path.join(self.resource_path,fname);
				base, _ = os.path.splitext(fname);
				resolved['title'] = base[len(video_prefix):];
			elif fname.startswith(thread_prefix):
				resolved['thread'].append(os.path.join(self.resource_path,fname));
			elif fname == play_info_prefix:
				resolved['play_info'] = os.path.join(self.resource_path,fname);
			elif fname == meta_info_prefix:
				resolved['meta_info'] = os.path.join(self.resource_path,fname);
			pass
		if video_id in self.override_table['thread']:
			resolved['thread'] = self.override_table['thread'][video_id];
		if video_id in self.override_table['video']:
			resolved['video'] = self.override_table['video'][video_id];
		return resolved
	
	def download(self, video_id, commentOpt, resolved):
		no_video = 'video' not in resolved;
		no_thread = 'thread' not in resolved or len(resolved['thread']) <= 0;
		no_play_info = 'play_info' not in resolved;
		no_meta_info =  'meta_info' not in resolved;
		if no_video or no_thread or no_play_info or no_meta_info:
			#どれか一つでも足りないなら
			cjar = login.login(self.auth.get('user'), self.auth.get('password'), self.auth.get('cookie'))
			play_info_path, play_info_dic = play_info.downloadPlayInfo(cjar, video_id, self.resource_path);
			meta_info_path, meta_info_dic = meta_info.downloadMetaInfo(video_id, self.resource_path);
			resolved['play_info'] = play_info_path;
			resolved['meta_info'] = meta_info_path;
			if no_video:
				resolved['video'] = video.downloadVideo(cjar, play_info_dic, meta_info_dic, self.resource_path)
			if no_thread:
				resolved['thread'] = thread.downloadThreads(cjar, video_id, play_info_dic, commentOpt, self.resource_path)
		return resolved;
