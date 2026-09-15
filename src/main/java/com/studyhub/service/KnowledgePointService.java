package com.studyhub.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studyhub.pojo.entity.KnowledgePoint;
import com.studyhub.pojo.query.KnowledgePointQuery;
import com.studyhub.pojo.vo.KnowledgePointVO;

import java.util.List;

public interface KnowledgePointService extends IService<KnowledgePoint> {

    // 查询
    KnowledgePoint getDetail(Long id);

    // 新增
    boolean add(KnowledgePoint knowledgePoint);

    // 更新
    KnowledgePoint update(KnowledgePoint knowledgePoint);

    // 删除
    boolean delete(Long id);

    // 分页查询
    Page<KnowledgePointVO> getPage(KnowledgePointQuery query);

    // 批量导入
    int batchImport(List<KnowledgePoint> list);
}
