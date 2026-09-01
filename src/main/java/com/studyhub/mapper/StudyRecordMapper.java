package com.studyhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyhub.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {

    @Select("SELECT SUM(duration) FROM study_record")
    Long sumDuration();

    @Select("SELECT SUM(study_record.question_count) FROM study_record")
    Long sumQuestionCount();
}
