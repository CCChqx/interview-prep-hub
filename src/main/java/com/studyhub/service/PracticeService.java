package com.studyhub.service;

import com.studyhub.entity.KnowledgePoint;

import java.util.Map;

public interface PracticeService {

    KnowledgePoint next(); // 出题
    KnowledgePoint getAnswer(Long knowledgeId); // 看答案
    Map<String,Object> submit(Long knowledgeId, int quality); //自评
}
