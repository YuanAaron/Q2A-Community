package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventProducer;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.Comment;
import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.service.CommentService;
import com.oshacker.Q2ACommunity.service.LikeService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import com.oshacker.Q2ACommunity.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path={"/like"},method={RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);//与前端的约定
        }

        //把点赞事件发送到队列
        Comment comment=commentService.getCommentById(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
                .setEntityType(ConstantUtil.ENTITY_COMMENT).setEntityId(commentId)
                .setEntityOwnerId(comment.getUserId())
                .setExt("questionId", String.valueOf(comment.getEntityId())));

        Long likeCount=likeService.like(hostHolder.getUser().getId(), ConstantUtil.ENTITY_COMMENT,commentId);
        return JSONUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path={"/dislike"},method={RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);
        }

        Long disLikeCount=likeService.dislike(hostHolder.getUser().getId(), ConstantUtil.ENTITY_COMMENT,commentId);
        return JSONUtil.getJSONString(0,String.valueOf(disLikeCount));
    }
}
