package com.acautomaton.forum.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Stream;

public class IPUtil {
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
