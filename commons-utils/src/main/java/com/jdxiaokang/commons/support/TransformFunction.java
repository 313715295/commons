package com.jdxiaokang.commons.support;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:
 * @date 2019/12/29
 */
@FunctionalInterface
public interface TransformFunction<S,T> {
    /**
     * 传入一个实体s 和目标实体t，然后根据s设置相关熟悉
     *
     * @param s           实体s
     * @param t           目标实体
     */
    T accept(S s, T t);
}
