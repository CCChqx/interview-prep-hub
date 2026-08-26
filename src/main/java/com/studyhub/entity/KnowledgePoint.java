package com.studyhub.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgePoint {

    // 分组接口，只是空接口
    public interface Add{}
    public interface Update{}


    private Long id;

    @NotNull(message = "所属分类不能为空")
    private Long categoryId;

    @NotBlank(groups = Add.class, message = "标题不能为空")
    @Size(max=200,message = "标题最长200个字符")
    private String title;

    @NotBlank(groups = Add.class, message = "内容不能为空")
    private String content;

    @Size(max = 200)
    private String tags;

    @Min(1)
    @Max(5)
    private Integer importance;

    @Min(1)
    @Max(2)
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
