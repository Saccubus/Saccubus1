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
from . import login;
from .login import util;
from . import play_info;
from .. import test_common;
import os;

class Test(unittest.TestCase):
	def setUp(self):
		self.jar = login.login(test_common.TEST_USER, test_common.TEST_PASS, 'own')
		self.assertTrue(util.isLoggedIn(self.jar));
	def tearDown(self):
		pass
	def testBasic(self):
		f, dic = play_info.downloadPlayInfo(self.jar, 'sm60', test_common.RESOURCE_DL_PATH)
		self.assertEquals(dic['thread_id'], '1173124005')
		self.assertEquals(dic['l'], '50')
		self.assertTrue(os.path.exists(f))
		pass


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()