package com.oshacker.Q2ACommunity.dao;

import com.oshacker.Q2ACommunity.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionDAO {

    Integer addQuestion(Question question);

    //当有多个参数时，@Param不能少
    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}
