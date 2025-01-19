package com.acautomaton.forum.util;

import com.acautomaton.forum.entity.SecurityUser;
import com.acautomaton.forum.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalUtil {
    public static User getUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (securityUser == null) {
            return null;
        }
        return securityUser.getUser();
    }
}
