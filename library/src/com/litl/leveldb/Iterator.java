package com.litl.leveldb;

import java.io.Closeable;

public class Iterator implements Closeable {
    private int mPtr;

    Iterator(int iterPtr) {
        mPtr = iterPtr;
    }

    @Override
    public void close() {
        if (mPtr != 0) {
            nativeDestroy(mPtr);
            mPtr = 0;
        }
    }

    public void seekToFirst() {
        nativeSeekToFirst(mPtr);
    }

    public boolean isValid() {
        return nativeValid(mPtr);
    }

    public void next() {
        nativeNext(mPtr);
    }

    public byte[] getKey() {
        return nativeKey(mPtr);
    }

    public byte[] getValue() {
        return nativeValue(mPtr);
    }

    private static native void nativeDestroy(int ptr);

    private static native void nativeSeekToFirst(int ptr);

    private static native boolean nativeValid(int ptr);

    private static native void nativeNext(int ptr);

    private static native byte[] nativeKey(int dbPtr);

    private static native byte[] nativeValue(int dbPtr);
}
