package com.jdxiaokang.core.canal.transfer;

import com.alibaba.otter.canal.client.CanalConnector;
import com.jdxiaokang.core.canal.CanalEventListener;
import com.jdxiaokang.core.canal.abstracts.AbstractBasicMessageTransponder;
import com.jdxiaokang.core.canal.config.CanalConfig;

import java.util.List;
import java.util.Map;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.common.canal.transfer
 * @description: 默认信息转换器
 * @author: MuYu
 * @create: 2019-03-05 15:24
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public class DefaultMessageTransponder extends AbstractBasicMessageTransponder {
    /**
     * @param connector canal 连接器
     * @param config    canal 连接配置
     * @param listeners 实现接口层的 canal 监听器
     */
    public DefaultMessageTransponder(CanalConnector connector, CanalConfig config, Map<String,List<CanalEventListener>> listeners) {
        super(connector, config, listeners);
    }
}
