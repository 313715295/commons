package com.jdxiaokang.core.canal.abstracts;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;

import com.jdxiaokang.core.canal.CanalEventListener;
import com.jdxiaokang.core.canal.config.CanalConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.common.canal.abstracts
 * @description:
 * @author: MuYu
 * @create: 2019-03-05 15:25
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public abstract class AbstractBasicMessageTransponder extends AbstractMessageTransponder {

    /**
     * 日志记录
     */
    private final static Logger logger = LoggerFactory.getLogger(AbstractBasicMessageTransponder.class);

    /**
     * @param connector     canal 连接器
     * @param config        canal 连接配置
     * @param listeners     实现接口层的 canal 监听器
     */
    public AbstractBasicMessageTransponder(CanalConnector connector, CanalConfig config, Map<String,List<CanalEventListener>> listeners) {
        super(connector, config, listeners);
    }

    /**
     * 处理消息
     */
    @Override
    protected void distributeEvent(Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            List<CanalEntry.EntryType> ignoreEntryTypes = getIgnoreEntryTypes();
            if (ignoreEntryTypes != null
                    && ignoreEntryTypes.stream().anyMatch(t -> entry.getEntryType() == t)) {
                continue;
            }
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());

            } catch (Exception e) {
                throw new CanalClientException("错误 ##转换错误 , 数据信息:" + entry.toString(),
                        e);
            }

            distributeByListener(entry.getHeader().getTableName(), rowChange);
        }
    }

    /**
     * 处理监听信息
     * @param tableName   表名
     * @param rowChange   參數
     * @return
     */
    protected void distributeByListener(String tableName,CanalEntry.RowChange rowChange) {
        if (listeners != null && !listeners.isEmpty()) {
            if(StringUtils.isNotBlank(tableName)){
                List<CanalEventListener> canalEventListenerList = listeners.get(tableName);
                if(canalEventListenerList != null && !canalEventListenerList.isEmpty()){
                    logger.info("监听表{} 数据发生变化，变化类型为：",tableName);
                    for (CanalEventListener canalEventListener : canalEventListenerList) {
                        canalEventListener.onEvent(tableName,rowChange);
                    }
                }else{
                    logger.warn("监听表名:"+tableName+",找不到对应监听器");
                }
            }else{
                logger.warn("监听表名为空，无法处理");
            }
        }
    }

    /**
     * 返回一个空集合
     * @param
     * @return
     */
    protected List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Collections.emptyList();
    }
}
