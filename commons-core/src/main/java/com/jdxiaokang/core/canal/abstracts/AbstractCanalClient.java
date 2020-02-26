package com.jdxiaokang.core.canal.abstracts;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.jdxiaokang.core.canal.client.CanalClient;
import com.jdxiaokang.core.canal.client.TransponderFactory;
import com.jdxiaokang.core.canal.config.CanalConfig;
import com.jdxiaokang.core.canal.transfer.DefaultMessageTransponder;


import java.net.InetSocketAddress;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal.abstracts
 * @description: Canal 客户端抽象类
 * @author: MuYu
 * @create: 2019-03-05 10:03
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public abstract class AbstractCanalClient implements CanalClient {

    /**
     * 运行状态
     */
    private volatile boolean running;

    /**
     * canal 配置
     */
    private CanalConfig canalConfig;

    /**
     * 转换工厂类
     */
    protected final TransponderFactory factory;


    /**
     * 构造方法，初始化 canal 的配置以及转换信息的工厂实例
     *
     * @param canalConfig
     * @return
     */
    protected AbstractCanalClient(CanalConfig canalConfig) {
        this.canalConfig = canalConfig;
        this.factory = (connector, config, listeners) -> new DefaultMessageTransponder(connector, config, listeners);
    }

    /**
     * 开启 canal 客户端
     * @param
     * @return
     */
    @Override
    public void start() {
        process(processInstanceEntry(canalConfig));
    }

    /**
     * 初始化 canal 连接
     * @param connector
     */
    protected abstract void process(CanalConnector connector);

    /**
     * 处理 canal 连接实例
     * @return
     */
    private CanalConnector processInstanceEntry(CanalConfig canalConfig) {
        //声明连接
        CanalConnector connector;
        //是否是集群模式
        if (canalConfig.isClusterEnabled()) {
            //zookeeper 连接集合
            //若集群的话，使用 newClusterConnector 方法初始化
            connector = CanalConnectors.newClusterConnector(canalConfig.getZookeeperAddress(), canalConfig.getDestination(), canalConfig.getUserName(), canalConfig.getPassword());
        } else {
            //若不是集群的话，使用 newSingleConnector 初始化
            connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalConfig.getHost(), canalConfig.getPort()), canalConfig.getDestination(), canalConfig.getUserName(), canalConfig.getPassword());
        }

        return connector;
    }

    /**
     * 停止 canal 客户端
     */
    @Override
    public void stop() {
        setRunning(false);
    }

    /**
     * 返回 canal 客户端的状态
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * 设置 canal 客户端状态
     */
    private void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * 获取Canal配置信息
     * @return
     */
    public CanalConfig getCanalConfig(){
        return this.canalConfig;
    }
}
