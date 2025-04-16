package com.dolphin.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;

import java.io.File;
import java.util.Map;

/**
 * 配置工具类
 */
public class ConfigUtils {

    private static final Log log = LogFactory.get();

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
//         尝试加载 .properties 文件
        String propertiesFileName = String.valueOf(configFileBuilder.append(".properties"));
        Props props = new Props(propertiesFileName);
        T propertiesBean = props.toBean(tClass, prefix);
        if (propertiesBean != null) {
            return propertiesBean;
        }

        //todo 尝试加载 .yml 文件  未实现成功
//        String ymlFileName = String.valueOf(configFileBuilder.append(".yml"));
//        Dict dict = YamlUtil.loadByPath(ymlFileName);
//        T ymlBean = BeanUtil.toBean(dict.getBean(prefix), tClass);
//        if (ymlBean != null){
//            return ymlBean;
//        }

        throw new IllegalArgumentException("未找到配置文件");
    }
}
