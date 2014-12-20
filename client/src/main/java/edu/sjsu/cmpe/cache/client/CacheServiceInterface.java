package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);
    
    public int getResponseCode();
    
    public void delete(long key);

    public void put(long key, String value);
    
    public void asynchroPut(long key, String value);
    
    public void asynchroGet(long key, final ConcurrentHashMap<String,String> valueMap,final CountDownLatch cntLatch);
    
    }
