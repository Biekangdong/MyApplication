# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("nativelib")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


# 编译一个库
add_library( # Sets the name of the library.
             nativelib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        nativelib.cpp)

add_library(
        # 库的名字
        dynamicnativelib

        # 动态库（.so库）
        SHARED

        # 需要编译的C++文件
        dynamicnativelib.c)

add_library(
        # 库的名字
        dynamicnativelib2

        # 动态库（.so库）
        SHARED

        # 需要编译的C++文件
        dynamicnativelib2.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.


# 将变量log-lib连接到so库（我这边的so库名字是native-lib）中，这样这个库就能使用日志打印功能了
target_link_libraries( # Specifies the target library.
                       nativelib
                       dynamicnativelib
                       dynamicnativelib2
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )


