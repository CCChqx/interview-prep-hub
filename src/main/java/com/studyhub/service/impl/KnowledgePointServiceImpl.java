package com.studyhub.service.impl;

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

}
