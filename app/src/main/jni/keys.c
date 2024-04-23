#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_appintern_ApiUtilities_getApi(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "oDX3N4SHQ5E0V0btfR3Rt09FYGO4NAYt4Ds0Ig6mrnc4oJpGWvROUTg7");
}
JNIEXPORT jstring JNICALL
Java_com_example_appintern_RecheckPassword_getKeys(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "not a password");
}
JNIEXPORT jstring JNICALL
Java_com_example_appintern_EnterPassword_getKeys(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "not a password");
}

JNIEXPORT jstring JNICALL
Java_com_example_appintern_OverlayService_getKeys(JNIEnv *env, jclass clazz) {
    // TODO: implement getKeys()
    return (*env)->NewStringUTF(env, "not a password");
}