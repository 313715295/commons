package com.jdxiaokang.commons.core.pool;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons-parent
 * @description: 连接池统一创建方法
 * @date 2020/2/25
 */
public class HttpClientFactory {

    public static OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().dispatcher(new Dispatcher(ThreadPoolFactory.createServiceExecutor("OkHttp Dispatcher -exec-"))
        ).connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS).build();
    }
}
