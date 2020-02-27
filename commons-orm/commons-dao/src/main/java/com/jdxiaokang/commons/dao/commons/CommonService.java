package com.jdxiaokang.commons.dao.commons;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jdxiaokang.commons.dao.utils.SpringBeanFactoryUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 通用方法，默认实现
 * @date 2019/12/28
 */
public interface CommonService<T> {


    /**
     * 一次插入多少数量
     */
    int INSERT_SIZE = 1000;

    /**
     * 批量插入
     *
     * @param list 列表
     */
    @Transactional(rollbackFor = RuntimeException.class)
    default void batchInsert(List<T> list) {
        batchInsert(list, INSERT_SIZE);
    }

    /**
     * 批量插入
     *
     * @param list 列表
     * @param size 每批的数量
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @SuppressWarnings("unchecked")
    default void batchInsert(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        //暂时不使用SqlSession,生成主键异常问题(执行器，但是批量执行器事物提交前不会返回主键id)没解决~后续再弄
        IBatchMapper<T> mapper = SpringBeanFactoryUtils.getApplicationContext().getBean(IBatchMapper.class);
        // 每批最后一个的下标
        int batchLastIndex = size;
        for (int i = 0; i < list.size(); ) {
            if (batchLastIndex >= list.size()) {
                batchLastIndex = list.size();
                List<T> insertList = list.subList(i, batchLastIndex);
                int count = mapper.insertBatch(insertList);
                if (count != insertList.size()) {
                    throw new RuntimeException();
                }
                break;
            } else {
                List<T> insertList = list.subList(i, batchLastIndex);
                int count = mapper.insertBatch(insertList);
                i = batchLastIndex;
                batchLastIndex = i + size;
                if (count != insertList.size()) {
                    throw new RuntimeException();
                }
            }
        }
    }


}
