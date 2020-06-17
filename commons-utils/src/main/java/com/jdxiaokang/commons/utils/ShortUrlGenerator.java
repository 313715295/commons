package com.jdxiaokang.commons.utils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons
 * @description:
 * @date 2020/6/15
 */
public class ShortUrlGenerator {


    /**
     * 短连接包含字幕
     */
    private static final String[] CHARS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private static final String KEY = "jingxiaokang";


    /**
     * url短连接方法,采取32位MD5的16进制分4段，每段独立转换为52进制后再拼接
     *
     * @param url 需要转换的url
     * @return 短码
     */
    public static String shortUrlMethod(String url) {
        // 对传入网址进行 MD5 加密
        String hex = MD5Utils.md5(KEY + url);
        StringBuilder stringBuilder = new StringBuilder();
        //得到 2组短链接字符串
        for (int i = 0; i < 4; i++) {
            //把加密字符按照8 位一组 16 进制
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            //转换为52进制
            stringBuilder.append(hexToFiftyTwo(sTempSubString));
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制转52进制字符串
     *
     * @param hex 16进制字符串  15位及以下长度
     * @return 字符串
     */
    private static String hexToFiftyTwo(String hex) {
        StringBuilder builder = new StringBuilder();
        //传入的位16位16进制 Long的最大值为0x7fffffffffffffffL,可能会超过，截断一位
        Deque<String> deque = hexToFiftyTwo(Long.parseLong(hex, 16));
        int length = deque.size();
        for (int i = 0; i < length; i++) {
            String first = deque.pollFirst();
            builder.append(first);
        }
        return builder.toString();
    }

    /**
     * 16进制转52进制  没有数字，a=0  Z=51
     *
     * @param hex 16进制字符串
     */
    private static Deque<String> hexToFiftyTwo(long hex) {
        Deque<String> deque = new ArrayDeque<>();
        //商
        long quotient;
        do {
            quotient = hex / 52;
            //余数
            long r = hex - quotient * 52;
            hex = quotient;
            deque.addFirst(CHARS[(int) r]);
        } while (quotient != 0);
        return deque;
    }

}
