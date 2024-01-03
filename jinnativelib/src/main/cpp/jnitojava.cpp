#include <jni.h>
#include <string>

#include <android/log.h>
#define TAG "kang"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);

extern "C" JNIEXPORT void JNICALL
Java_com_bob_nativelib_TestCallBack_callBackAdd(JNIEnv* env,jobject) {
    //1得到字节码 包名：com.bob.nativelib
    jclass jclazz = env->FindClass("com/bob/nativelib/TestCallBack");
    //2得到方法
    jmethodID jmethodIds = env->GetMethodID(jclazz,"add","(II)I");
    //3实例化
    jobject object = env->AllocObject(jclazz);
    //4调用方法
    jint result= env->CallIntMethod(object,jmethodIds,100,1);
    //5打印结果
    LOGE("result：%d",result);
}