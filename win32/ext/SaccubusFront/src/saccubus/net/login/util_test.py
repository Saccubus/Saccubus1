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
from . import util;

class Test(unittest.TestCase):


	def setUp(self):
		pass


	def tearDown(self):
		pass


	def testReadSession(self):
		# TODO: お使いの環境のクッキーがありそうな場所を指定してください。
		jar = util.searchNicoSessionFrom(
			"/home/psi/.mozilla/firefox",
			"C:/Users/psi/AppData/Roaming/Mozilla/Firefox/Profiles/3whnmkfi.default",
			);
		self.assertIsNotNone(jar);
		self.assertTrue(util.isLoggedIn(jar))

if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()