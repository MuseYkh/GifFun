#include <jni.h>
#include <string.h>
#include "ffmpeg/ffmpeg.h"

JNIEXPORT jint JNICALL
Java_cn_muse_ffmpegutil_FFmpegUtils_runCommands(JNIEnv *env, jclass clazz, jobjectArray cmd) {
    int argc = (*env)->GetArrayLength(env, cmd);
    char **argv = (char **) malloc(argc * sizeof(char *));
    int i;
    int result;
    for (i = 0; i < argc; i++) {
        jstring jstr = (jstring) (*env)->GetObjectArrayElement(env, cmd, i);
        char *temp = (char *) (*env)->GetStringUTFChars(env, jstr, 0);
        argv[i] = malloc(1024);
        strcpy(argv[i], temp);
        (*env)->ReleaseStringUTFChars(env, jstr, temp);
    }
    //执行ffmpeg命令
    result = run(argc, argv);
    //释放内存
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
    return result;
}

JNIEXPORT jstring JNICALL
Java_cn_muse_ffmpegutil_FFmpegUtils_getVersion(JNIEnv *env, jclass clazz) {
    const char *version = av_version_info();
    return (*env)->NewStringUTF(env, version);
}