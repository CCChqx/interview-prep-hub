package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyhub.entity.Category;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.CategoryMapper;
import com.studyhub.mapper.KnowledgePointMapper;
import com.studyhub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired //注入Mapper
    private CategoryMapper categoryMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    // 查询所有的分类数据
    @Override
    public List<Category> getList() {
        return categoryMapper.selectList(null);
    }

    //新添数据
    @Override
    public void addCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);

    }

    //更新数据
    @Override
    public void putCategory(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        int i = categoryMapper.updateById(category);
        if (i == 0) {
             throw new BusinessException(404,"分类不存在");
        }
    }

    //删除分类
    @Override
    public void deleteCategory(Long id) {
        Long count = knowledgePointMapper.selectCount(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .eq(KnowledgePoint::getCategoryId,id));

        if (count > 0) {
            throw new BusinessException(400,"分类下存在知识点，禁止删除");
        }


        int i = categoryMapper.deleteById(id);
        if(i == 0){
            throw new BusinessException(404,"分类不存在");
        }
    }
}
