#include <string.h>
#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

#include "leveldbjni.h"

#include "leveldb/db.h"
#include "leveldb/write_batch.h"

static jlong
nativeOpen(JNIEnv* env,
           jclass clazz,
           jstring dbpath)
{

    const char *path = env->GetStringUTFChars(dbpath, 0);
    LOGI("Opening database %s", path);

    leveldb::DB* db;
    leveldb::Options options;
    options.create_if_missing = true;
    leveldb::Status status = leveldb::DB::Open(options, path, &db);
    env->ReleaseStringUTFChars(dbpath, path);

    if (!status.ok()) {
        throwException(env, status);
    } else {
        LOGI("Opened database");
    }

    return reinterpret_cast<jlong>(db);
}

static void
nativeClose(JNIEnv* env,
            jclass clazz,
            jlong dbPtr)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);
    if (db) {
        delete db;
    }

    LOGI("Database closed");
}

static jbyteArray
nativeGet(JNIEnv * env,
          jclass clazz,
          jlong dbPtr,
          jlong snapshotPtr,
          jbyteArray keyObj)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);
    leveldb::ReadOptions options = leveldb::ReadOptions();
    options.snapshot = reinterpret_cast<leveldb::Snapshot*>(snapshotPtr);

    size_t keyLen = env->GetArrayLength(keyObj);
    jbyte *buffer = env->GetByteArrayElements(keyObj, NULL);
    jbyteArray result;

    leveldb::Slice key = leveldb::Slice((const char *)buffer, keyLen);
    leveldb::Iterator* iter = db->NewIterator(options);
    iter->Seek(key);
    if (iter->Valid() && key == iter->key()) {
        leveldb::Slice value = iter->value();
        size_t len = value.size();
        result = env->NewByteArray(len);
        env->SetByteArrayRegion(result, 0, len, (const jbyte *) value.data());
    } else {
        result = NULL;
    }

    env->ReleaseByteArrayElements(keyObj, buffer, JNI_ABORT);
    delete iter;

    return result;
}

static void
nativePut(JNIEnv *env,
          jclass clazz,
          jlong dbPtr,
          jbyteArray keyObj,
          jbyteArray valObj)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);

    size_t keyLen = env->GetArrayLength(keyObj);
    jbyte *keyBuf = env->GetByteArrayElements(keyObj, NULL);

    size_t valLen = env->GetArrayLength(valObj);
    jbyte *valBuf = env->GetByteArrayElements(valObj, NULL);

    leveldb::Status status = db->Put(leveldb::WriteOptions(),
            leveldb::Slice((const char *) keyBuf, keyLen),
            leveldb::Slice((const char *) valBuf, valLen));

    env->ReleaseByteArrayElements(keyObj, keyBuf, JNI_ABORT);
    env->ReleaseByteArrayElements(valObj, valBuf, JNI_ABORT);

    if (!status.ok()) {
        throwException(env, status);
    }
}

static void
nativeDelete(JNIEnv *env,
             jclass clazz,
             jlong dbPtr,
             jbyteArray keyObj)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);

    size_t keyLen = env->GetArrayLength(keyObj);
    jbyte *buffer = env->GetByteArrayElements(keyObj, NULL);

    leveldb::Status status = db->Delete(leveldb::WriteOptions(), leveldb::Slice((const char *) buffer, keyLen));
    env->ReleaseByteArrayElements(keyObj, buffer, JNI_ABORT);

    if (!status.ok()) {
        throwException(env, status);
    }
}

static void
nativeWrite(JNIEnv *env,
            jclass clazz,
            jlong dbPtr,
            jlong batchPtr)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);

    leveldb::WriteBatch *batch = (leveldb::WriteBatch *) batchPtr;
    leveldb::Status status = db->Write(leveldb::WriteOptions(), batch);
    if (!status.ok()) {
        throwException(env, status);
    }
}

static jlong
nativeIterator(JNIEnv* env,
               jclass clazz,
               jlong dbPtr,
               jlong snapshotPtr)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);
    leveldb::ReadOptions options = leveldb::ReadOptions();
    options.snapshot = reinterpret_cast<leveldb::Snapshot*>(snapshotPtr);

    leveldb::Iterator *iter = db->NewIterator(options);
    return reinterpret_cast<jlong>(iter);
}

static jlong
nativeGetSnapshot(JNIEnv *env,
                  jclass clazz,
                  jlong dbPtr)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);
    const leveldb::Snapshot* snapshot = db->GetSnapshot();
    return reinterpret_cast<jlong>(snapshot);
}

static void
nativeReleaseSnapshot(JNIEnv *env,
                      jclass clazz,
                      jlong dbPtr,
                      jlong snapshotPtr)
{
    leveldb::DB* db = reinterpret_cast<leveldb::DB*>(dbPtr);
    const leveldb::Snapshot *snapshot = reinterpret_cast<leveldb::Snapshot*>(snapshotPtr);
    db->ReleaseSnapshot(snapshot);
}

static void
nativeDestroy(JNIEnv *env,
              jclass clazz,
              jstring dbpath)
{
    const char* path = env->GetStringUTFChars(dbpath,0);
    leveldb::Options options;
    options.create_if_missing = true;
    leveldb::Status status = DestroyDB(path, options);
    if (!status.ok()) {
        throwException(env, status);
    }
}

static JNINativeMethod sMethods[] =
{
        { "nativeOpen", "(Ljava/lang/String;)J", (void*) nativeOpen },
        { "nativeClose", "(J)V", (void*) nativeClose },
        { "nativeGet", "(JJ[B)[B", (void*) nativeGet },
        { "nativePut", "(J[B[B)V", (void*) nativePut },
        { "nativeDelete", "(J[B)V", (void*) nativeDelete },
        { "nativeWrite", "(JJ)V", (void*) nativeWrite },
        { "nativeIterator", "(JJ)J", (void*) nativeIterator },
        { "nativeGetSnapshot", "(J)J", (void*) nativeGetSnapshot },
        { "nativeReleaseSnapshot", "(JJ)V", (void*) nativeReleaseSnapshot },
        { "nativeDestroy", "(Ljava/lang/String;)V", (void*) nativeDestroy }
};

int
register_com_litl_leveldb_DB(JNIEnv *env) {
    jclass clazz = env->FindClass("com/litl/leveldb/DB");
    if (!clazz) {
        LOGE("Can't find class com.litl.leveldb.DB");
        return 0;
    }

    return env->RegisterNatives(clazz, sMethods, NELEM(sMethods));
}
