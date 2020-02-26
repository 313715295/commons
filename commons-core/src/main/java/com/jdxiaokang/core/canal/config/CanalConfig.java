package com.jdxiaokang.core.canal.config;

import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal.config
 * @description: Canal配置类
 * @author: MuYu
 * @create: 2019-03-05 10:04
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
@Data
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CanalConfig {

    /**
     * 是否是集群模式
     */
    private boolean clusterEnabled;

    /**
     * zookeeper 地址
     */
    private String zookeeperAddress;

    /**
     * canal 服务器地址，默认是本地的环回地址
     */
    private String host = "127.1.1.1";

    /**
     * canal 服务设置的端口，默认 11111
     */
    private int port = 11111;

    /**
     * instances
     */
    private String destination;

    /**
     * 集群 设置的用户名
     */
    private String userName = "";

    /**
     * 集群 设置的密码
     */
    private String password = "";

    /**
     * 批量从 canal 服务器获取数据的最多数目
     */
    private int batchSize = 1000;

    /**
     * 是否有过滤规则
     */
    private String subscribe = ".*\\\\..*";

    /**
     * 当错误发生时，重试次数
     */
    private int retryCount = 5;

    /**
     * 信息捕获心跳时间
     */
    private long acquireInterval = 1000;

}
