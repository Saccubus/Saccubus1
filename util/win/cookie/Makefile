# Makefile ( n source file 1 execute file version )
# 2002/12/ 4

# 日本語のコメント部分に従って修正して下さい。
# $(HOGEHOGE) というのはマクロ（一種の変数）で、
# HOGEHOGE=... で定義された内容に展開して解釈されます。
# 行頭のタブには意味がありますので、スペースに変換しないで下さい。
# できれば、最終的に日本語コメントは削除して下さい。

#	 ↓パッケージ名かつ実行ファイル名に書き換える
PACKAGE	= cookie
#	 ↓ソースファイルを書き並べる
SRCS	= cookie.c
#	 ↓ヘッダファイルを書き並べる
HEADS	= 
OBJS	= $(SRCS:.c=.o) $(PACKAGE)_res.o

#	 ↓パッケージに含めるファイル名を書き並べる
FILES	= Makefile $(HEADS) $(SRCS)
VER	= `date +%Y%m%d`


### command and flags ###
# uncomment when debugging
#DEBUG	= -ggdb -pg # -lefence
RELEASE = -s

# common (*.o)
LD	= gcc
LDFLAGS	= -g $(DEBUG) $(RELEASE)
LDLIBS	= -lm -lmingw32 # -mwindows

# C (*.c)
CC	= gcc
CFLAGS	= -g -O3 -Wall $(DEBUG) $(RELEASE)
CPPFLAGS= -I.

# C++ (*.cc)
CXX	= g++
CXXFLAGS= -g -O3 -Wall $(DEBUG)

# Fortran77 (*.f)
FC	= f77
FFLAGS	= -Wall $(DEBUG)

# Pascal (*.p)
PC	= pc
PFLAGS	= -Wall $(DEBUG)

# etc
SHELL	= /bin/sh
RM	= rm -f
PROF	= gprof


### rules ###

.SUFFIXES:
.SUFFIXES: .o .c .cc .f .p

all: $(PACKAGE)

$(PACKAGE): $(OBJS)
	$(LD) $(LDFLAGS) $(OBJS) -o $@ $(LDLIBS)

$(OBJS): $(HEADS) Makefile

.c.o:
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@
.cc.o:
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) -c $< -o $@
.f.o:
	$(FC) $(FFLAGS) -c $< -o $@
.p.o:
	$(PC) $(PFLAGS) $(CPPFLAGS) -c $< -o $@

$(PACKAGE)_res.o: $(PACKAGE).rc
	windres -i $(PACKAGE).rc -o $(PACKAGE)_res.o

### useful commands ###

clean:
	$(RM) $(PACKAGE) $(OBJS)
	$(RM) core gmon.out *~ #*#

tar:
	@echo $(PACKAGE)-$(VER) > .package
	@$(RM) -r `cat .package`
	@mkdir `cat .package`
	@ln $(FILES) `cat .package`
	tar cvf - `cat .package` | gzip -9 > `cat .package`.tar.gz
	@$(RM) -r `cat .package` .package

zip:
	zip -9 $(PACKAGE)-$(VER).zip $(FILES)


prof: run
	$(PROF) $(PACKAGE) | less

run: all
#	 ↓サンプルデータで実行するように書き直す
#	    $(PACKAGE) は実行ファイル名に展開される
#	    行頭はタブ（スペースではない）
	./$(PACKAGE) < sample-data | less
