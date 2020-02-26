package com.jdxiaokang.orm.dao.utils;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: SQL相关工具
 * @date 2019/12/31
 */
public class MybatisPlusUtils {
    /**
     * 获取冻结通用Wrapper
     *
     * @param <T> 实体类型
     */
    public static <T> UpdateWrapper<T> getFreezeWrapper(boolean freeze) {
        return new UpdateWrapper<T>().set("frozen", freeze);
    }

    /**
     * 转换为数组，省的到warnings
     *
     * @param columns 查询字段
     * @param <T>     类型
     */
    @SafeVarargs
    public static <T> SFunction<T, ?>[] convert(SFunction<T, ?>... columns) {
        return columns;
    }

}
