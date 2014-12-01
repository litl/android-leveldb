LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := leveldbjni
LOCAL_C_INCLUDES := $(LOCAL_PATH)/leveldb-1.18/include
LOCAL_CPP_EXTENSION := .cc
LOCAL_CFLAGS := -DLEVELDB_PLATFORM_ANDROID -std=gnu++0x
LOCAL_SRC_FILES := com_litl_leveldb_DB.cc com_litl_leveldb_Iterator.cc com_litl_leveldb_WriteBatch.cc leveldbjni.cc
LOCAL_STATIC_LIBRARIES +=  leveldb
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := leveldb
LOCAL_CFLAGS := -D_REENTRANT -DOS_ANDROID -DLEVELDB_PLATFORM_POSIX -DNDEBUG -DSNAPPY
LOCAL_CPP_EXTENSION := .cc
LOCAL_C_INCLUDES := $(LOCAL_PATH)/leveldb-1.18 $(LOCAL_PATH)/leveldb-1.18/include $(LOCAL_PATH)/snappy-1.1.0
LOCAL_SRC_FILES := leveldb-1.18/db/builder.cc leveldb-1.18/db/c.cc leveldb-1.18/db/db_impl.cc leveldb-1.18/db/db_iter.cc leveldb-1.18/db/dbformat.cc leveldb-1.18/db/filename.cc leveldb-1.18/db/log_reader.cc leveldb-1.18/db/log_writer.cc leveldb-1.18/db/memtable.cc leveldb-1.18/db/repair.cc leveldb-1.18/db/table_cache.cc leveldb-1.18/db/version_edit.cc leveldb-1.18/db/version_set.cc leveldb-1.18/db/write_batch.cc leveldb-1.18/table/block.cc leveldb-1.18/table/block_builder.cc leveldb-1.18/table/filter_block.cc leveldb-1.18/table/format.cc leveldb-1.18/table/iterator.cc leveldb-1.18/table/merger.cc leveldb-1.18/table/table.cc leveldb-1.18/table/table_builder.cc leveldb-1.18/table/two_level_iterator.cc leveldb-1.18/util/arena.cc leveldb-1.18/util/bloom.cc leveldb-1.18/util/cache.cc leveldb-1.18/util/coding.cc leveldb-1.18/util/comparator.cc leveldb-1.18/util/crc32c.cc leveldb-1.18/util/env.cc leveldb-1.18/util/env_posix.cc leveldb-1.18/util/filter_policy.cc leveldb-1.18/util/hash.cc leveldb-1.18/util/histogram.cc leveldb-1.18/util/logging.cc leveldb-1.18/util/options.cc leveldb-1.18/util/status.cc leveldb-1.18/port/port_posix.cc
LOCAL_STATIC_LIBRARIES += snappy

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := snappy
LOCAL_CPP_EXTENSION := .cc
LOCAL_SRC_FILES := snappy-1.1.0/snappy.cc snappy-1.1.0/snappy-sinksource.cc

include $(BUILD_STATIC_LIBRARY)
