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
    private Long id;

    @NotNull(message = "分类id不能为空")
    private Long categoryId;

    @NotBlank
    @Size(max=200,message = "标题最长200个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
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
