package com.jdxiaokang.commons.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * @param <T>
 */
@Data
public class PageDTO<T> implements Serializable {
    /**
     *  记录总（分页时有用）
     */
    private long totalRecordSize;

    private List<T> records;

    public PageDTO(List<T> records,long totalRecordSize){
        this.records = records;
        this.totalRecordSize = totalRecordSize;
    }

    public static <T> PageDTO of(List<T> object,long totalRecordSize){
        return new PageDTO(object,totalRecordSize);
    }
}