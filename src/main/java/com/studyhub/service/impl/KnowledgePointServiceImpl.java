package com.studyhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.exception.BusinessException;
import com.studyhub.mapper.CategoryMapper;
import com.studyhub.mapper.KnowledgePointMapper;
import com.studyhub.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper,KnowledgePoint>
        implements KnowledgePointService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public KnowledgePoint getDetail(Long id) {
         KnowledgePoint kp = getById(id);
        if(kp == null){
            throw new BusinessException(404,"知识点不存在");
        }
        return kp;
    }

    @Override
    public boolean add(KnowledgePoint knowledgePoint){
        if (categoryMapper.selectById(knowledgePoint.getCategoryId()) == null) {
            throw new BusinessException(400, "分类不存在");
        }
        return this.save(knowledgePoint);
    }

    @Override
    public KnowledgePoint update(KnowledgePoint knowledgePoint){
        if (this.getById(knowledgePoint.getId()) == null) {
            throw new BusinessException(404,"知识点不存在");
        }
        this.updateById(knowledgePoint);
        return this.getById(knowledgePoint.getId());
    }

    @Override
    public boolean delete(Long id){
       if(this.getById(id) == null){
           throw new BusinessException(404,"知识点不存在");
       }
       return this.removeById(id);
    }

    @Override
    public Page<KnowledgePoint> getPage(int page,int size,Long categoryId, String keyword,Integer importance,Integer status){

        size = Math.min(size,100);
        page = Math.min(page,1);

        // 告诉 MP 我要第 page页，每页size条
        Page<KnowledgePoint> p = new Page<>(page,size);

        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<>();

        // ③ 动态条件：传了才筛，没传不加
        if (categoryId != null) {
            wrapper.eq(KnowledgePoint::getCategoryId, categoryId);   // WHERE category_id = ?
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(KnowledgePoint::getTitle, keyword);         // AND title LIKE '%?%'
        }
        if (importance != null) {
            wrapper.eq(KnowledgePoint::getImportance, importance);   // AND importance = ?
        }
        if (status != null) {
            wrapper.eq(KnowledgePoint::getStatus, status);           // AND status = ?
        }

        wrapper.select(KnowledgePoint::getId, KnowledgePoint::getCategoryId,
                KnowledgePoint::getTitle, KnowledgePoint::getTags,
                KnowledgePoint::getImportance, KnowledgePoint::getStatus,
                KnowledgePoint::getCreateTime, KnowledgePoint::getUpdateTime);

        // 按时间倒序排列
        wrapper.orderByDesc(KnowledgePoint::getCreateTime);

        // 执行分页查询 自动 COUNT LIMIT
        return this.page(p,wrapper);

    }

}
