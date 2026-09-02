package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name ="打卡及时长题目展示")
@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    // 打卡：成功返回true；今天已打卡返回400
    @Operation(summary = "打卡")
    @PostMapping
    public Result<Boolean> checkin(){
        boolean first = checkinService.checkin();
        return first ? Result.success(true) : Result.error(400,"今天已打卡");
    }

    @Operation(summary = "展示连续打卡天数")
    @GetMapping("/streak")
    public Result<Integer> streak(){
        return Result.success(checkinService.streak());
    }

    // 累加总时长 接收前端传递的时长
    @Operation(summary = "累加传递的时长")
    @PostMapping("/duration")
    public Result<Void> addDuration(@RequestParam int minutes){
        checkinService.addDuration(minutes);
        return Result.success();
    }

    @Operation(summary = "查看今日时长")
    @GetMapping("/duration")
    public Result<Long> getTodayDuration(){
        return Result.success(checkinService.getTodayDuration());
    }

    @Operation(summary = "累加今日题目数")
    @PostMapping("/question")
    public Result<Void> addQuestion(@RequestParam int questionId){
        checkinService.addQuestions(questionId);
        return Result.success();
    }

    @Operation(summary = "查看今日题数")
    @GetMapping("/question")
    public Result<Long> getTodayQusetion(){
        return Result.success(checkinService.getTodayQuestions());
    }


    @Operation(summary = "查看总学习时长")
    @GetMapping("/total-duration")
    public Result<Long> getTotalDuration(){
        return Result.success(checkinService.getTotalDuration());
    }

    @Operation(summary = "查看总学习题目数")
    @GetMapping("/total-questions")
    public Result<Long> getTotalQuestions(){
        return Result.success(checkinService.getTotalQuestions());
    }

}
