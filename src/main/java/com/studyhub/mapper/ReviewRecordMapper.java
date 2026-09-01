package com.studyhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyhub.entity.KnowledgePoint;
import com.studyhub.entity.ReviewRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReviewRecordMapper extends BaseMapper<ReviewRecord> {

    @Select("SELECT k.id,k.title,k.content FROM knowledge_point AS k " +
            "JOIN review_record AS r ON k.id = r.knowledge_id" +
            " WHERE r.next_review_date <= #{date} AND r.mastered = 0")
    List<KnowledgePoint>selectDuePoint(@Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM review_record WHERE next_review_date <= #{data} AND mastered = 0")
    long countDue(@Param("data") LocalDate date);
}
