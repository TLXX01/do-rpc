package com.dolphin.example.consumer;

import com.dolphin.rpc.config.RpcConfig;
import com.dolphin.rpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 *
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
