package com.acautomaton.forum.service.inte;

import com.acautomaton.forum.entity.User;

public interface UserService {
    User getUserByUid(String uid);
    User getUserByUid(Integer uid);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User registerUser(User user);
    void updateUser(User user);
}
