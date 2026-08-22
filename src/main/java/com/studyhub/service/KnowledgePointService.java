package com.studyhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.studyhub.entity.KnowledgePoint;

public interface KnowledgePointService extends IService<KnowledgePoint> {

    // 查询
    KnowledgePoint getDetail(Long id);

    // 新增
    boolean add(KnowledgePoint knowledgePoint);

    // 更新
    KnowledgePoint update(KnowledgePoint knowledgePoint);

    // 删除
    boolean delete(Long id);
}
