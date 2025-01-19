package com.acautomaton.forum.filter;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.util.IPUtil;
import com.acautomaton.forum.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AccessFrequencyFilter extends OncePerRequestFilter {
    RedisUtil redisUtil;

    @Autowired
    public AccessFrequencyFilter(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestIp = IPUtil.getIp(request);
        String keyOfBan = "Ban_" + requestIp;
        String keyOfRequestTimes = "RequestTimes_" + requestIp;

        if (redisUtil.hasKey(keyOfBan)) {
            String result = new ObjectMapper().writeValueAsString(Response.tooFrequentError("请求过于频繁，请休息一会后再试。"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        if (redisUtil.hasKey(keyOfRequestTimes)) {
            Integer times = (Integer) redisUtil.get(keyOfRequestTimes);
            if (times >= 600) {
                redisUtil.set(keyOfBan, 1, 60 * 15L);
                log.warn("IP: {} 被BAN", requestIp);
                redisUtil.deleteKeys(keyOfRequestTimes);
                String result = new ObjectMapper().writeValueAsString(Response.tooFrequentError("请求过于频繁，请休息一会后再试。"));
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(result);
                return;
            }
            long expire = redisUtil.getExpire(keyOfRequestTimes);
            if (expire <= 1L) {
                expire = 1L;
            }
            redisUtil.set(keyOfRequestTimes, times + 1, expire);
        }
        else {
            redisUtil.set(keyOfRequestTimes, 1, 60L);
        }
        filterChain.doFilter(request, response);
    }
}
