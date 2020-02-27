package com.jdxiaokang.commons.core.pool;

import okhttp3.OkHttpClient;

import static com.jdxiaokang.commons.core.pool.HttpClientFactory.createOkHttpClient;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:  httpclient
 * @date 2020/2/5
 */
public enum OkHttpClientSingleton {
    /**
     * 默认
     */
    INSTANCE(createOkHttpClient());

    OkHttpClientSingleton(OkHttpClient client) {
        this.client = client;
    }

    private OkHttpClient client;

    public OkHttpClient getClient() {
        return client;
    }
}
