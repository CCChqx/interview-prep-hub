package com.studyhub.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private Long id;

    @Size(max=50,message = "分类名最长50个字符")
    @NotBlank(message = "分类名不能为空")
    private String name;

    private Integer sort;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
