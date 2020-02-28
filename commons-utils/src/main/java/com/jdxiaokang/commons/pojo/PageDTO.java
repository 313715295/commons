package com.jdxiaokang.commons.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * @param <T>
 */
@Data
@Accessors(chain = true)
public class PageDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Long pageNum;
    /**
     * 显示条数
     */
    private Long pageSize;
    /**
     * 总页数
     */
    private Long pages;

    /**
     * 总条数
     */
    private Long totalRecordSize;

    /**
     * 数据
     */
    private List<T> records;



}