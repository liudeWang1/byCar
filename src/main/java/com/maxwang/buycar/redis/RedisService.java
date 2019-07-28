package com.maxwang.buycar.redis;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.maxwang.buycar.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.security.Key;
import java.util.*;

@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;



    public <T> T get(KeyPrefix prefix,String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //生成真正的key
            String realKey=prefix.getPrefix()+key;

            String str = jedis.get(realKey);
            //我们需要将得到的str转化为T
            T t = stringToBean(str,clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }

    }


    public <T> boolean set(KeyPrefix prefix,String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            String str = beanToString(value);

            if (str == null) {
                return false;
            }
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            int seconds=prefix.expireSeconds();
            if (seconds<=0){
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey,seconds,str);
            }

            return true;
        } finally {
            returnToPool(jedis);
        }

    }

    /**
     * 判断键是否存在值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //生成真正的key
            String realKey=prefix.getPrefix()+key;

            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }

    }

    /**
     * incr方法
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //生成真正的key
            String realKey=prefix.getPrefix()+key;

            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * decr方法
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //生成真正的key
            String realKey=prefix.getPrefix()+key;

            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }

    }


    /**
     * 添加到有序集合中
     * @param prefix
     * @param key
     * @param scope
     * @param member
     */
    public boolean zSet(KeyPrefix prefix,String key,double scope,String member){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            String realKey=prefix.getPrefix()+key;

            jedis.zadd(realKey,scope,member);

            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 实现集合的键自动增加
     * @param prefix
     * @param key
     * @param incr
     * @param member
     * @return
     */
    public boolean addScope(KeyPrefix prefix,String key,double incr,String member){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;

            jedis.zincrby(realKey,incr,member);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 获取有序集合的某个范围的值
     * @param prefix
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> getRange(KeyPrefix prefix,String key,long start,long end){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            Set<String> range = jedis.zrevrange(realKey,start,end);
            return range;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 得到有序集合中所有成员和分数
     * @param prefix
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Tuple> getRangeAll(KeyPrefix prefix,String key,long start,long end){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;

            Set<Tuple> set = jedis.zrevrangeWithScores(realKey, start, end);

            System.out.println(".............redis"+jedis.zrevrangeWithScores(realKey, start, end));

            List<Tuple> list = Lists.newArrayList(set.toArray(new Tuple[]{}));

            return list;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 返回集合中某个记录的分值
     * @param prefix
     * @param key
     * @param member
     * @return
     */
    public Double getScope(KeyPrefix prefix,String key,String member){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;

            Double scope = jedis.zscore(realKey,member);
            return scope;
        } finally {
            returnToPool(jedis);
        }
    }

    public boolean lPush(KeyPrefix prefix,String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;

            long result = jedis.lpush(realKey,value);
            if (result == 0){
               return false;
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    public List<String> lRange( KeyPrefix prefix,String key, long start, long end){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            List<String> list = jedis.lrange(realKey,start,end);

            return list;
        } finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    /**
     * 将bean转化为String
     *
     * @param value
     * @return
     */
    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();

        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 将String转化为Bean
     * am str
     *
     * @param <T>
     * @param clazz
     * @return
     */
    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null||str.length() <= 0||clazz==null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }





}
