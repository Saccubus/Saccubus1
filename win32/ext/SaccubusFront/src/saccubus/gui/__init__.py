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


__all__=[
		'FrontendConfigureFilename',
		'BackendConfigureFilename'
];
FrontendConfigureFilename="_cfg_frontend.bin";
BackendConfigureFilename="_cfg_backend.bin";

import pickle;
import os.path

def loadFrontendConfigure():
	conf = {};
	if os.path.exists(FrontendConfigureFilename):
		with open(FrontendConfigureFilename, "rb") as f:
			conf = pickle.load(f)
	return conf;
