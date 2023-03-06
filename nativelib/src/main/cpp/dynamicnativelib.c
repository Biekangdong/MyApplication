#include "jni.h"

//日志打印
#include <android/log.h>
#define TAG "kang"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__);

//加
jint addNumber(JNIEnv *env,jclass clazz,jint a,jint b){
    return a+b;
}

static const char *mClassName = "com/bob/nativelib/JNITools";
//三个参数，java层函数名,java 层方法签名,C 层方法指针
//获取签名方法: javap -s -p DynamicRegister.class
static const JNINativeMethod methods[]={
        {"add","(II)I",(void*)addNumber},
};
//java层load时，便会自动调用该方法
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved){

    JNIEnv* env = NULL;
    //获得 JniEnv
    int r = (*vm)->GetEnv(vm,(void**)&env,JNI_VERSION_1_6);
    if(r != JNI_OK){
        return  -1;
    }
    //FindClass,反射,通过类的名字反射
    jclass mainActivityCls = (*env)->FindClass(env, mClassName);//注册 如果小于0则注册失败

    //注册方法
    r = (*env)->RegisterNatives(env,mainActivityCls,methods,sizeof(methods)/sizeof(methods[0]));
    if(r != JNI_OK){
        return -1;
    }
    return JNI_VERSION_1_6;
}