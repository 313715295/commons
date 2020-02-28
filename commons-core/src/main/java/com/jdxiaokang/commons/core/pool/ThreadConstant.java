package com.jdxiaokang.commons.core.pool;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 加载配置文件部分配置
 * @date 2019/12/28
 */
@Data
@Accessors(chain = true)
public class ThreadConstant {


    /**
     * cpu核数
     */
    public static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * web线程池最大线程数 默认200 枚举初始化的会采用这个~配置文件的bean修改还未生效
     */
    public static int WEB_MAX_THREAD = 200;
    /**
     * web线程池最小线程数
     */
    public static final int  WEB_MIN_THREADS = CPU_NUM*5;


}
