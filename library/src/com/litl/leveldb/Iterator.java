package com.litl.leveldb;

public class Iterator extends NativeObject {
    Iterator(int iterPtr) {
        super(iterPtr);
    }

    @Override
    public void closeNativeObject(int ptr) {
        nativeDestroy(ptr);
    }

    public void seekToFirst() {
        assertOpen("Iterator is closed");
        nativeSeekToFirst(mPtr);
    }

    public boolean isValid() {
        assertOpen("Iterator is closed");
        return nativeValid(mPtr);
    }

    public void next() {
        assertOpen("Iterator is closed");
        nativeNext(mPtr);
    }

    public byte[] getKey() {
        assertOpen("Iterator is closed");
        return nativeKey(mPtr);
    }

    public byte[] getValue() {
        assertOpen("Iterator is closed");
        return nativeValue(mPtr);
    }

    private static native void nativeDestroy(int ptr);

    private static native void nativeSeekToFirst(int ptr);

    private static native boolean nativeValid(int ptr);

    private static native void nativeNext(int ptr);

    private static native byte[] nativeKey(int dbPtr);

    private static native byte[] nativeValue(int dbPtr);
}
