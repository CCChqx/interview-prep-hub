package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.entity.Category;
import com.studyhub.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="分类管理",description = "知识分类的增删改查")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "查询所有分类")
    @GetMapping()
    public Result <List<Category>> getList() {

        List<Category> list = categoryService.getList();
        return Result.success(list);

    }

    @Operation(summary = "新增分类",description = "名称必填,长度≤50")
    @PostMapping()
    public Result<Category> addCategory(@Validated @RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success(category);
    }

    @Operation(summary = "修改分类",description = "无该分类时返回404")
    @PutMapping("/{id}")
    public Result<Void> putCategory(@PathVariable Long id, @Validated @RequestBody Category category) {
        category.setId(id);
        categoryService.putCategory(category);
        return Result.success();
    }

    @Operation(summary = "删除分类",description = "分类下有知识点时返回400")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();

    }
}
