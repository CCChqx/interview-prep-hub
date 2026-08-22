package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledgepoint")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;

    @GetMapping("/{id}")
    public Result<KnowledgePoint> getKnowledgePoint(@PathVariable Long id) {
        return Result.success(knowledgePointService.getDetail(id));
    }

    @PostMapping()
    public Result<KnowledgePoint> createKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        knowledgePointService.add(knowledgePoint);
        return Result.success(knowledgePoint);
    }

    @PutMapping("/{id}")
    public Result<KnowledgePoint> updateKnowledgePoint(@PathVariable Long id, @RequestBody KnowledgePoint knowledgePoint) {
        knowledgePoint.setId(id);
        return Result.success(knowledgePointService.update(knowledgePoint));
    }

    @DeleteMapping("/{id}")
    public Result<KnowledgePoint> deleteKnowledgePoint(@PathVariable Long id) {
        knowledgePointService.delete(id);
        return Result.success();
    }

}
