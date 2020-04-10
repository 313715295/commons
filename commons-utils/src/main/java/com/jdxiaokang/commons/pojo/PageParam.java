package com.jdxiaokang.commons.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 排序字段
     */
    public String sortColumn;

    public String sortRule;

    public String orderByClause;
}