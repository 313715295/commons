package com.jdxiaokang.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal
 * @description: Canal客户端监听
 * @author: MuYu
 * @create: 2019-03-05 10:15
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalClientListener {

    /** 监听表名 **/
    String tables() default "";

    /** 监听字段 **/
    String column() default "";
}
