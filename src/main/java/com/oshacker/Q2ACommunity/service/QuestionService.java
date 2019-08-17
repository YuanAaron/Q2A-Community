package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.QuestionDAO;
import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Question> getLatestQuestion(int userId,int offset,int limit) {
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }

    public int addQuestion(Question question) {
        //HTML标签过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        //敏感词过滤
        question.setTitle(sensitiveFilter.filter(question.getTitle()));
        question.setContent(sensitiveFilter.filter(question.getContent()));

        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public Question selectById(int id) {
        return questionDAO.selectById(id);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
