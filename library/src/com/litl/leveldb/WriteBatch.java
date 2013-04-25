package com.litl.leveldb;

import java.io.Closeable;

public class WriteBatch implements Closeable {
    private int mPtr;

    public WriteBatch() {
        mPtr = nativeCreate();
    }

    @Override
    public void close() {
        if (mPtr != 0) {
            nativeDestroy(mPtr);
            mPtr = 0;
        }
    }

    public void delete(byte[] key) {
        if (mPtr == 0) {
            throw new IllegalStateException("WriteBatch is closed");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }

        nativeDelete(mPtr, key);
    }

    public void put(byte[] key, byte[] value) {
        if (mPtr == 0) {
            throw new IllegalStateException("WriteBatch is closed");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }

        nativePut(mPtr, key, value);
    }

    public void clear() {
        if (mPtr == 0) {
            throw new IllegalStateException("WriteBatch is closed");
        }
        nativeClear(mPtr);
    }

    int getPtr() {
        return mPtr;
    }

    private static native int nativeCreate();

    private static native void nativeDestroy(int ptr);

    private static native void nativeDelete(int ptr, byte[] key);

    private static native void nativePut(int ptr, byte[] key, byte[] val);

    private static native void nativeClear(int ptr);
}
