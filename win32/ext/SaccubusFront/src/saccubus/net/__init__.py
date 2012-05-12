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
☆設計方針☆
Saccubus1系における、Clientクラスを作ってコンテキストを保持しておく方法だと、
グローバル変数と同じように、処理する順番を気にしなくてはならなくなり、バギーで複雑になってしまいました。
というわけで、今回は次のように実装します。
・すべての機能は、あくまでメソッドとして実装する。
　・各メソッドは、取得するのに最低限必要な情報を受取り、結果を返す。
　・各メソッドは、グローバルな状態を絶対に保持しない。（スタック上だけに確保する。）←特にこれ守って！！
'''
