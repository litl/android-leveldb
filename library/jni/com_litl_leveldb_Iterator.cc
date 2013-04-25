#include <jni.h>
#include <android/log.h>

#include "leveldbjni.h"

#include "leveldb/iterator.h"

static void
nativeDestroy(JNIEnv* env,
              jclass clazz,
              jint ptr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(ptr);

    delete iter;
}

static void
nativeSeekToFirst(JNIEnv* env,
                  jclass clazz,
                  jint iterPtr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(iterPtr);
    iter->SeekToFirst();
}

static jboolean
nativeValid(JNIEnv* env,
            jclass clazz,
            jint iterPtr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(iterPtr);
    return iter->Valid();
}

static void
nativeNext(JNIEnv* env,
           jclass clazz,
           jint iterPtr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(iterPtr);
    iter->Next();
}

static jbyteArray
nativeKey(JNIEnv* env,
          jclass clazz,
          jint iterPtr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(iterPtr);
    leveldb::Slice key = iter->key();

    size_t len = key.size();
    jbyteArray result = env->NewByteArray(len);
    env->SetByteArrayRegion(result, 0, len, (const jbyte *) key.data());
    return result;
}

static jbyteArray
nativeValue(JNIEnv* env,
            jclass clazz,
            jint iterPtr)
{
    leveldb::Iterator* iter = reinterpret_cast<leveldb::Iterator*>(iterPtr);
    leveldb::Slice value = iter->value();

    size_t len = value.size();
    jbyteArray result = env->NewByteArray(len);
    env->SetByteArrayRegion(result, 0, len, (const jbyte *) value.data());
    return result;
}

static JNINativeMethod sMethods[] =
{
        { "nativeDestroy", "(I)V", (void*) nativeDestroy },
        { "nativeSeekToFirst", "(I)V", (void*) nativeSeekToFirst },
        { "nativeValid", "(I)Z", (void*) nativeValid },
        { "nativeNext", "(I)V", (void*) nativeNext },
        { "nativeKey", "(I)[B", (void*) nativeKey },
        { "nativeValue", "(I)[B", (void*) nativeValue }
};

int
register_com_litl_leveldb_Iterator(JNIEnv *env) {
    jclass clazz = env->FindClass("com/litl/leveldb/Iterator");
    if (!clazz) {
        LOGE("Can't find class com.litl.leveldb.Iterator");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
