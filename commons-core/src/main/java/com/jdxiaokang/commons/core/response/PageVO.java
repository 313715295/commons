package com.jdxiaokang.commons.core.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@ApiModel("分页列表信息")
public class PageVO<T> {

    @ApiModelProperty(value = "总页数",example = "2")
    private Long pages;

    @ApiModelProperty(value = "总条数",example = "30")
    private Long totalRecordSize;

    @ApiModelProperty(value = "当前页数",example = "1")
    private Long currentPage;

    @ApiModelProperty(value = "记录数据")
    private List<T> records;

    @ApiModelProperty(value = "当前页",example = "1")
    private Long pageNum;
    @ApiModelProperty(value = "是否还有下一页")
    private Boolean hasNextPage;


    public PageVO() {
    }

    public PageVO(IPage<?> page) {
        Optional.ofNullable(page).ifPresent(pageData->{
            this.pages = pageData.getPages();
            this.totalRecordSize = pageData.getTotal();
            this.currentPage = pageData.getCurrent();
            this.pageNum = pageData.getCurrent();
            this.hasNextPage = this.currentPage < this.pages;
        });
    }


}
