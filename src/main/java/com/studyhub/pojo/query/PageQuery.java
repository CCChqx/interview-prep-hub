package com.studyhub.pojo.query;

import lombok.Data;

/*
* 通用分页参数，所有分页查询复用
* */
@Data
public class PageQuery {
    private Integer page = 1;
    private Integer size = 10;
    private String sortField;  // 排序字段
    private String sortOrder; // acs/desc
}
