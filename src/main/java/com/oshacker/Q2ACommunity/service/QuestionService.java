package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.QuestionDAO;
import com.oshacker.Q2ACommunity.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

    public List<Question> getLatestQuestion(int userId,int offset,int limit) {
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }
}
