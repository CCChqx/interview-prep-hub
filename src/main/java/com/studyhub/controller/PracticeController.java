package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "刷题练习",description = "主动回忆卡片：出题/看答案/自评")
@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @Operation(summary = "出题")
    @GetMapping("/next")
    public Result<KnowledgePoint> next(){
        return Result.success(practiceService.next());
    }

    @Operation(summary = "看答案")
    @GetMapping("/{id}/answer")
    public Result<KnowledgePoint> answer(@PathVariable Long id){
        return Result.success(practiceService.getAnswer(id));
    }

    @Operation(summary = "自评打分")
    @PostMapping("/anwser")
    public Result<Map<String,Object>> submit(@RequestParam Long knowledgeId,
                                             @RequestParam int quality){
        return Result.success(practiceService.submit(knowledgeId,quality));
    }
}
