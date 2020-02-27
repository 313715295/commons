package com.jdxiaokang.commons.core.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.jdxiaokang.commons.core.canal.config.CanalConfig;
import com.jdxiaokang.commons.core.canal.CanalEventListener;


import java.util.List;
import java.util.Map;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal.client
 * @description: 信息转换工厂类接口层
 * @author: MuYu
 * @create: 2019-03-05 10:11
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public interface TransponderFactory {

    /**
     * @param connector        canal 连接工具
     * @param config           canal 链接信息
     * @param listeners 实现接口的监听器
     * @return
     */
    MessageTransponder newTransponder(CanalConnector connector, CanalConfig config, Map<String, List<CanalEventListener>> listeners);
}
