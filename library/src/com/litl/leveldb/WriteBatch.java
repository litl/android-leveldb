package com.litl.leveldb;

import java.nio.ByteBuffer;

public class WriteBatch extends NativeObject {
    public WriteBatch() {
        super(nativeCreate());
    }

    @Override
    public void closeNativeObject(int ptr) {
        nativeDestroy(ptr);
    }

    public void delete(ByteBuffer key) {
        assertOpen("WriteBatch is closed");
        if (key == null) {
            throw new NullPointerException("key");
        }

        nativeDelete(mPtr, key);
    }

    public void put(ByteBuffer key, ByteBuffer value) {
        assertOpen("WriteBatch is closed");
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }

        nativePut(mPtr, key, value);
    }

    public void clear() {
        assertOpen("WriteBatch is closed");
        nativeClear(mPtr);
    }

    private static native int nativeCreate();

    private static native void nativeDestroy(int ptr);

    private static native void nativeDelete(int ptr, ByteBuffer key);

    private static native void nativePut(int ptr, ByteBuffer key, ByteBuffer val);

    private static native void nativeClear(int ptr);
}
