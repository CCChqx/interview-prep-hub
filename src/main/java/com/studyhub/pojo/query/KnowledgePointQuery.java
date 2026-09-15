package com.studyhub.pojo.query;

import lombok.Data;

/*
*  知识点自己的筛选(继承PageQuery → 既有分页,又有自己的筛选)
* */
@Data
public class KnowledgePointQuery extends PageQuery{
    private Long categoryId;
    private String keyword;
    private Integer importance;
    private Integer status;
}
