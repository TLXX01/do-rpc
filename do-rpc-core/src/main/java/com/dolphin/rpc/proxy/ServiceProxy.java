package com.dolphin.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.dolphin.rpc.RpcApplication;
import com.dolphin.rpc.config.RpcConfig;
import com.dolphin.rpc.constant.ProtocolConstant;
import com.dolphin.rpc.constant.RpcConstant;
import com.dolphin.rpc.fault.retry.RetryStrategy;
import com.dolphin.rpc.fault.retry.RetryStrategyFactory;
import com.dolphin.rpc.fault.tolerant.TolerantStrategy;
import com.dolphin.rpc.fault.tolerant.TolerantStrategyFactory;
import com.dolphin.rpc.loadbalancer.LoadBalancer;
import com.dolphin.rpc.loadbalancer.LoadBalancerFactory;
import com.dolphin.rpc.model.RpcRequest;
import com.dolphin.rpc.model.RpcResponse;
import com.dolphin.rpc.model.ServiceMetaInfo;
import com.dolphin.rpc.protocol.*;
import com.dolphin.rpc.registry.Registry;
import com.dolphin.rpc.registry.RegistryFactory;
import com.dolphin.rpc.serializer.Serializer;
import com.dolphin.rpc.serializer.SerializerFactory;
import com.dolphin.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 *
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // rpc 请求
            // 使用重试机制
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }
    }
}
