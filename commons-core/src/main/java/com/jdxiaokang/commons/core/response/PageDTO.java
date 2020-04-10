package com.jdxiaokang.commons.core.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
     * 当前页
     */
    private Long currentPage;
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

    /**
     * 是否还有下页
     */
    private Boolean hasNextPage;


    public PageDTO() {
    }

    public PageDTO(IPage<?> page) {
        Optional.ofNullable(page).ifPresent(pageData->{
            this.pages = pageData.getPages();
            this.totalRecordSize = pageData.getTotal();
            this.currentPage = pageData.getCurrent();
            this.pageNum = pageData.getCurrent();
            this.hasNextPage = this.currentPage < this.pages;
        });
    }


}