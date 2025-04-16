package com.dolphin.example.provider;


import com.dolphin.example.common.model.User;
import com.dolphin.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getUsername());
        return user;
    }

    public short getNumber() {
        return 1;
    }
}
