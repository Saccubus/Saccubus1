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
from . import meta_info;
from . import error;
from .. import test_common;
import os;
class Test(unittest.TestCase):
	def testSucceed(self):
		f, t = meta_info.downloadMetaInfo('sm60', test_common.RESOURCE_DL_PATH);
		self.assertTrue(os.path.exists(f))
		self.assertEqual(t['title'], "なに勘違いしているんだ")
	
	def testFailure(self):
		try:
			f, t = meta_info.downloadMetaInfo('sm0', test_common.RESOURCE_DL_PATH);
		except error.LoadError:
			pass
		except:
			self.fail("未知のエラーが発生")
		else:
			self.fail("sm0は存在しないのに、取得できてしまった => {0}:{1}".format(f,t))


if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testName']
	unittest.main()