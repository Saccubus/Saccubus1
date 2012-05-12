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
from . import own;
from .error import LoginError;
from ... import test_common;

class Test(unittest.TestCase):


	def setUp(self):
		pass


	def tearDown(self):
		pass


	def testNone(self):
		try:
			own.login(None, None)
		except LoginError:
			pass
		except:
			self.fail("ログイン中に未知のエラーが発生")
		else:
			self.fail("(None, None)でログイン成功？")

	def testLogin(self):
		self.assertIsNotNone(
			own.login(test_common.TEST_USER,test_common.TEST_PASS),
			"正しいパスワードでもログインできません"
			)

	def testLoginFailure(self):
		try:
			own.login(test_common.TEST_USER,"__invalid_password__")
		except LoginError:
			pass
		except:
			self.fail("ログイン中に未知のエラーが発生")
		else:
			self.fail("間違ったパスワードでログインできちゃう？？")


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testLogin']
	unittest.main()