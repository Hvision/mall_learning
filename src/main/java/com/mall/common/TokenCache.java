package com.mall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wxhong on 2018/5/1.
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX = "token_";
    //LRU算法
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1024).
            maximumSize(10240).expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
       //默认数据加载实现，当调用的key没有对应的值时，调用该方法进行加载。
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });

    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
        } catch (ExecutionException e) {
            logger.error("localCache get error", e);
        }

        return value;
    }


}
