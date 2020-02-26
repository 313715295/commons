package com.jdxiaokang.core.canal.client;

/**
 * @program: itemcenter
 * @package: com.duozheng.itemcenter.domain.canal
 * @description: Canal客户端
 * @author: MuYu
 * @create: 2019-03-05 10:00
 * @copyright: Copyright (c) 2019, muyu@duozhengdian.com All Rights Reserved.
 **/
public interface CanalClient {
    /**
     * 开启 canal 客户端，并根据配置连接到 canal ,然后进行针对性的监听
     */
    void start();


    /**
     * 关闭 canal 客户端
     */
    void stop();

    /**
     * 判断 canal 客户端是否是开启状态
     */
    boolean isRunning();
}
