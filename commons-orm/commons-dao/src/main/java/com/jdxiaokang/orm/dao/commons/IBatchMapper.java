package com.jdxiaokang.orm.dao.commons;

import com.jdxiaokang.orm.dao.utils.BatchSQLUtil;
import org.apache.ibatis.annotations.InsertProvider;

import java.util.List;

/**
 * created by zwq on 2019/1/9
 * 批量插入，暂时只支持这个列表属性赋值情况相同
 *
 * @author Administrator
 */
public interface IBatchMapper<T> {
    /**
     * 测试批量~
     *
     * @param list 列表
     * @return 行数
     */
    @InsertProvider(type = BatchSQLUtil.class, method = "batchInsert")
    int insertBatch(List<T> list);
}
