package com.litl.leveldb;

import java.io.Closeable;
import java.io.File;

public class DB implements Closeable {
    private final File mPath;
    private int mPtr;

    public DB(File path) {
        if (path == null) {
            throw new NullPointerException();
        }
        mPath = path;
    }

    public void open() {
        mPtr = nativeOpen(mPath.getAbsolutePath());
    }

    @Override
    public void close() {
        if (mPtr != 0) {
            nativeClose(mPtr);
            mPtr = 0;
        }
    }

    public void put(byte[] key, byte[] value) {
        if (mPtr == 0) {
            throw new IllegalStateException("Database is closed");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }

        nativePut(mPtr, key, value);
    }

    public byte[] get(byte[] key) {
        if (mPtr == 0) {
            throw new IllegalStateException("Database is closed");
        }
        if (key == null) {
            throw new NullPointerException();
        }

        return nativeGet(mPtr, key);
    }

    public void delete(byte[] key) {
        if (mPtr == 0) {
            throw new IllegalStateException("Database is closed");
        }
        if (key == null) {
            throw new NullPointerException();
        }

        nativeDelete(mPtr, key);
    }

    public void write(WriteBatch batch) {
        if (mPtr == 0) {
            throw new IllegalStateException("Database is closed");
        }
        if (batch == null) {
            throw new NullPointerException();
        }

        nativeWrite(mPtr, batch.getPtr());
    }

    public Iterator iterator() {
        if (mPtr == 0) {
            throw new IllegalStateException("Database is closed");
        }
        return new Iterator(nativeIterator(mPtr));
    }

    public static void destroy(File path) {
        nativeDestroy(path.getAbsolutePath());
    }

    private static native int nativeOpen(String dbpath);

    private static native void nativeClose(int dbPtr);

    private static native void nativePut(int dbPtr, byte[] key, byte[] value);

    private static native byte[] nativeGet(int dbPtr, byte[] key);

    private static native void nativeDelete(int dbPtr, byte[] key);

    private static native void nativeWrite(int dbPtr, int batchPtr);

    private static native void nativeDestroy(String dbpath);

    private static native int nativeIterator(int dbPtr);

    public static native String stringFromJNI();

    {
        System.loadLibrary("leveldb");
        System.loadLibrary("leveldbjni");
    }
}
