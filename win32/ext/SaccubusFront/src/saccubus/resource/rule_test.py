#! python3
# -*- coding: utf-8 -*-
'''
Created on 2012/04/19

@author: psi
'''
import unittest
from . import rule;

class Test(unittest.TestCase):


	def setUp(self):
		pass


	def tearDown(self):
		pass


	def testVideoRule(self):
		self.assertEqual(rule.formatVideoPrefix("sm0"), "sm0_video_");
		self.assertEqual(rule.formatVideoFilename("sm0", "あ", ".mp4"), "sm0_video_あ.mp4");
	
	def testThreadRule(self):
		self.assertEqual(rule.formatThreadPrefix("sm0"), "sm0_thread_")
		self.assertEqual(rule.formatThreadFilename("sm0", 123456), "sm0_thread_123456.xml")

	def testMetaRule(self):
		self.assertEqual(rule.formatMetaInfoFilename("sm0"), "sm0_meta_info.xml")

	def testPlayRule(self):
		self.assertEqual(rule.formatPlayInfoFilename("sm0"), "sm0_play_info.txt")

if __name__ == "__main__":
	#import sys;sys.argv = ['', 'Test.testVideoRule']
	unittest.main()