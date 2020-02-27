package com.jdxiaokang.commons.dao.plugin.page.entity;

import java.io.Serializable;
import java.util.List;

/***
 *
 *
 * @类名称：Page
 * @类描述：Mybatis - 分页实体
 * @创建人：robin @修改人：
 * @修改时间：2014年9月4日 下午11:51:49 @修改备注：
 * @version 1.0.0
 */
public class Page<T> implements Serializable {
    /**
     * 取得查询总记录数
     */
    private int totalCount;

    private int toalPage;

    private int pageSize = 15;

    private int startIndex;


    private List<T> object;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getToalPage() {
        return toalPage;
    }

    public void setToalPage(int toalPage) {
        this.toalPage = toalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public List<T> getObject() {
        return object;
    }

    public void setObject(List<T> object) {
        this.object = object;
    }
}