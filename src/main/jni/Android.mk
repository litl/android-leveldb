LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := leveldbjni
LOCAL_C_INCLUDES := $(LOCAL_PATH)/leveldb-1.13.0/include
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
LOCAL_C_INCLUDES := $(LOCAL_PATH)/leveldb-1.13.0 $(LOCAL_PATH)/leveldb-1.13.0/include $(LOCAL_PATH)/snappy-1.1.0
LOCAL_SRC_FILES := leveldb-1.13.0/db/builder.cc leveldb-1.13.0/db/c.cc leveldb-1.13.0/db/db_impl.cc leveldb-1.13.0/db/db_iter.cc leveldb-1.13.0/db/dbformat.cc leveldb-1.13.0/db/filename.cc leveldb-1.13.0/db/log_reader.cc leveldb-1.13.0/db/log_writer.cc leveldb-1.13.0/db/memtable.cc leveldb-1.13.0/db/repair.cc leveldb-1.13.0/db/table_cache.cc leveldb-1.13.0/db/version_edit.cc leveldb-1.13.0/db/version_set.cc leveldb-1.13.0/db/write_batch.cc leveldb-1.13.0/table/block.cc leveldb-1.13.0/table/block_builder.cc leveldb-1.13.0/table/filter_block.cc leveldb-1.13.0/table/format.cc leveldb-1.13.0/table/iterator.cc leveldb-1.13.0/table/merger.cc leveldb-1.13.0/table/table.cc leveldb-1.13.0/table/table_builder.cc leveldb-1.13.0/table/two_level_iterator.cc leveldb-1.13.0/util/arena.cc leveldb-1.13.0/util/bloom.cc leveldb-1.13.0/util/cache.cc leveldb-1.13.0/util/coding.cc leveldb-1.13.0/util/comparator.cc leveldb-1.13.0/util/crc32c.cc leveldb-1.13.0/util/env.cc leveldb-1.13.0/util/env_posix.cc leveldb-1.13.0/util/filter_policy.cc leveldb-1.13.0/util/hash.cc leveldb-1.13.0/util/histogram.cc leveldb-1.13.0/util/logging.cc leveldb-1.13.0/util/options.cc leveldb-1.13.0/util/status.cc leveldb-1.13.0/port/port_posix.cc
LOCAL_STATIC_LIBRARIES += snappy

include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := snappy
LOCAL_CPP_EXTENSION := .cc
LOCAL_SRC_FILES := snappy-1.1.0/snappy.cc snappy-1.1.0/snappy-sinksource.cc

include $(BUILD_STATIC_LIBRARY)
