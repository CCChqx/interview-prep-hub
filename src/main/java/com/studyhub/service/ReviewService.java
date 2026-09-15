package com.studyhub.service;

import com.studyhub.pojo.entity.KnowledgePoint;
import com.studyhub.pojo.entity.ReviewRecord;

import java.time.LocalDate;
import java.util.List;

public interface ReviewService {

    ReviewRecord score(Long knowledgeId,int quality);

    List<KnowledgePoint> dueList(LocalDate date);

    long countDue(LocalDate date);
}
