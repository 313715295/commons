package com.jdxiaokang.commons.utils;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:
 * @date 2020/1/19
 */
public class FileUtils {

    /**
     * filename 下载文件名 agent 客户端浏览器
     */
    public static String encodeDownloadFilename(String filename, String agent) throws IOException {
        if (agent.contains("Firefox")) { // 火狐浏览器
            filename = "=?UTF-8?B?" + new BASE64Encoder().encode(filename.getBytes(StandardCharsets.UTF_8)) + "?=";
            filename = filename.replaceAll("\r\n", "");
        } else { // IE及其他浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        return filename;
    }
}
