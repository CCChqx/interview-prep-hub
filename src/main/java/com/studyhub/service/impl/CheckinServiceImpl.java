package com.studyhub.service.impl;

import com.studyhub.entity.StudyRecord;
import com.studyhub.mapper.StudyRecordMapper;
import com.studyhub.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // 获取总时长
    @Override
    public long getTotalDuration() {
        return 0;
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

    // 获取总题数
    @Override
    public long getTotalQuestions() {
        return 0;
    }

    // 数据归集
    @Scheduled(cron = "0 5 0 * * ?")
    public void archiveYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String minutes = stringRedisTemplate.opsForValue().get(durationKey(yesterday));
        String questions = stringRedisTemplate.opsForValue().get(questionKey(yesterday));
        if(questions == null && minutes == null) return; // 昨天没数据 跳过

        StudyRecord sr = new StudyRecord();
        sr.setRecordDate(yesterday);
        sr.setDuration(minutes == null ? 0 : Integer.parseInt(minutes));
        sr.setQuestionCount(questions == null ? 0 : Integer.parseInt(questions));
        sr.setCreateTime(LocalDateTime.now());
        sr.setUpdateTime(LocalDateTime.now());
        studyRecordMapper.insert(sr);
    }
}

