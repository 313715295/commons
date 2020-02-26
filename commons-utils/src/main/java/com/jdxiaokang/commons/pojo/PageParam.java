package com.jdxiaokang.commons.pojo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum;

    private Integer pageNo;

    private Integer pageSize;

    public void setPageNum(Integer pageNum){
        this.pageNo = pageNum;
    }

    /**
     * 排序字段
     */
    public String sortColumn;

    public String sortRule;

    public String orderByClause;
}