package com.litl.leveldb;

import java.io.Closeable;

abstract class NativeObject implements Closeable {
    protected long mPtr;

    protected NativeObject() {
    }

    protected NativeObject(long ptr) {
        if (ptr == 0) {
            throw new OutOfMemoryError("Failed to allocate native object");
        }

        mPtr = ptr;
    }

    protected long getPtr() {
        return mPtr;
    }

    protected void assertOpen(String message) {
        if (mPtr == 0) {
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void close() {
        if (mPtr != 0) {
            closeNativeObject(mPtr);
            mPtr = 0;
        }
    }

    protected abstract void closeNativeObject(long ptr);
}
