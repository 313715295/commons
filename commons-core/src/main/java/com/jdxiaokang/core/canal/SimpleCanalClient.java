package com.jdxiaokang.core.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.jdxiaokang.core.annotation.CanalClientListener;
import com.jdxiaokang.core.canal.abstracts.AbstractCanalClient;
import com.jdxiaokang.core.canal.config.CanalConfig;
import com.jdxiaokang.orm.dao.utils.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal
 * @description: Canal客户端
 * @author: MuYu
 * @create: 2019-03-05 10:15
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
@Slf4j
public class SimpleCanalClient extends AbstractCanalClient {

    /**
     * 声明一个线程池
     */
    private ThreadPoolExecutor executor;

    private final static int COREPOOLSIZE = 5;

    private final static int MAXIMUMPOOLSIZE = 20;

    private final static Long KEEP_ALIVE_TIME = 120L;

    private Map<String,List<CanalEventListener>> canalEventListenerMap = new HashMap<>();

    public SimpleCanalClient(CanalConfig canalConfig) {
        super(canalConfig);
        //默认核心线程数5个，最大线程数20个
        executor = new ThreadPoolExecutor(COREPOOLSIZE, MAXIMUMPOOLSIZE,KEEP_ALIVE_TIME, TimeUnit.SECONDS,new SynchronousQueue<>(), Executors.defaultThreadFactory());
        //初始化监听器
        initListeners();
    }

    @Override
    protected void process(CanalConnector connector) {
       executor.submit(factory.newTransponder(connector,getCanalConfig(), canalEventListenerMap));
    }

    /**
     * 初始化监听器
     */
    private void initListeners() {
        log.info("{}: 监听器正在初始化....", Thread.currentThread().getName());
        List<CanalEventListener> canalEventListenerList = SpringBeanFactoryUtils.getBeansOfType(CanalEventListener.class);
        if(canalEventListenerList == null){
            log.warn("{}:Canal监听器未找到");
        }else{
            for (CanalEventListener canalEventListener : canalEventListenerList) {
                CanalClientListener canalClientListener = canalEventListener.getClass().getAnnotation(CanalClientListener.class);
                String tableName = canalClientListener.tables();
                List<CanalEventListener> canalEventListeners = canalEventListenerMap.get(tableName);
                if(canalEventListeners == null){
                    canalEventListeners = new ArrayList<>();
                    canalEventListenerMap.put(tableName,canalEventListeners);
                }
                canalEventListeners.add(canalEventListener);
                log.info("{} Canal监听器",canalEventListener.getClass().getName()+"注册成功，监听表名：" + canalClientListener.tables());
            }
        }
    }

    @Override
    public void stop() {
        //停止 canal 客户端
        super.stop();
        //线程池关闭
        executor.shutdown();
    }
}
