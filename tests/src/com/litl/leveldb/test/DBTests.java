package com.litl.leveldb.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.test.AndroidTestCase;

import com.litl.leveldb.DB;
import com.litl.leveldb.Iterator;
import com.litl.leveldb.WriteBatch;

public class DBTests extends AndroidTestCase {
    private File mPath;
    private DB mDb;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPath = new File(getContext().getCacheDir(), "db-tests");
        mDb = new DB(mPath);
        mDb.open();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDb.close();
        DB.destroy(mPath);
    }

    private byte[] bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void testBasics() {
        mDb.put(bytes("hello"), bytes("world"));
        mDb.put(bytes("bye"), bytes("moon"));

        byte[] val = mDb.get(bytes("hello"));
        assertNotNull(val);
        assertTrue(Arrays.equals(val, bytes("world")));

        val = mDb.get(bytes("bye"));
        assertNotNull(val);
        assertTrue(Arrays.equals(val, bytes("moon")));

        val = mDb.get(bytes("boo"));
        assertNull(val);
    }

    public void testBatchAndIterator() {
        final String[] keys = { "foo1", "foo2", "foo3", "foo4", "foo5" };
        final String[] vals = { "bar1", "bar2", "bar3", "bar4", "bar5" };

        final WriteBatch batch = new WriteBatch();
        try {
            for (int i = 0; i < keys.length; i++) {
                batch.put(bytes(keys[i]), bytes(vals[i]));
            }

            mDb.write(batch);
        } finally {
            batch.close();
        }

        final Iterator iter = mDb.iterator();
        try {
            int i = 0;
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                assertTrue(i < keys.length);

                final byte[] key = iter.getKey();
                final byte[] val = iter.getValue();

                assertTrue(Arrays.equals(bytes(keys[i]), key));
                assertTrue(Arrays.equals(bytes(vals[i]), val));

                i++;
            }
            assertTrue(i == keys.length);
        } finally {
            iter.close();
        }
    }

    public void testSnapshots() {
        mDb.put(bytes("hello"), bytes("one"));
        mDb.put(bytes("bye"), bytes("one"));
        mDb.put(bytes("hi"), bytes("one"));

        final DB.Snapshot snapshot = mDb.getSnapshot();
        final Iterator iter = mDb.iterator(snapshot);
        try {
            mDb.put(bytes("hello"), bytes("two"));
            mDb.delete(bytes("bye"));

            assertTrue(Arrays.equals(mDb.get(snapshot, bytes("hello")), bytes("one")));
            assertTrue(Arrays.equals(mDb.get(snapshot, bytes("bye")), bytes("one")));
            assertTrue(Arrays.equals(mDb.get(snapshot, bytes("hi")), bytes("one")));

            int i = 0;
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                i++;
                assertTrue(Arrays.equals(iter.getValue(), bytes("one")));
            }
            assertEquals(3, i);
        } finally {
            iter.close();
            mDb.releaseSnapshot(snapshot);
        }

        assertTrue(Arrays.equals(mDb.get(bytes("hello")), bytes("two")));
        assertTrue(Arrays.equals(mDb.get(bytes("hi")), bytes("one")));
        assertNull(mDb.get(bytes("bye")));
    }
}
