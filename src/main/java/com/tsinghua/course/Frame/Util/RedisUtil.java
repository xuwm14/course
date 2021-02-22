package com.tsinghua.course.Frame.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @描述 redis数据库相关功能的封装，不完全，可以自己添加封装
 **/
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /** 是否存在某个key */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    /** 批量删除对象 */
    public Long deleteKeys(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }
    /** 设置key并设置过期时间，指定时间后会删除key */
    public void setKey(String key, Object val, long expTime) {
        redisTemplate.opsForValue().set(key, val.toString(), expTime, TimeUnit.SECONDS);
    }
    /** 设置key的过期时间 */
    public void setKeyExpTime(String key, long expTime) {
        redisTemplate.expire(key, expTime, TimeUnit.SECONDS);
    }
    /** 设置key */
    public void setKey(String key, Object val) {
        redisTemplate.opsForValue().set(key, val.toString());
    }
    /** 根据key获取String对象 */
    public String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    /** 设置redis Map对象的val */
    public void setMapVal(String key, Object mapKey, Object val) {
        redisTemplate.boundHashOps(key).put(mapKey.toString(), val.toString());
    }
    /** 获取redis Map对象的所有key */
    public Set<String> getMapKeys(String key) {
        Set<Object> srcKeys = redisTemplate.boundHashOps(key).keys();
        Set<String> keys = new HashSet<>();
        for (Object src:srcKeys)
            keys.add(src.toString());
        return keys;
    }
    /** 获取redis Map对象 */
    public Map<Object, Object> getAllMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }
    /** Map中不存在某个key才设置，否则不设置 */
    public boolean setMapValNx(String key, String mapKey, Object val) {
        return redisTemplate.boundHashOps(key).putIfAbsent(mapKey, val.toString());
    }
    /** 获取Map的val */
    public String getMapVal(String key, String mapKey) {
        return (String) redisTemplate.boundHashOps(key).get(mapKey);
    }
    /** 对Map中某个val增加值，只能对Map中的数字值操作，否则会报错 */
    public int incMapVal(String key, String mapKey, int num) {
        return Math.toIntExact(redisTemplate.boundHashOps(key).increment(mapKey, num));
    }
    /** 批量设置Map对象的key和val */
    public void setMultiMapVal(String key, Object... mapKeyValues) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < mapKeyValues.length - 1; i += 2)
            map.put(mapKeyValues[i].toString(), mapKeyValues[i + 1].toString());
        redisTemplate.boundHashOps(key).putAll(map);
    }
    /** 批量获取Map对象的val */
    public String[] getMultiMapVal(String key, String... mapKes) {
        List<Object> objects = redisTemplate.boundHashOps(key).multiGet(Arrays.asList(mapKes));
        String[] ret = new String[objects.size()];
        for (int i = 0; i < objects.size(); ++i) {
            Object obj = objects.get(i);
            if (obj != null)
                ret[i] = obj.toString();
        }
        return ret;
    }
    /** 批量删除Map对象的key */
    public void deleteMapKey(String key, Object... mapKey) {
        String[] strKeys = new String[mapKey.length];
        for (int i = 0; i < mapKey.length; ++i)
            strKeys[i] = mapKey[i].toString();
        redisTemplate.boundHashOps(key).delete(strKeys);
    }
    /** 获取集合对象 */
    public Set<String> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }
    /** 获取集合大小 */
    public int getSetSize(String key) {
        return (int)(long)redisTemplate.boundSetOps(key).size();
    }
    /** 获取集合中所有元素 */
    public Set<String> getSetMembers(String key) {
        return redisTemplate.boundSetOps(key).members();
    }
    /** 随机获取集合中一个元素 */
    public String getRandSetMember(String key) {
        return redisTemplate.boundSetOps(key).randomMember();
    }
    /** val是否在集合内 */
    public boolean isSetMember(String key, String val) {
        return redisTemplate.boundSetOps(key).isMember(val);
    }
    /** map中是否包含对应键 */
    public boolean mapContainKey(String key, Object mapKey) {
        return redisTemplate.boundHashOps(key).hasKey(mapKey.toString());
    }
    /** 批量增加对象到集合中 */
    public int addToSet(String key, String... val) {
        return (int)(long)redisTemplate.boundSetOps(key).add(val);
    }
    /** 从集合中删除对象 */
    public long removeFromSet(String key, String val) {
        return redisTemplate.boundSetOps(key).remove(val);
    }
}
