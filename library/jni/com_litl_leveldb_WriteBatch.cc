#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

#include "leveldbjni.h"

#include "leveldb/write_batch.h"

static jint
nativeCreate(JNIEnv* env,
             jclass clazz)
{
    leveldb::WriteBatch* batch = new leveldb::WriteBatch();
    return reinterpret_cast<jint>(batch);
}

static void
nativeDestroy(JNIEnv* env,
              jclass clazz,
              jint ptr)
{
    leveldb::WriteBatch* batch = reinterpret_cast<leveldb::WriteBatch*>(ptr);

    delete batch;
}

static void
nativeDelete(JNIEnv* env,
             jclass clazz,
             jint ptr,
             jbyteArray keyObj)
{
    leveldb::WriteBatch* batch = reinterpret_cast<leveldb::WriteBatch*>(ptr);

    size_t keyLen = env->GetArrayLength(keyObj);
    jbyte *buffer = env->GetByteArrayElements(keyObj, NULL);
    batch->Delete(leveldb::Slice((const char *) buffer, keyLen));
    env->ReleaseByteArrayElements(keyObj, buffer, JNI_ABORT);
}

static void
nativePut(JNIEnv* env,
          jclass clazz,
          jint ptr,
          jbyteArray keyObj,
          jbyteArray valObj)
{
    leveldb::WriteBatch* batch = reinterpret_cast<leveldb::WriteBatch*>(ptr);

    size_t keyLen = env->GetArrayLength(keyObj);
    jbyte *keyBuf = env->GetByteArrayElements(keyObj, NULL);

    size_t valLen = env->GetArrayLength(valObj);
    jbyte *valBuf = env->GetByteArrayElements(valObj, NULL);

    batch->Put(leveldb::Slice((const char *) keyBuf, keyLen), leveldb::Slice((const char *) valBuf, valLen));

    env->ReleaseByteArrayElements(keyObj, keyBuf, JNI_ABORT);
    env->ReleaseByteArrayElements(valObj, valBuf, JNI_ABORT);
}

static void
nativeClear(JNIEnv* env,
            jclass clazz,
            jint ptr)
{
    leveldb::WriteBatch* batch = reinterpret_cast<leveldb::WriteBatch*>(ptr);
    batch->Clear();
}

static JNINativeMethod sMethods[] =
{
        { "nativeCreate", "()I", (void*) nativeCreate },
        { "nativeDestroy", "(I)V", (void*) nativeDestroy },
        { "nativeDelete", "(I[B)V", (void*) nativeDelete },
        { "nativePut", "(I[B[B)V", (void*) nativePut },
        { "nativeClear", "(I)V", (void*) nativeClear }
};

int
register_com_litl_leveldb_WriteBatch(JNIEnv *env) {
    jclass clazz = env->FindClass("com/litl/leveldb/WriteBatch");
    if (!clazz) {
        LOGE("Can't find class com.litl.leveldb.WriteBatch");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
