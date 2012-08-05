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
 サンプルNGスクリプト。
'''

def shouldCommentBeIgnored(*arg):
	com = dict(arg)
	'''
	さきゅばす２では、ユーザーがPythonスクリプトを用いることでNGコメントを柔軟に指定することができます。

	＜セキュリティに注意＞
	特にPythonにセキュリティ上の制限などは掛けていませんので、他人の書いたNGスクリプトなどを用いる時はご注意ください。
	'''

	'''
	以下の情報が使えます。
	'''
	int(com['thread']) #スレッドID
	int(com['date']) # コメント投稿日時（1970年1月1日からのミリ秒）
	int(com["no"]) # コメント番号
	float(com['vpos']) #動画内での投稿時間
	int(com["deleted"]) #コメントは削除済みか否か(1で削除済み）
	int(com["score"]) #NG共有スコア
	str(com["user_id"]) #ユーザーID
	str(com["mail"]) #コマンド欄の内容
	str(com["message"]) #メッセージ
	str(com["anonymity"]) == 'true' #匿名コメントならTrue
	str(com["leaf"]) == 'true' #よくわからない
	str(com["premium"]) == 'true' #プレミアム会員ならTrue
	str(com["fork"]) == 'true' #投稿者コメントならTrue

	'''
	サンプル：何も無視しない
	この関数がTrueを返すと、コメントが無視されます。Falseを返した場合は、無視されません。
	この場合、どんな場合もFalseを返しているので、コメントは一切無視されません
	'''
	return False
	
	
	
