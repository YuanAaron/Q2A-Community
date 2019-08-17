package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.model.Comment;
import com.oshacker.Q2ACommunity.dao.CommentDAO;
import com.oshacker.Q2ACommunity.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//HTML过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//敏感词过滤
        return commentDAO.addComment(comment)>0?comment.getId():0;
    }

    public List<Comment> getCommentByEntity(int entityId, int entityType) {
        return commentDAO.selectCommentByEntity(entityId,entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public boolean deleteComment(int id) {
        return commentDAO.updatestatus(id,1)>0;
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

}
