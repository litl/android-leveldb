package com.litl.leveldb;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;

abstract class NativeObject implements Closeable {
    protected long mPtr;
    private AtomicInteger mRefCount = new AtomicInteger();

    protected NativeObject() {
        // The Java wrapper counts as one reference, will
        // be released when closed
        ref();
    }

    protected NativeObject(long ptr) {
        this();

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

    void ref() {
        mRefCount.incrementAndGet();
    }

    void unref() {
        if (mRefCount.decrementAndGet() == 0) {
            closeNativeObject(mPtr);
            mPtr = 0;
        }
    }

    protected abstract void closeNativeObject(long ptr);

    @Override
    public void close() {
        unref();
    }
}
