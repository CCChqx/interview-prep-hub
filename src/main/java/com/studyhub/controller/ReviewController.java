package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;
import com.studyhub.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name ="SM2算法复习")
@Validated
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "计算下次该知识点复习日期")
    @PostMapping("/score")
    public Result<ReviewRecord> score(@RequestParam Long knowledgeId,
                                      @RequestParam @Min(0) @Max(5) Integer quality){

        return Result.success(reviewService.score(knowledgeId, quality));
    }

    @Operation(summary = "展示所需复习知识点信息")
    @GetMapping("/dueList")
    public Result<List<KnowledgePoint>> dueList(@RequestParam LocalDate date){
        List<KnowledgePoint> k = reviewService.dueList(date);
        return Result.success(k);
    }

    @Operation(summary = "展示所需复习知识点数量")
    @GetMapping("due-count")
    public Result<Long> knowledgeCount(@RequestParam LocalDate date){
        return Result.success(reviewService.countDue(date));
    }
}
