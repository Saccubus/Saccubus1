#!/bin/bash
# Usage: bash configExp.sh ffmpeg-git-dir-path
#        otherwize use "../ffmpeg" as ffmpeg-git-path
ffmpegdir=$1
if [[ -z $ffmpegdir ]]; then
  ffmpegdir=$PWD/../ffmpeg
fi
export dir=$ffmpegdir
 if [[ ! -d $dir ]]; then
 (
  mkdir $dir
  cd $dir/..
  git clone https://github.com/Saccubus/ffmpeg.git $dir
  cd $dir
  git remote add official https://github.com/ffmpeg/ffmpeg.git || true
  cd ..
 )
 fi
 pushd $dir
 git reset --hard
 git fetch origin
 git fetch official
 git branch -D officialmaster || true
 git checkout -b officialmaster official/master
 git branch -D n3.2.2 || true
 git checkout refs/tags/n3.2.2
 git checkout -b n3.2.2
 git checkout master
 git branch -D buildbranch || true
 git checkout -b buildbranch origin/Saccubus1
 git rebase n3.2.2
 cat<<EOL
 #######################
 If you want to build anohter branch for Saccubus1 else with latest_1.x 
 try followings:
   git checkout -b buildbranch origin/Saccubus1
   git rebase "something-another-master-branch-ffmpeg"
   (ex1:  git rebase official/master)
   (ex2:  git rebase official/release/3.3)
   (      etc...)
 #######################
EOL
 bash version.sh . version.h
 echo Going to configure followings:
 cat version.h
 popd
# goto build dir
if [[ ! -d build ]]; then
   mkdir build
fi
mv $dir/version.h build/version.h

pushd build
echo Starting Configuration Script: $dir/configure
export TEMPDIR=/mingw32/tmp
TEMPDIR=/mingw32/tmp LANG=C lang=C $dir/configure \
 --enable-gpl \
 --enable-version3 \
 --enable-postproc \
 --enable-libopencore-amrnb \
 --enable-libopencore-amrwb \
 --enable-libvo-amrwbenc \
 --enable-libmp3lame \
 --enable-libgsm \
 --enable-libspeex \
 --enable-libvorbis \
 --enable-libtheora \
 --enable-libopus \
 --enable-libxvid \
 --enable-libvpx \
 --enable-libx264 \
 --enable-hwaccel=h264_dxva2 \
 --enable-libx265 \
 --enable-hwaccel=hevc_dxva2 \
 --enable-libopenjpeg \
 --enable-libmfx \
 --enable-nvenc \
 --disable-ffserver \
 --disable-ffplay \
 --disable-ffprobe \
 --enable-avisynth \
 --enable-w32threads \
 --extra-ldflags="-static -static-libgcc" \
 --extra-cflags="-march=i686 -mtune=generic -mfpmath=sse -msse" \
 --enable-extra-warnings \
 --disable-debug \
 --enable-runtime-cpudetect \
 --optflags="-O2 -finline-functions" \
 --pkg-config-flags="--static" \
 --extra-version="d_$dir" 

echo End Configuration Script: $dir/configure
cat<<EOA
 ########################################################
 Starting build ffmpeg.exe
 It will take SOME TIME (a few hours.. or minutes or else)
 Please take it easy and wait and take a break.
 ########################################################
EOA
make
$PWD/ffmpeg.exe -version
cat<<EOB
 ########
 # DONE!#
 ########
EOB
popd
exit

# --extra-version=""
# --enable-avresample \
# --enable-libmodplug \
# --enable-libkvazaar \

# --extra-version="_$dir" 
