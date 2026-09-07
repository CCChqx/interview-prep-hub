package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.KnowledgePointMapper;
import com.studyhub.mapper.PracticeMapper;
import com.studyhub.mapper.ReviewRecordMapper;
import com.studyhub.service.PracticeService;
import com.studyhub.util.SM2Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PracticeServiceImpl implements PracticeService {

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private ReviewRecordMapper reviewRecordMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;


    @Override
    public KnowledgePoint next() {
       // 优先出没学过的
        KnowledgePoint unlearned = practiceMapper.selectUnlearned();
        if (unlearned != null) {
            unlearned.setContent(null);
            return unlearned;
        }
        // 都学完了，取到期需要复习的
        List<KnowledgePoint> due = reviewRecordMapper.selectDuePoint(LocalDate.now());
        if (due.size() > 0) {
            KnowledgePoint kp = due.get(0);
            kp.setContent(null);
            return kp;
        }
        return null; // 全部复习完
    }

    @Override
    public KnowledgePoint getAnswer(Long knowledgeId) {
        KnowledgePoint kp = knowledgePointMapper.selectById(knowledgeId);
        if (kp == null) {
            throw new BusinessException(404,"知识点不存在");
        }
        return kp;
    }

    @Override
    @Transactional //复习记录 + 知识点状态 同一事物
    public Map<String, Object> submit(Long knowledgeId, int quality) {
        KnowledgePoint kp = knowledgePointMapper.selectById(knowledgeId);
        if (kp == null) {
            throw new BusinessException(404,"知识点不存在");
        }

        LambdaQueryWrapper<ReviewRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewRecord::getKnowledgeId,knowledgeId);
        ReviewRecord reviewRecord = reviewRecordMapper.selectOne(queryWrapper);

        if (reviewRecord == null) {
            // 没学过 → 建记录 + 状态 未学0 学习中1
            reviewRecord = new ReviewRecord();
            reviewRecord.setKnowledgeId(knowledgeId);
            reviewRecord.setEf(2.5);
            reviewRecord.setIntervalDays(0);
            reviewRecord.setNextReviewDate(LocalDate.now());
            reviewRecord.setMastered(0);
            reviewRecord.setCreateTime(LocalDateTime.now());
            reviewRecord.setUpdateTime(LocalDateTime.now());
            reviewRecordMapper.insert(reviewRecord);
            kp.setStatus(1);
            knowledgePointMapper.updateById(kp);
        }else{
            // 已毕业 →非法流转,拒绝（业务异常）
            if (reviewRecord.getMastered() !=null && reviewRecord.getMastered() == 1){
                throw new BusinessException(400,"该知识点已毕业，无需复习");
            }
            // 复用 SM2算新值
            double newEf = SM2Calculator.updateEF(reviewRecord.getEf(),quality);
            int nextInterval = SM2Calculator.nextInterval(reviewRecord.getIntervalDays(),newEf,quality,reviewRecord.getReviewCount());
            reviewRecord.setEf(newEf);
            reviewRecord.setIntervalDays(nextInterval);
            reviewRecord.setNextReviewDate(SM2Calculator.nextReviewDate(LocalDate.now(),nextInterval));
            reviewRecord.setReviewCount(reviewRecord.getReviewCount()+1);
            reviewRecord.setUpdateTime(LocalDateTime.now());
            if (nextInterval >= 180){
                reviewRecord.setMastered(1); //毕业
                kp.setStatus(2); // 已掌握
                knowledgePointMapper.updateById(kp);
            }
            reviewRecordMapper.updateById(reviewRecord);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("quality",quality);
        result.put("nextReviewDate",reviewRecord.getNextReviewDate());
        result.put("mastered",reviewRecord.getMastered());
        result.put("status",kp.getStatus());
        return result;
    }
}
