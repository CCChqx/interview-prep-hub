package com.studyhub.service;

import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;

import java.time.LocalDate;
import java.util.List;

public interface ReviewService {

    ReviewRecord score(Long knowledgeId,int quality);

    List<KnowledgePoint> dueList(LocalDate date);

    long countDue(LocalDate date);
}
