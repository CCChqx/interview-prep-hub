package com.studyhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PracticeMapper extends BaseMapper<ReviewRecord> {

    // 查找没学过的 = 知识点表里有，但复习记录表没有
    @Select("SELECT k.id,k.title from knowledge_point k " +
            "left join review_record r on k.id = r.knowledge_id" +
            " Where r.id is null order by k.id limit 1")
    KnowledgePoint selectUnlearned();
}
