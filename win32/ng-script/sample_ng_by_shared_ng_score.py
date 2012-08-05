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
	NG共有機能のスコアで無視するかどうかを決めるスクリプト
'''

'''
以下の大百科からNGスコアのリミット値をとってきました
http://dic.nicovideo.jp/a/ng%E5%85%B1%E6%9C%89%E6%A9%9F%E8%83%BD
'''
LIMIT_NONE=-9999999999999999
LIMIT_WEAK=-10000
LIMIT_NORMAL=-4800
LIMIT_STRONG=-1000

#ここで設定したリミットを使います
LIMIT=LIMIT_NORMAL #ノーマルなNG共有スコア

'''
上のホワイトリストとブラックリストを利用するNGフィルタです
'''
def shouldCommentBeIgnored(*arg):
	com = dict(arg) #おまじないです
	# NG共有スコアを取得します
	score = int(com["score"])
	# 指定したリミットよりスコアが低い（＝不愉快）コメントを無視するようにします
	return score < LIMIT


