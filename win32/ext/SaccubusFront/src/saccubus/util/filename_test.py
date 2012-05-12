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
from . import filename;

class Test(unittest.TestCase):


	def setUp(self):
		pass


	def tearDown(self):
		pass


	def testName(self):
		self.assertEqual("abc", filename.escapeFilename("abc"));
		self.assertEqual("__", filename.escapeFilename("_♥_"));
		self.assertEqual("_＼_", filename.escapeFilename('_\_'));
		self.assertEqual(filename.FILENAME_TO, filename.escapeFilename(filename.FILENAME_FROM));


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()