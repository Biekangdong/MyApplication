#include <jni.h>
#include <string>

#include <android/log.h>
#define TAG "kang"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);


extern "C" JNIEXPORT jstring JNICALL
Java_com_bob_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject) {
    std::string hello = "Hello from C++";
    LOGE("Hello from C++");
    return env->NewStringUTF(hello.c_str());
}