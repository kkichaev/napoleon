package com.ashberrysoft.leadertask.interfaces;

import java.util.List;

public interface CacheHolder<DATA> {

    void refreshCache();
    
    Iterable<DATA> getListData() throws Exception;
    
    void updateCache(Iterable<DATA> list);
    
    void updateCache(DATA data);
    
    int getKey(DATA data);
    
    void removeFromCache(int hash);
    
    void removeFromCache(DATA data);
    
    DATA findData(int hash);
    
    List<DATA> findData(List<Integer> hashes);
    
    List<DATA> getData();
    
    boolean isEmpty();
}