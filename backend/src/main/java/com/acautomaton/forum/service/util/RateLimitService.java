package com.acautomaton.forum.service.util;

import com.acautomaton.forum.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RateLimitService {
    RedisService redisService;
    @Autowired
    public RateLimitService(RedisService redisService) {
        this.redisService = redisService;
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
        if (redisService.hasKey(keyOfBan)) {
            return false;
        }
        if (redisService.hasKey(keyOfRequestTimes)) {
            Integer times = (Integer) redisService.get(keyOfRequestTimes);
            if (times >= timesPerExpire) {
                redisService.set(keyOfBan, expireOfBan);
                log.warn("{} 访问次数过于频繁，已被临时停止访问", keyOfRequestTimes);
                redisService.deleteKeys(keyOfRequestTimes);
                return false;
            }
            long nowExpire = redisService.getExpire(keyOfRequestTimes);
            if (nowExpire <= 1L) {
                nowExpire = 1L;
            }
            redisService.set(keyOfRequestTimes, times + 1, nowExpire);
        }
        else {
            redisService.set(keyOfRequestTimes, 1, expire);
        }
        return true;
    }
}
