package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventProducer;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.Comment;
import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.service.CommentService;
import com.oshacker.Q2ACommunity.service.QuestionService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger= LoggerFactory.getLogger("CommentController.calss");

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path={"/addComment"},method={RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment=new Comment();
            comment.setContent(content);
            if (hostHolder.getUser()!=null) {//登录
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(ConstantUtil.ANONYMOUS_USERID);//匿名用户
                //return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(ConstantUtil.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            comment.setStatus(0);

            //添加评论和更新question表中的评论数应该添加事务(应用于操作数据库的两个表，
            //对表的操作存在失败的可能 或 执行两条sql语句，语句存在执行失败的可能)
            //老师的处理方式是把评论数的更新做成异步的，因为评论数更新的晚一点也没有什么关系。
            commentService.addComment(comment);

            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);

            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));
        } catch (Exception e) {
            logger.error("增加评论失败"+e.getMessage());
        }

        return "redirect:/question/"+questionId;

    }
}
