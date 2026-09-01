package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhub.entity.StudyRecord;
import com.studyhub.mapper.StudyRecordMapper;
import com.studyhub.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckinServiceImpl implements CheckinService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    private static final String KEY = "checkin:1";

    @Override
    public boolean checkin() {
        String today = LocalDate.now().toString();
        Double score = stringRedisTemplate.opsForZSet().score(KEY, today);
        if(score != null){
            return false; //今天已打卡
        }
        stringRedisTemplate.opsForZSet().add(KEY, today, System.currentTimeMillis());
        return true;
    }

    @Override
    public int streak() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 365; i++) {
            String day = today.minusDays(i).toString();
            if(stringRedisTemplate.opsForZSet().score(KEY, day) != null){
                count++;
            }else{
                break;
            }
        }
        return count;
    }



    // 在类里加今日时长的两个方法
    private String durationKey(LocalDate date){
        return "study:1:duration:" + date; //key动态拼接今天日期
    }

    private String questionKey(LocalDate date){
        return "study:1:question:" + date;
    }

    // 累加今日时长
    @Override
    public void addDuration(int minutes) {
        stringRedisTemplate.opsForValue().increment(durationKey(LocalDate.now()), minutes);
    }

    // 获取今日时长
    @Override
    public long getTodayDuration() {
        String v = stringRedisTemplate.opsForValue().get(durationKey(LocalDate.now()));
        return v == null ? 0 : Long.parseLong(v);
    }

    // 累加题目总数
    @Override
    public void addQuestions(int count) {
        Long l = stringRedisTemplate.opsForValue().increment(questionKey(LocalDate.now()), count);
    }

    // 获取今日题数
    @Override
    public long getTodayQuestions() {
        String v = stringRedisTemplate.opsForValue().get(questionKey(LocalDate.now()));
        return v == null ? 0 : Long.parseLong(v);
    }


    // 数据归集（有则更新、无则插入）——直接替换旧版
    @Scheduled(cron = "0 5 0 * * ?")
    public void archiveYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String minutes = stringRedisTemplate.opsForValue().get(durationKey(yesterday));
        String questions = stringRedisTemplate.opsForValue().get(questionKey(yesterday));
        if (questions == null && minutes == null) return; // 昨天没数据 跳过

        int duration = minutes == null ? 0 : Integer.parseInt(minutes);
        int questionCount = questions == null ? 0 : Integer.parseInt(questions);

        // 先查：昨天这一行在不在表里
        LambdaQueryWrapper<StudyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyRecord::getRecordDate, yesterday);
        StudyRecord existing = studyRecordMapper.selectOne(wrapper);

        if (existing != null) {
            // 在 → 更新（改查出来的对象，再存回去）
            existing.setDuration(duration);
            existing.setQuestionCount(questionCount);
            existing.setUpdateTime(LocalDateTime.now());
            studyRecordMapper.updateById(existing);
        } else {
            // 不在 → 插入新的一行
            StudyRecord sr = new StudyRecord();
            sr.setRecordDate(yesterday);
            sr.setDuration(duration);
            sr.setQuestionCount(questionCount);
            sr.setCreateTime(LocalDateTime.now());
            sr.setUpdateTime(LocalDateTime.now());
            studyRecordMapper.insert(sr);
        }
    }

    // 获取总时长
    @Override
    public long getTotalDuration(){
        Long sum = studyRecordMapper.sumDuration();
        return sum == null ? 0 : sum;
    }

    // 获取总题目
    @Override
    public long getTotalQuestions(){
        Long count = studyRecordMapper.sumQuestionCount();
        return count == null ? 0 : count;
    }

}

