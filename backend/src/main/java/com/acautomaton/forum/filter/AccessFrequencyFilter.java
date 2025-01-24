package com.acautomaton.forum.filter;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.util.RedisService;
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
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class AccessFrequencyFilter extends OncePerRequestFilter {
    RedisService redisService;

    @Autowired
    public AccessFrequencyFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestIp = getIp(request);
        String keyOfBan = "Ban_" + requestIp;
        String keyOfRequestTimes = "RequestTimes_" + requestIp;

        if (redisService.hasKey(keyOfBan)) {
            String result = new ObjectMapper().writeValueAsString(Response.tooFrequentError("请求过于频繁，请休息一会后再试。"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        if (redisService.hasKey(keyOfRequestTimes)) {
            Integer times = (Integer) redisService.get(keyOfRequestTimes);
            if (times >= 600) {
                redisService.set(keyOfBan, 1, 60 * 15L);
                log.warn("IP: {} 被BAN", requestIp);
                redisService.deleteKeys(keyOfRequestTimes);
                String result = new ObjectMapper().writeValueAsString(Response.tooFrequentError("请求过于频繁，请休息一会后再试。"));
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(result);
                return;
            }
            long expire = redisService.getExpire(keyOfRequestTimes);
            if (expire <= 1L) {
                expire = 1L;
            }
            redisService.set(keyOfRequestTimes, times + 1, expire);
        }
        else {
            redisService.set(keyOfRequestTimes, 1, 60L);
        }
        request.setAttribute("request-ip-from", requestIp);
        filterChain.doFilter(request, response);
    }

    public static String getIp(HttpServletRequest request) {
        List<String> ipHeadList = Stream.
                of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP",
                "x-forwarded-for", "proxy-client-iP", "wl-proxy-client-ip", "http_client_ip", "x-real-ip").toList();
        for (String ipHead : ipHeadList) {
            if (checkIP(request.getHeader(ipHead))) {
                return request.getHeader(ipHead).split(",")[0];
            }
        }
        return "127.0.0.1";
    }

    private static boolean checkIP(String ip) {
        return !(null == ip || ip.isEmpty() || "unknown".equalsIgnoreCase(ip));
    }
}
