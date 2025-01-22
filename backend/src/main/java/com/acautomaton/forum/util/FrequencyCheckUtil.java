package com.acautomaton.forum.util;

import com.acautomaton.forum.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FrequencyCheckUtil {
    RedisUtil redisUtil;
    @Autowired
    public FrequencyCheckUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public boolean privateProjectFrequencyAccess(String checkProjectName, User user, Long expire, Integer timesPerExpire, Long expireOfBan) {
        String keyOfBan = "Ban_" + checkProjectName + "_" + user.getUid();
        String keyOfRequestTimes = "RequestTimes_ " + checkProjectName + "_" + user.getUid();
        return check(expire, timesPerExpire, expireOfBan, keyOfBan, keyOfRequestTimes);
    }

    public boolean publicProjectFrequencyAccess(String checkProjectName, Long expire, Integer timesPerExpire, Long expireOfBan) {
        String keyOfBan = "Ban_" + checkProjectName;
        String keyOfRequestTimes = "RequestTimes_ " + checkProjectName;
        return check(expire, timesPerExpire, expireOfBan, keyOfBan, keyOfRequestTimes);
    }

    private boolean check(Long expire, Integer timesPerExpire, Long expireOfBan, String keyOfBan, String keyOfRequestTimes) {
        if (redisUtil.hasKey(keyOfBan)) {
            return false;
        }
        if (redisUtil.hasKey(keyOfRequestTimes)) {
            Integer times = (Integer) redisUtil.get(keyOfRequestTimes);
            if (times >= timesPerExpire) {
                redisUtil.set(keyOfBan, expireOfBan);
                log.warn("{} 访问次数过于频繁，已被临时停止访问", keyOfRequestTimes);
                redisUtil.deleteKeys(keyOfRequestTimes);
                return false;
            }
            long nowExpire = redisUtil.getExpire(keyOfRequestTimes);
            if (nowExpire <= 1L) {
                nowExpire = 1L;
            }
            redisUtil.set(keyOfRequestTimes, times + 1, nowExpire);
        }
        else {
            redisUtil.set(keyOfRequestTimes, 1, expire);
        }
        return true;
    }
}
