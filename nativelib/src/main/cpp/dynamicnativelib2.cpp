#include <jni.h>

//加
jint addNumber(JNIEnv *env,jclass clazz,jint a,jint b){
    return a+b;
}

//三个参数，java层函数名,java 层方法签名,C 层方法指针
//获取签名方法: javap -s -p DynamicRegister.class
static const JNINativeMethod methods[]={
        {"add","(II)I",(void*)addNumber},
};
//java层load时，便会自动调用该方法
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved){
    //获得 JniEnv
    JNIEnv *jniEnv{nullptr};
    if (vm->GetEnv((void **) &jniEnv, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    //FindClass,反射,通过类的名字反射
    jclass mainActivityCls = jniEnv->FindClass("com/bob/nativelib/JNITools2");//注册 如果小于0则注册失败

    //注册方法
    jint ret=jniEnv->RegisterNatives(mainActivityCls,methods,sizeof(methods)/sizeof(methods[0]));
    if (ret != 0) {

        return -1;
    }

    return JNI_VERSION_1_6;
}