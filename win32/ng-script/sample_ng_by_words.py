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
	NGワードでコメントをNGするかどうか決めるサンプルスクリプト
'''

# このキーワードが入ってる場合は、無視しないようにしてみましょう。
WHITE_LIST=[
	"好き",
	"最高",
]

# このキーワードが入っている場合は、無視しましょう。
BLACK_LIST=[
	"嫌い",
	"最悪"
]

'''
上のホワイトリストとブラックリストを利用するNGフィルタです
'''
def shouldCommentBeIgnored(*arg):
	com = dict(arg) #おまじないです

	# コメント本文を取り出します
	msg = str(com["message"]) #メッセージ
	
	# ホワイトリストの処理
	for white in WHITE_LIST: #WHITE_LISTの中のキーワードについて順番に処理します
		if msg.find(white) >= 0: #ホワイトリストのキーワードが入っているなら…
			return False #Falseを返す＝無視しない　ようにします。ブラックリストのキーワードについては考慮しません。
	# ブラックリストの処理
	for black in BLACK_LIST: #今度はBLACK_LIST内のキーワードについて調べます。
		if msg.find(black) >= 0: #ブラックリスト内のキーワードが入っているなら…
			return True #Trueを返す＝無視する　ようにします
	# どちらも含まれていないその他のキーワードは、Falseを返す＝無視しないことにします。
	return False


