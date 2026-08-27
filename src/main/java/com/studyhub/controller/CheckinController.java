package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    // 打卡：成功返回true；今天已打卡返回400
    @PostMapping
    public Result<Boolean> checkin(){
        boolean first = checkinService.checkin();
        return first ? Result.success(true) : Result.error(400,"今天已打卡");
    }

    @GetMapping("/streak")
    public Result<Integer> streak(){
        return Result.success(checkinService.streak());
    }

    // 累加总时长 接收前端传递的时长
    @PostMapping("/duration")
    public Result<Void> addDuration(@RequestParam int minutes){
        checkinService.addDuration(minutes);
        return Result.success();
    }

    @GetMapping("/duration")
    public Result<Long> getTodayDuration(){
        return Result.success(checkinService.getTodayDuration());
    }

    @PostMapping("/qusetion")
    public Result<Void> addQusetion(@RequestParam int questionId){
        checkinService.addQuestions(questionId);
        return Result.success();
    }

    @GetMapping("/question")
    public Result<Long> getTodayQusetion(){
        return Result.success(checkinService.getTodayQuestions());
    }

}
