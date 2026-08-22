package com.studyhub.service;

import com.studyhub.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getList();

    void addCategory(Category category);

    void putCategory(Category category);

    void deleteCategory(Long id);
}
