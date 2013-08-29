package com.litl.leveldb;

import java.io.File;

public class DB extends NativeObject {
    public static class Snapshot {
        private long mPtr;

        Snapshot(long ptr) {
            mPtr = ptr;
        }

        long getPtr() {
            return mPtr;
        }
    }

    private final File mPath;

    public DB(File path) {
        super();

        if (path == null) {
            throw new NullPointerException();
        }
        mPath = path;
    }

    public void open() {
        mPtr = nativeOpen(mPath.getAbsolutePath());
    }

    @Override
    public void closeNativeObject(long ptr) {
        nativeClose(ptr);
    }

    public void put(byte[] key, byte[] value) {
        assertOpen("Database is closed");
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }

        nativePut(mPtr, key, value);
    }

    public byte[] get(byte[] key) {
        return get(null, key);
    }

    public byte[] get(Snapshot snapshot, byte[] key) {
        assertOpen("Database is closed");
        if (key == null) {
            throw new NullPointerException();
        }

        return nativeGet(mPtr, snapshot != null ? snapshot.getPtr() : 0, key);
    }

    public void delete(byte[] key) {
        assertOpen("Database is closed");
        if (key == null) {
            throw new NullPointerException();
        }

        nativeDelete(mPtr, key);
    }

    public void write(WriteBatch batch) {
        assertOpen("Database is closed");
        if (batch == null) {
            throw new NullPointerException();
        }

        nativeWrite(mPtr, batch.getPtr());
    }

    public Iterator iterator() {
        return iterator(null);
    }

    public Iterator iterator(Snapshot snapshot) {
        assertOpen("Database is closed");
        return new Iterator(nativeIterator(mPtr, snapshot != null ? snapshot.getPtr() : 0));
    }

    public Snapshot getSnapshot() {
        assertOpen("Database is closed");
        return new Snapshot(nativeGetSnapshot(mPtr));
    }

    public void releaseSnapshot(Snapshot snapshot) {
        assertOpen("Database is closed");
        if (snapshot == null) {
            throw new NullPointerException();
        }

        nativeReleaseSnapshot(mPtr, snapshot.getPtr());
    }

    public static void destroy(File path) {
        nativeDestroy(path.getAbsolutePath());
    }

    private static native long nativeOpen(String dbpath);

    private static native void nativeClose(long dbPtr);

    private static native void nativePut(long dbPtr, byte[] key, byte[] value);

    private static native byte[] nativeGet(long dbPtr, long snapshotPtr, byte[] key);

    private static native void nativeDelete(long dbPtr, byte[] key);

    private static native void nativeWrite(long dbPtr, long batchPtr);

    private static native void nativeDestroy(String dbpath);

    private static native long nativeIterator(long dbPtr, long snapshotPtr);

    private static native long nativeGetSnapshot(long dbPtr);

    private static native void nativeReleaseSnapshot(long dbPtr, long snapshotPtr);

    public static native String stringFromJNI();

    {
        System.loadLibrary("leveldbjni");
    }
}
