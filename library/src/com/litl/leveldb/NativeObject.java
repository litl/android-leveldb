package com.litl.leveldb;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

abstract class NativeObject implements Closeable {
    private static final String TAG = NativeObject.class.getSimpleName();
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

    @Override
    protected void finalize() throws Throwable {
        if (mPtr != 0) {
            Log.w(TAG,
                    "NativeObject "
                            + getClass().getSimpleName()
                            + " was finalized before native resource was closed, did you forget to call close()?");
        }

        super.finalize();
    }
}
