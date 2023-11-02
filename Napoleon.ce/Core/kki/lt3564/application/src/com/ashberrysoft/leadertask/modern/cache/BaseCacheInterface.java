package com.ashberrysoft.leadertask.modern.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseCacheInterface<KEY extends Serializable, VALUE> {

    /** DO IN ANOTHER THREAD */
    void refreshCache();

    void updateCache(Collection<VALUE> values);

    void updateCache(VALUE value);

    KEY getKey(VALUE value);

    boolean remove(KEY key);

    boolean remove(VALUE value);

    void clear();

    VALUE find(KEY key);

    List<VALUE> getAll();
}