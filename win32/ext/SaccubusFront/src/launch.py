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
import sys
import os
import saccubus.gui.main_window.window

if __name__ == "__main__":
	if len(sys.argv) > 1:
		#fd = os.open(sys.argv[1], os.O_WRONLY|os.O_CREAT)
		#os.dup2(fd, sys.stdout.fileno())
		#os.dup2(fd, sys.stderr.fileno())
		logger = open(sys.argv[1], "w", encoding="utf-8")
		sys.stdout = logger
		sys.stderr = logger
		print("log redirected to: "+sys.argv[1]);
		sys.stdout.flush()
	print("Launching Saccubus......");
	sys.stdout.flush()
	saccubus.gui.main_window.window.MainWindow().mainloop()
