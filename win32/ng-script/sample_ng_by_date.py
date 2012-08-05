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

'''
ここ三ヶ月のコメントだけ表示します
'''
def shouldCommentBeIgnored(*arg):
	com = dict(arg) #おまじないです
	# 現在の日付を得るために、timeモジュールを使用します
	import time
	# コメントの投稿された時間（1970年１月１日０時からのミリ秒）を取り出します
	comtime = int(com['date']);
	# 今日の時間を取得します
	today = time.time()
	# 三ヶ月（90日）前の時間を取得します。
	limit = today - (24*60*60*90)
	
	# 三ヶ月前より古いコメントを無視することで、最近三ヶ月のコメントだけ表示するようにします
	return comtime < limit


