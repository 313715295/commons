package com.jdxiaokang.commons.core.canal.abstracts;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.jdxiaokang.commons.core.canal.client.MessageTransponder;
import com.jdxiaokang.commons.core.canal.config.CanalConfig;
import com.jdxiaokang.commons.core.canal.CanalEventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.common.canal.abstracts
 * @description:
 * @author: MuYu
 * @create: 2019-03-05 15:20
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public abstract class AbstractMessageTransponder implements MessageTransponder {
    /**
     * canal 连接器
     */
    private final CanalConnector connector;

    /**
     * custom 连接配置
     */
    protected final CanalConfig config;

    /**
     * canal 服务指令
     */
    protected final String destination;

    /**
     * 实现接口的 canal 监听器(上：表内容，下：表结构)
     */
    protected final Map<String,List<CanalEventListener>> listeners = new HashMap<>();

    /**
     * canal 客户端的运行状态
     */
    private volatile boolean running = true;

    /**
     * 日志记录
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageTransponder.class);

    /**
     * 构造方法，初始化参数
     * @param connector     canal 连接器
     * @param config        canal 连接配置
     * @param listeners     实现接口层的 canal 监听器(表结构)
     */
    public AbstractMessageTransponder(CanalConnector connector, CanalConfig config, Map<String,List<CanalEventListener>> listeners) {
        //参数处理
        Objects.requireNonNull(connector, "连接器不能为空!");
        Objects.requireNonNull(config, "配置信息不能为空!");
        //参数初始化
        this.connector = connector;
        this.destination = config.getDestination();
        this.config = config;
        if (listeners != null) {
            this.listeners.putAll(listeners);
        }
    }

    /**
     * 线程搞起来
     *
     * @param
     * @return
     */
    @Override
    public void run() {
        //canal 连接
        try {
            connector.connect();
            if (!StringUtils.isEmpty(config.getSubscribe())) {
                connector.subscribe(config.getSubscribe());
            } else {
                connector.subscribe();
            }
            connector.rollback();
        } catch (Exception e){
            e.printStackTrace();
            logger.info(" canal connector error : {}",e);

        }
        //错误重试次数
        int errorCount = config.getRetryCount();
        //捕获信息的心跳时间
        final long interval = config.getAcquireInterval();
        //当前线程的名字
        final String threadName = Thread.currentThread().getName();
        //若线程正在进行
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                //获取消息
                Message message = connector.getWithoutAck(config.getBatchSize());
                //获取消息 ID
                long batchId = message.getId();
                //消息数
                int size = message.getEntries().size();
                //debug 模式打印消息数
                //若是没有消息
                if (batchId == -1 || size == 0) {
                    //休息
                    Thread.sleep(interval);
                } else {
                    //处理消息
                    distributeEvent(message);
                }
                //确认消息已被处理完
                connector.ack(batchId);
                //若是 debug模式
            } catch (CanalClientException e) {
                //每次错误，重试次数减一处理
                errorCount--;
                logger.error(threadName + ": 发生错误!! ", e);
                try {
                    //等待时间
                    Thread.sleep(interval);
                } catch (InterruptedException e1) {
                    errorCount = 0;
                }
            } catch (InterruptedException e) {
                //线程中止处理
                errorCount = 0;
                connector.rollback();
            } finally {
                //若错误次数小于 0
                if (errorCount <= 0) {
                    //停止 canal 客户端
                    stop();
                    logger.info("{}: canal 客户端已停止... ", Thread.currentThread().getName());
                }
            }
        }
        //停止 canal 客户端
        stop();
        logger.info("{}: canal 客户端已停止. ", Thread.currentThread().getName());
    }

    /**
     * 处理监听的事件
     */
    protected abstract void distributeEvent(Message message);

    /**
     * 停止 canal 客户端
     */
    void stop() {
        running = false;
    }
}
