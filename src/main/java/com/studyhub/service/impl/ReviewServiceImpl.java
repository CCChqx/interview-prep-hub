package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.KnowledgePointMapper;
import com.studyhub.mapper.ReviewRecordMapper;
import com.studyhub.service.ReviewService;
import com.studyhub.util.SM2Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;


    @Override
    public ReviewRecord score(Long knowledgeId,int quality) {
        // 按 knowledgeId查询知识点
        LambdaQueryWrapper<ReviewRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewRecord::getKnowledgeId, knowledgeId);
        ReviewRecord r = reviewRecordMapper.selectOne(queryWrapper);

        // 如果没有说明该知识点还未复习
        if (r == null) {
            throw new BusinessException(404,"该知识点还没有复习计划");
        }

        double newEf = SM2Calculator.updateEF(r.getEf(), quality);
        int nextInterval = SM2Calculator.nextInterval(r.getIntervalDays(), newEf, quality, r.getReviewCount());
        LocalDate nextReviewDate = SM2Calculator.nextReviewDate(LocalDate.now(), nextInterval);

        if (nextInterval >= 180){
            r.setMastered(1);
        }

        r.setEf(newEf);
        r.setIntervalDays(nextInterval);
        r.setNextReviewDate(nextReviewDate);
        r.setReviewCount(r.getReviewCount() + 1);
        r.setUpdateTime(LocalDateTime.now());

        reviewRecordMapper.updateById(r);
        return r;
    }

    @Override
    public List<KnowledgePoint> dueList(LocalDate date) {
        return reviewRecordMapper.selectDuePoint(date);
    }

    @Override
    public long countDue(LocalDate date) {
        return reviewRecordMapper.countDue(date);
    }
}
