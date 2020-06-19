package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class tokenCache {
    private static Logger logger = LoggerFactory.getLogger(tokenCache.class);

    public static final String TOKEN_PREFIX="token_";
    //LRU算法清除
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        //默认的数据加载实现，当调用get取值得时候，如果key没有在缓存中命中，就会调用这个方法
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value =null;
        try{
            value = localCache.get(key);
            if(value.equals("null")){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache error",e);
        }
        return null;
    }
}
