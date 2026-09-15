package com.studyhub.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgePointDetailVO {
    private Long id;
    private Long categoryId;
    private String title;
    private String tags;
    private Integer importance;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String content;
}
