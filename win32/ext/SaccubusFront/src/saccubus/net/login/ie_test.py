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

#from ... import test_common;
#from . import ie, constant;
#import os;

class Test(unittest.TestCase):
	def testReadCookieTest(self):
		'''
		jar = ie.readCookieFile(os.path.join(test_common.path, "net", "login", "ie"))
		for cookie in jar:
			if cookie.name=="user_session" and cookie.domain==constant.COOKIE_DOMAIN:
				self.assertEqual("user_session_26735140_19439488701445715695", cookie.value);
				return
		self.fail("Failed to read IE cookie!");
		'''


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()