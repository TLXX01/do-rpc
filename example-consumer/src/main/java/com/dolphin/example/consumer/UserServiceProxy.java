package com.dolphin.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.dolphin.example.common.model.User;
import com.dolphin.example.common.service.UserService;
import com.dolphin.rpc.model.RpcRequest;
import com.dolphin.rpc.model.RpcResponse;
import com.dolphin.rpc.serializer.JdkSerializer;
import com.dolphin.rpc.serializer.Serializer;

public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        //指定序列化器
        final Serializer serializer = new JdkSerializer();

        //发送请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class<?>[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse response = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()){
                result = response.bodyBytes();
            }
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
