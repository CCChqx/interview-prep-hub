package com.studyhub.service;

public interface CheckinService {

    boolean checkin(); //打卡，返回是否首次打卡

    int streak(); // 返回连续打卡天数

    void addDuration(int minutes); //累加今日时长

    long getTodayDuration(); //查看今日时长

    long getTotalDuration(); //查看历史总时长

    void addQuestions(int count); //累加今日题目数量

    long getTodayQuestions(); //查今日题数

    long getTotalQuestions(); //查累计题数
}
