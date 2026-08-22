package com.studyhub.controller;

import com.studyhub.common.Result;
import com.studyhub.entity.Category;
import com.studyhub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public Result <List<Category>> getList() {

        List<Category> list = categoryService.getList();
        return Result.success(list);

    }

    @PostMapping()
    public Result<Category> addCategory(@Validated @RequestBody Category category) {
        categoryService.addCategory(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<Void> putCategory(@PathVariable Long id, @Validated @RequestBody Category category) {
        category.setId(id);
        categoryService.putCategory(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();

    }
}
