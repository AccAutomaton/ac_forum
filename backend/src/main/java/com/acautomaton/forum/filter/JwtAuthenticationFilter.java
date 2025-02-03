package com.acautomaton.forum.filter;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.util.JwtService;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    UserDetailsService userDetailsService;
    JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            authorization = getUrlQueryMap(request).get("Authorization");
            if (authorization != null) {
                authorization = authorization.replace("%20", " ");
            }
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorization.substring(7);
        try {
            jwtService.verify(jwtToken);
        }
        catch (TokenExpiredException e) {
            String result = new ObjectMapper().writeValueAsString(Response.jwtError("Token超时,请重新登录"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        catch (SignatureVerificationException e) {
            String result = new ObjectMapper().writeValueAsString(Response.jwtError("非法签名"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        catch (AlgorithmMismatchException e) {
            String result = new ObjectMapper().writeValueAsString(Response.jwtError("加密错误"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        catch (Exception e) {
            log.warn(e.toString());
            String result = new ObjectMapper().writeValueAsString(Response.jwtError("Token错误"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }

        String uid, username;
        try {
            uid = jwtService.decode(jwtToken, "uid");
            username = jwtService.decode(jwtToken, "username");
            if (!username.trim().isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("收到用户 {} (用户名: {}) 访问 {} 的请求", uid, username, request.getRequestURI());
                if (!userDetails.isAccountNonLocked()) {
                    throw new ForumIllegalAccountException("账户被锁定");
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (UsernameNotFoundException e) {
            String result = new ObjectMapper().writeValueAsString(Response.jwtError("Token用户不存在"));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        catch (ForumIllegalAccountException e) {
            String result = new ObjectMapper().writeValueAsString(Response.jwtError(e.getMessage()));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(result);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static Map<String, String> getUrlQueryMap(HttpServletRequest request) {
        String urlQueryString = request.getQueryString();
        Map<String, String> queryMap = new HashMap<>();
        String[] arrSplit;
        if (urlQueryString != null) {
            arrSplit = urlQueryString.split("&");
            for (String strSplit : arrSplit) {
                String[] arrSplitEqual = strSplit.split("=");
                if (arrSplitEqual.length > 1) {
                    queryMap.put(arrSplitEqual[0], arrSplitEqual[1]);
                }
                else {
                    if (!"".equals(arrSplitEqual[0])) {
                        queryMap.put(arrSplitEqual[0], "");
                    }
                }
            }
        }
        return queryMap;
    }
}
