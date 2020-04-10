package com.jdxiaokang.commons.core.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jdxiaokang.commons.pojo.PageParam;

import java.util.Optional;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons
 * @description: 分页关联工具
 * @date 2020/4/10
 */
public class PageUtils {

    /**
     * 分页默认页大小：10
     */
    private static final int DEFAULT_PAGE_SIZE = 15;

    /**
     * 默认当前页
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * 最大页码
     */
    private static final int MAX_PAGE_SIZE = 100;

    public static <T> IPage<T> getPage(PageParam pageParam) {
        return Optional.ofNullable(pageParam).map(param -> new Page<T>()
                .setCurrent(Optional.ofNullable(param.getPageNum()).orElse(DEFAULT_PAGE))
                .setPages(Optional.ofNullable(param.getPageSize()).orElse(DEFAULT_PAGE_SIZE)))
                .orElseGet(PageUtils::getDefaultPage);
    }

    public static <T> IPage<T> getDefaultPage() {
        return new Page<>(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }
}
