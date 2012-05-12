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
import unittest
from .resolve import Resolver, fromNative;
from saccubus import test_common;
from saccubus.error import SaccubusError;
import os.path;

class Test(unittest.TestCase):
	def setUp(self):
		self.resource_path=os.path.join(test_common.PATH, "resources");
		pass


	def tearDown(self):
		pass


	def testInvalidArg(self):
		try:
			Resolver();
		except TypeError:
			pass
		else:
			self.fail("解決パスを示していないのに例外が発生しない")
		try:
			Resolver("/tmp/__not_exist__");
		except SaccubusError:
			pass
		else:
			self.fail("存在しないパスを指定しているのに例外が発生しない")
		try:
			Resolver(self.resource_path)
		except SaccubusError as e:
			self.fail("例外が発生: {0}".format(e.what()))
		except :
			self.fail("未知の例外")
		else:
			pass

	def testBaseResolver(self):
		resolv = Resolver(self.resource_path)
		dic = resolv.resolve("sm0");
		
		self.assertEqual(dic['video'], os.path.join(self.resource_path,"sm0_video_test.mp4"), "動画の取得に失敗しています")
		self.assertEqual("test", dic['title'], "タイトルの取得に失敗しています");
		self.assertEqual(1, len(dic['thread']), "スレッドの数がおかしいです。{0}個ありますが、一個のはずです".format(len(dic['thread'])));
		self.assertEqual(dic['thread'][0], os.path.join(self.resource_path, "sm0_thread_123456.xml"), "スレッドの取得に失敗")
		self.assertEqual(dic['play_info'], os.path.join(self.resource_path,"sm0_play_info.txt"));
		self.assertEqual(dic['meta_info'], os.path.join(self.resource_path,"sm0_meta_info.xml"));

	def testNativeBasic(self):
		opt = [];
		opt.append(('resource-path', self.resource_path))
		opt.append(('video-id', 'sm0'))
		opt.append(('comment-back', '500'))
		dic = fromNative(*opt)
		self.assertEqual(dic['video'], os.path.join(self.resource_path,"sm0_video_test.mp4"), "動画の取得に失敗しています")
		self.assertEqual("test", dic['title'], "タイトルの取得に失敗しています");
		self.assertEqual(1, len(dic['thread'].split('\n')), "スレッドの数がおかしいです。{0}個ありますが、一個のはずです".format(len(dic['thread'])));
		self.assertEqual(dic['thread'].split('\n')[0], os.path.join(self.resource_path, "sm0_thread_123456.xml"), "スレッドの取得に失敗")
		self.assertEqual(dic['play_info'], os.path.join(self.resource_path,"sm0_play_info.txt"));
		self.assertEqual(dic['meta_info'], os.path.join(self.resource_path,"sm0_meta_info.xml"));
	
	def testNativeOverride(self):
		opt = [];
		opt.append(('resource-path', self.resource_path))
		opt.append(('video-id', 'sm0'))
		opt.append(('override-video', 'sm0:noodle'))
		opt.append(('override-thread', 'sm0:udon'))
		opt.append(('override-thread', 'sm0:soba'))
		dic = fromNative(*opt)
		self.assertEqual(dic['video'], 'noodle', "動画のオーバーライドに失敗しています")
		self.assertListEqual(dic['thread'].split('\n'), ['udon', 'soba'], "スレッドのオーバーライドに失敗しています")

	def testDownload(self):
		auth = {'user': test_common.TEST_USER, 'password': test_common.TEST_PASS, 'cookie':'own'}
		resolv = Resolver(self.resource_path, auth)
		dic = resolv.resolve("sm60");
		dic = resolv.download("sm60", {'comment-back': 1000}, dic)
		self.assertTrue('thread' in dic)
		self.assertTrue('video' in dic)
		self.assertTrue('meta_info' in dic)
		self.assertTrue('play_info' in dic)
if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()