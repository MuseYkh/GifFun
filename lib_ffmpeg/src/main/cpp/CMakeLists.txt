# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../libs)

add_library(
        ffmpeg
        SHARED
        IMPORTED)
set_target_properties(
        ffmpeg
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libffmpeg.so)

add_library( # Sets the name of the library.
        ffmpeg-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ffmpeg/cmdutils.c
        ffmpeg/ffmpeg.c
        ffmpeg/ffmpeg_filter.c
        ffmpeg/ffmpeg_opt.c
        ffmpeg-lib.c)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(.)
include_directories(./include)

target_link_libraries( # Specifies the target library.
        ffmpeg-lib
        ffmpeg
        ${log-lib})