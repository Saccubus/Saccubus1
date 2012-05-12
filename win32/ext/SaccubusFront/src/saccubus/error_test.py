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
from .error import SaccubusError;

class Test(unittest.TestCase):
	def setUp(self):
		pass
	def tearDown(self):
		pass
	def testThrow(self):
		try:
			raise SaccubusError("Test");
		except SaccubusError as e:
			self.assertEqual(str(e), "Test", "Error messages must be equal.");
		except:
			self.fail("Unkown exception caught.")
		else:
			self.fail("No exception caught.")
	def testFormat(self):
		try:
			raise SaccubusError("Test {0}", "FMT");
		except SaccubusError as e:
			self.assertEqual(str(e), "Test FMT", "Error messages must be equal.");
		except:
			self.fail("Unkown exception caught.")
		else:
			self.fail("No exception caught.")


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()