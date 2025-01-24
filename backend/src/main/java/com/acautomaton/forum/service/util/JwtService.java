package com.acautomaton.forum.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

@Service
public class JwtService {
    @Value("${project.jwt.secretkey}")
    private String BACKEND_SECRET_KEY;
    @Value("${project.jwt.timeoutDays}")
    private Integer BACKEND_TIME_OUT_DAYS;

    public String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, BACKEND_TIME_OUT_DAYS * 24);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(BACKEND_SECRET_KEY));
    }

    public void verify(String token) {
        JWT.require(Algorithm.HMAC256(BACKEND_SECRET_KEY)).build().verify(token);
    }

    private DecodedJWT getToken(String token) {
        return JWT.require(Algorithm.HMAC256(BACKEND_SECRET_KEY)).build().verify(token);
    }

    public String decode(String token, String key) {
        return getToken(token).getClaims().get(key).asString();
    }
}
