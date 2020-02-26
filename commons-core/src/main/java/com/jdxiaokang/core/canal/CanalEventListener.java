package com.jdxiaokang.core.canal;
import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.web.config
 * @description: Canal监听
 * @author: MuYu
 * @create: 2019-02-16 15:31
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public interface CanalEventListener {

    /**
     * 监听处理事件
     * @param tableName   表名
     * @param rowChange   詳細參數
     * @return
     */
    void onEvent(String tableName, CanalEntry.RowChange rowChange);
}