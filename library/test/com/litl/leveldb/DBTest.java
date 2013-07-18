package com.litl.leveldb;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.app.Activity;

@RunWith(RobolectricTestRunner.class)
public class DBTest {
    private File mPath;
    private DB mDb;

    @Before
    public void setUp() throws Exception {
        mPath = new File((new Activity()).getCacheDir(), "db-tests");
        mDb = new DB(mPath);
        mDb.open();
    }

    @After
    public void tearDown() throws Exception {
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

    @Test
    public void testBasics() {
        mDb.put(bytes("hello"), bytes("world"));
        mDb.put(bytes("bye"), bytes("moon"));

        byte[] val = mDb.get(bytes("hello"));
        Assert.assertNotNull(val);
        Assert.assertTrue(Arrays.equals(val, bytes("world")));

        val = mDb.get(bytes("bye"));
        Assert.assertNotNull(val);
        Assert.assertTrue(Arrays.equals(val, bytes("moon")));

        val = mDb.get(bytes("boo"));
        Assert.assertNull(val);
    }

    @Test
    public void testBatchAndIterator() {
        final String[] keys = { "foo1", "foo2", "foo3", "foo4", "foo5" };
        final String[] vals = { "bar1", "bar2", "bar3", "bar4", "bar5" };

        final WriteBatch batch = new WriteBatch();
        try {
            final ByteBuffer key = ByteBuffer.allocate(10);
            final ByteBuffer val = ByteBuffer.allocate(10);
            for (int i = 0; i < keys.length; i++) {
                key.clear();
                key.put(bytes(keys[i]));
                key.flip();

                val.clear();
                val.put(bytes(vals[i]));
                val.flip();

                batch.put(key, val);
            }

            mDb.write(batch);
        } finally {
            batch.close();
        }

        final Iterator iter = mDb.iterator();
        try {
            int i = 0;
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Assert.assertTrue(i < keys.length);

                final byte[] key = iter.getKey();
                final byte[] val = iter.getValue();

                Assert.assertTrue(Arrays.equals(bytes(keys[i]), key));
                Assert.assertTrue(Arrays.equals(bytes(vals[i]), val));

                i++;
            }
            Assert.assertTrue(i == keys.length);
        } finally {
            iter.close();
        }
    }

    @Test
    public void testSnapshots() {
        mDb.put(bytes("hello"), bytes("one"));
        mDb.put(bytes("bye"), bytes("one"));
        mDb.put(bytes("hi"), bytes("one"));

        final DB.Snapshot snapshot = mDb.getSnapshot();
        final Iterator iter = mDb.iterator(snapshot);
        try {
            mDb.put(bytes("hello"), bytes("two"));
            mDb.delete(bytes("bye"));

            Assert.assertTrue(Arrays.equals(mDb.get(snapshot, bytes("hello")), bytes("one")));
            Assert.assertTrue(Arrays.equals(mDb.get(snapshot, bytes("bye")), bytes("one")));
            Assert.assertTrue(Arrays.equals(mDb.get(snapshot, bytes("hi")), bytes("one")));

            int i = 0;
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                i++;
                Assert.assertTrue(Arrays.equals(iter.getValue(), bytes("one")));
            }
            Assert.assertEquals(3, i);
        } finally {
            iter.close();
            mDb.releaseSnapshot(snapshot);
        }

        Assert.assertTrue(Arrays.equals(mDb.get(bytes("hello")), bytes("two")));
        Assert.assertTrue(Arrays.equals(mDb.get(bytes("hi")), bytes("one")));
        Assert.assertNull(mDb.get(bytes("bye")));
    }

    @Test
    public void testSeek() {
        mDb.put(bytes("01"), bytes("foo"));
        mDb.put(bytes("02"), bytes("foo"));
        mDb.put(bytes("11"), bytes("foo"));
        mDb.put(bytes("12"), bytes("foo"));
        mDb.put(bytes("13"), bytes("foo"));
        mDb.put(bytes("21"), bytes("foo"));

        Iterator iter = mDb.iterator();
        try {
            iter.seek(bytes("1"));
            Assert.assertTrue(iter.isValid());
            Assert.assertTrue(Arrays.equals(bytes("11"), iter.getKey()));

            iter.seek(bytes("2"));
            Assert.assertTrue(iter.isValid());
            Assert.assertTrue(Arrays.equals(bytes("21"), iter.getKey()));

            iter.seek(bytes("3"));
            Assert.assertFalse(iter.isValid());
        } finally {
            iter.close();
        }

        iter = mDb.iterator();
        try {
            // Iterate over all values starting with "1"
            int i = 0;
            final byte[] searchByte = new byte[] { '1' };
            for (iter.seek(searchByte); iter.isValid(); iter.next()) {
                final byte[] key = iter.getKey();
                if (key[0] != searchByte[0]) {
                    break;
                }

                i++;
            }

            Assert.assertEquals(3, i);
        } finally {
            iter.close();
        }
    }

    @Test
    public void testWriteBatch() {
        final ByteBuffer managedBuf = ByteBuffer.allocate(10);
        final ByteBuffer directBuf = ByteBuffer.allocateDirect(10);

        final WriteBatch putBatch = new WriteBatch();
        try {
            directBuf.clear();
            directBuf.put(bytes("hello"));
            directBuf.flip();
            managedBuf.clear();
            managedBuf.put(bytes("world"));
            managedBuf.flip();
            putBatch.put(directBuf, managedBuf);

            managedBuf.clear();
            managedBuf.put(bytes("bye"));
            managedBuf.flip();
            directBuf.clear();
            directBuf.put(bytes("moon"));
            directBuf.flip();
            putBatch.put(managedBuf, directBuf);

            mDb.write(putBatch);
        } finally {
            putBatch.close();
        }

        final Iterator iter1 = mDb.iterator();
        try {
            iter1.seekToFirst();
            Assert.assertTrue(iter1.isValid());
            Assert.assertTrue(Arrays.equals(bytes("bye"), iter1.getKey()));
            Assert.assertTrue(Arrays.equals(bytes("moon"), iter1.getValue()));
            iter1.next();
            Assert.assertTrue(iter1.isValid());
            Assert.assertTrue(Arrays.equals(bytes("hello"), iter1.getKey()));
            Assert.assertTrue(Arrays.equals(bytes("world"), iter1.getValue()));
            iter1.next();
            Assert.assertFalse(iter1.isValid());
        } finally {
            iter1.close();
        }

        final WriteBatch deleteBatch = new WriteBatch();
        try {
            managedBuf.clear();
            managedBuf.put(bytes("hello"));
            managedBuf.flip();
            deleteBatch.delete(managedBuf);

            directBuf.clear();
            directBuf.put(bytes("bye"));
            directBuf.flip();
            deleteBatch.delete(directBuf);

            mDb.write(deleteBatch);
        } finally {
            deleteBatch.close();
        }

        final Iterator iter2 = mDb.iterator();
        try {
            iter2.seekToFirst();
            Assert.assertFalse(iter2.isValid());
        } finally {
            iter2.close();
        }
    }
}
