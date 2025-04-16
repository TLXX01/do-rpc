package com.dolphin.example.provider;

import com.dolphin.example.common.service.UserService;
import com.dolphin.rpc.bootstrap.ProviderBootstrap;
import com.dolphin.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * provider
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        System.out.println("this is provider!");
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo<UserService>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化

        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
