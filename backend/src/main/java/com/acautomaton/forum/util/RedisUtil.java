package com.acautomaton.forum.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public final class RedisUtil {
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public void setExpireAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteKeys(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            }
            else {
                redisTemplate.delete(List.of(key));
            }
        }
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        }
        else {
            set(key, value);
        }
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Object hashMapGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    public Map<Object, Object> hashMapGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void hashMapSet(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public void hashMapSet(String key, Map<String, Object> map, long time) {
        hashMapSet(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    public void hashMapSet(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    public void hashMapSet(String key, String item, Object value, long time) {
        hashMapSet(key, item, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    public void hashMapDelete(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    public boolean hashMapHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    public double hashMapIncrement(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    public Set<Object> setGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean setHashKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long setSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Long setSetAndTime(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    public Long setGetSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    public List<Object> listGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long listGetListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Object listGetIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public void listSet(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public void listSet(String key, Object value, long time) {
        redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    public void listSet(String key, List<Object> value) {
        redisTemplate.opsForList().rightPushAll(key, value);
    }

    public void listSet(String key, List<Object> value, long time) {
        redisTemplate.opsForList().rightPushAll(key, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    public void listUpdateIndex(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    public Long listRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
}