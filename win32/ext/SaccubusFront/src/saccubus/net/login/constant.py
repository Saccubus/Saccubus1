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
ログイン時の定数など
'''
COOKIE_DOMAIN='.nicovideo.jp'
MYPAGE_URL="http://www.nicovideo.jp/my/top";
LOGIN_URL="https://secure.nicovideo.jp/secure/login?site=secniconico";
SESSION_PATH='/'
SESSION_NAME='user_session'
SESSION_PATTERN=r'user_session_[0-9]+_[0-9]+'
