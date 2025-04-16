package com.dolphin.example.consumer;

import com.dolphin.example.common.model.User;
import com.dolphin.example.common.service.UserService;
import com.dolphin.rpc.bootstrap.ConsumerBootstrap;
import com.dolphin.rpc.proxy.ServiceProxyFactory;

/**
 * Hello world!
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setUsername("dolphin");
        // 调用服务

        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println("用户名：" + newUser.getUsername());
        } else {
            System.out.println("用户不存在");
        }

    }
}
