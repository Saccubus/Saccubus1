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
from ..util.filename import escapeFilename;

__all__=[
	'formatPlayInfoPrefix',
	'formatMetaInfoPrefix',
	'formatVideoPrefix',
	'formatVideoFilename',
	'formatThreadPrefix'
	'formatThreadFilename'
];

'''
名前規則
・play_info(getflv)： dir/[video_id]_play_info.txt
・meta_info(getthumbinfo)： dir/[video_id]_meta_info.xml
・動画： dir/[video_id]_video_title<.ext>　→　注意、extは最初のドット込みで。
・コメント:dir/[video_id]_thread_<スレッドID><.ext>
'''
PLAY_INFO_FORMAT="{video_id}_play_info.txt"
META_INFO_FORMAT="{video_id}_meta_info.xml"

VIDEO_PREFIX="{video_id}_video_"
VIDEO_FORMAT=VIDEO_PREFIX+"{title}{ext}"

CONVERTED_PREFIX="{video_id}_conv_"
CONVERTED_FORMAT=CONVERTED_PREFIX+"{title}"

LOG_FORMAT="{video_id}__log__.log"

THREAD_PREFIX="{video_id}_thread_"
THREAD_FORMAT=THREAD_PREFIX+"{thread_id}.xml"

def formatPlayInfoFilename(video_id):
	return PLAY_INFO_FORMAT.format(video_id=video_id)
def formatMetaInfoFilename(video_id):
	return META_INFO_FORMAT.format(video_id=video_id)

def formatVideoPrefix(video_id):
	return VIDEO_PREFIX.format(video_id=video_id)
def formatVideoFilename(video_id, title, ext):
	return VIDEO_FORMAT.format(video_id=video_id, title=escapeFilename(title), ext=ext)

def formatConvertedFilenameBase(video_id, title):
	return CONVERTED_FORMAT.format(video_id=video_id, title=escapeFilename(title))

def formatLogFilename(video_id):
	return LOG_FORMAT.format(video_id=video_id)
def formatThreadPrefix(video_id):
	return THREAD_PREFIX.format(video_id=video_id)
def formatThreadFilename(video_id, thread_id):
	return THREAD_FORMAT.format(video_id=video_id, thread_id=thread_id)

