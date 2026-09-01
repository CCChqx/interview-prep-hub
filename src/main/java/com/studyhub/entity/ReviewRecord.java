package com.studyhub.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRecord {

    private Long id;
    private Long knowledgeId;
    private Double ef;
    private Integer intervalDays;
    private LocalDate nextReviewDate;
    private Integer reviewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer mastered;
}
