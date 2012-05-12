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
from . import thread, play_info;
from . import login;
from .. import test_common;
from xml.dom import minidom;
import os;

OFFICIAL_VIDEO='1318394714' #よーあけーまえのーくらやみがー
VIDEO_ID="sm14097905"

class Test(unittest.TestCase):
	jar = None;
	defInfo = None;
	offInfo = None;
	def setUp(self):
		if Test.jar is None:
			Test.jar = login.login(test_common.TEST_USER, test_common.TEST_PASS, 'own')
		if Test.defInfo is None:
			_, Test.defInfo = play_info.downloadPlayInfo(Test.jar, VIDEO_ID, test_common.RESOURCE_DL_PATH)
		if Test.offInfo is None:
			_, Test.offInfo = play_info.downloadPlayInfo(Test.jar, OFFICIAL_VIDEO, test_common.RESOURCE_DL_PATH)
	def tearDown(self):
		pass
	def testDownloadThread(self):
		files = thread.downloadThreads(Test.jar, OFFICIAL_VIDEO, Test.offInfo, {'comment-back': 1000}, test_common.RESOURCE_DL_PATH)
		for f in files:
			self.assertTrue(os.path.exists(f))
			self.assertTrue(os.path.isfile(f))
			os.remove(f)
		
	def testOfficialThread(self):
		fname = thread.downloadThread(Test.jar, OFFICIAL_VIDEO, Test.offInfo, 'thread_id', {'comment-back': 1000}, test_common.RESOURCE_DL_PATH);
		self.assertTrue(os.path.exists(fname))
		self.assertTrue(os.path.isfile(fname))
		os.remove(fname)
	def testGetThreadKey(self):
		key_dict = thread.getThreadKey(Test.jar, OFFICIAL_VIDEO);
		self.assertIsNotNone(key_dict['threadkey'])
		self.assertNotEquals(key_dict['threadkey'], '')
	def testGetNormalThread(self):
		fname = thread.downloadThread(Test.jar, VIDEO_ID, Test.defInfo, 'thread_id', {'comment-back': 1000}, test_common.RESOURCE_DL_PATH);
		self.assertTrue(os.path.exists(fname))
		self.assertTrue(os.path.isfile(fname))
		os.remove(fname)
	def testConstructCommand(self):
		payload = thread.constructCommand(Test.jar, Test.defInfo, 'thread_id', {'comment-back': 1000},)
		self.assertEqual(
			payload,
			b'<?xml version="1.0" encoding="utf-8"?><packet><thread scores="1" thread="1302222473" user_id="26735140" version="20090904"/><thread_leaves scores="1" thread="1302222473" user_id="26735140">0-99:100,1000</thread_leaves><thread click_revision="-1" fork="1" res_from="-1000" scores="1" thread="1302222473" user_id="26735140" version="20061206"/></packet>'
			)
	def testConstructPacketPayload(self):
		th = minidom.Element("thread");
		th.setAttribute("thread", "1")
		lst = minidom.NodeList()
		lst.append(th);
		payload = thread.constructPacketPayload(lst);
		self.assertEqual(b'<?xml version="1.0" encoding="utf-8"?><packet><thread thread="1"/></packet>', payload)

if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()