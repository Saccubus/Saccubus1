■SDL_gfx_Makefile
SDL1.2用のSDL_gfx(SDL_gfx-2.0.26.tar.gz)用のMakefile
※prefix/CFLAGS/LIBS の d/home/workspace/SDL は各自のインストールフォルダーに合わせて修正してください

■SDL2_gfx_Makefile
SDL2.x用のSDL_gfx(SDL2_gfx-1.0.4.tar.gz)用のMakefile
※prefix/CFLAGS/LIBS の /d/home/sdl2 は各自のインストールフォルダーに合わせて修正してください

いずれもMakefileにrenameしてからmakeしてください。

■winapifamily.h
MSYS2じゃないMSYS版MinGWのgccでSDL2_ttfやharfbuzzをmakeしようとするとこのファイルがないというエラーが出るはず
適当なincludeフォルダーにこのファイルを入れてください。
MSYS2ではこのファイルが存在するので上記の作業は不要です。
