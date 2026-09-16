package com.studyhub.pojo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/*
*  知识点自己的筛选(继承PageQuery → 既有分页,又有自己的筛选)
* */
@Data
@EqualsAndHashCode(callSuper = true)
public class KnowledgePointQuery extends PageQuery{

    //  白名单
    public static final Set<String> SORT_FIELDS = Set.of(
            "create_time",
            "update_time",
            "importance"
    );


    private Long categoryId;
    private String keyword;
    private Integer importance;
    private Integer status;


}
