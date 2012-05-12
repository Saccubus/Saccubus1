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
import os.path;
from saccubus import test_common;
from . import firefox, constant;

class Test(unittest.TestCase):
	def setUp(self):
		pass
	def tearDown(self):
		pass
	def testReadDatabase(self):
		jar = firefox.readDatabase(os.path.join(test_common.PATH, "net", "login", "firefox.sqlite"));
		for cookie in jar:
			if cookie.name=="user_session" and cookie.domain==constant.COOKIE_DOMAIN:
				self.assertEqual("user_session_26735140_14681406081901241868", cookie.value);
				#self.assertTrue(util.isLoggedIn(jar));
				return
		self.fail("Failed to read fiurefox cookie!");


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()