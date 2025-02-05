package com.acautomaton.forum.vo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageHelperVO<T> {
    Boolean isFirstPage;
    Boolean isLastPage;
    Integer pageNumber;
    Integer pageSize;
    Integer previousPage;
    Integer nextPage;
    Integer pages;
    List<?> records;

    public PageHelperVO(PageInfo<T> pageInfo) {
        this.isFirstPage = pageInfo.isIsFirstPage();
        this.isLastPage = pageInfo.isIsLastPage();
        this.pageNumber = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.previousPage = pageInfo.getPrePage();
        this.nextPage = pageInfo.getNextPage();
        this.pages = pageInfo.getPages();
        this.records = pageInfo.getList();
    }
}
