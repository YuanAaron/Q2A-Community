package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventProducer;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.*;
import com.oshacker.Q2ACommunity.service.*;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import com.oshacker.Q2ACommunity.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger= LoggerFactory.getLogger("QuestionController.calss");

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping("/question/{qid}")
    public String questionDetail(Model model,@PathVariable("qid") int qid) {
        Question question = questionService.selectById(qid);
        model.addAttribute("question",question);

        List<Comment> commentList= commentService.getCommentByEntity(qid, ConstantUtil.ENTITY_QUESTION);
        List<ViewObject> comments=new ArrayList<>();
        for (Comment comment:commentList) {
            ViewObject vo=new ViewObject();
            vo.set("comment",comment);
            vo.set("user",userService.getUserById(comment.getUserId()));

            //是否喜欢
            if (hostHolder.getUser()==null) {
                vo.set("liked",0);//相当于没有点击
            } else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),ConstantUtil.ENTITY_COMMENT,comment.getId()));
            }

            //喜欢的数量
            vo.set("likeCount",likeService.getlikeCount(ConstantUtil.ENTITY_COMMENT,comment.getId()));

            comments.add(vo);
        }

        model.addAttribute("comments",comments);

        List<ViewObject> followUsers=new ArrayList<>();
        List<Integer> users = followService.getFollowers(ConstantUtil.ENTITY_QUESTION, qid, 0, 10);
        for (int uid :users) {
            User user = userService.getUserById(uid);
            if (user == null)
                continue;

            ViewObject vo = new ViewObject();
            vo.set("id", user.getId());
            vo.set("name", user.getName());
            vo.set("headUrl", user.getHeadUrl());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers",followUsers);

        if (hostHolder.getUser()!=null) {
            model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),ConstantUtil.ENTITY_QUESTION,qid));
        } else {
            model.addAttribute("followed",false);
        }

        return "detail";
    }

    @RequestMapping(value = "/question/add",method ={RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question=new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if (hostHolder.getUser()==null) {//未登录
                //question.setUserId(ConstantUtil.ANONYMOUS_USERID);

                //从popupAdd.js中可知，999表示未登录，跳转到登录页面
                return JSONUtil.getJSONString(999);
            }else {
                question.setUserId(hostHolder.getUser().getId());
            }

            if (questionService.addQuestion(question)>0) {
                eventProducer.fireEvent(new EventModel(EventType.ADDQUESTION)
                        .setActorId(question.getUserId()).setEntityId(question.getId())
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));
                return JSONUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败"+e.getMessage());
        }

        return JSONUtil.getJSONString(1,"失败");
    }
}
