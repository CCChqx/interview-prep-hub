package com.studyhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyhub.common.Result;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.service.KnowledgePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "知识点管理",description = "知识点的增删改查")
@RestController
@RequestMapping("/api/knowledgepoint")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;

    @Operation(summary = "查询该分类下的知识点")
    @GetMapping("/{id}")
    public Result<KnowledgePoint> getKnowledgePoint(@PathVariable Long id) {
        return Result.success(knowledgePointService.getDetail(id));
    }

    @Operation(summary = "新增知识点",description = "分类不存在时返回400")
    @PostMapping()
    public Result<KnowledgePoint> createKnowledgePoint(@Validated({KnowledgePoint.Add.class, Default.class})
                                                           @RequestBody KnowledgePoint knowledgePoint) {
        knowledgePointService.add(knowledgePoint);
        return Result.success(knowledgePoint);
    }

    @Operation(summary = "修改知识点",description = "知识点不存在时返回404")
    @PutMapping("/{id}")
    public Result<KnowledgePoint> updateKnowledgePoint(@PathVariable Long id,
                                                       @Validated(Default.class)@RequestBody KnowledgePoint knowledgePoint) {
        knowledgePoint.setId(id);
        return Result.success(knowledgePointService.update(knowledgePoint));
    }

    @Operation(summary = "删除知识点",description = "知识点不存在时返回404")
    @DeleteMapping("/{id}")
    public Result<KnowledgePoint> deleteKnowledgePoint(@PathVariable Long id) {
        knowledgePointService.delete(id);
        return Result.success();
    }

    @Operation(summary = "分页查询知识点")
    @GetMapping()
    public Result<Page<KnowledgePoint>> list(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10")  int size,
                                             @RequestParam(required = false) Long categoryId,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer importance,
                                             @RequestParam(required = false) Integer status) {

        return Result.success(knowledgePointService.getPage(page, size, categoryId, keyword, importance, status));

    }
}
