package com.litl.leveldb;

public class WriteBatch extends NativeObject {
    public WriteBatch() {
        super(nativeCreate());
    }

    @Override
    public void closeNativeObject(int ptr) {
        nativeDestroy(ptr);
    }

    public void delete(byte[] key) {
        assertOpen("WriteBatch is closed");
        if (key == null) {
            throw new NullPointerException("key");
        }

        nativeDelete(mPtr, key);
    }

    public void put(byte[] key, byte[] value) {
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

    private static native void nativeDelete(int ptr, byte[] key);

    private static native void nativePut(int ptr, byte[] key, byte[] val);

    private static native void nativeClear(int ptr);
}
