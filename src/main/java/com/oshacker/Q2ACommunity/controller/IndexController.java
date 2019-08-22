package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.model.ViewObject;
import com.oshacker.Q2ACommunity.service.CommentService;
import com.oshacker.Q2ACommunity.service.FollowService;
import com.oshacker.Q2ACommunity.service.QuestionService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

//首页

@Controller
public class IndexController {

    private static final Logger LOGGER= LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path={"/user/{userId}"},method={RequestMethod.GET})
    public String home(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos",getQuestions(userId,0,10));

        User user = userService.getUserById(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(ConstantUtil.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, ConstantUtil.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), ConstantUtil.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    @RequestMapping(path={"/","/index"},method={RequestMethod.GET})
    public String index(Model model) {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    private List<ViewObject> getQuestions(int userId,int offset,int limit) {
        List<Question> questionList = questionService.getLatestQuestion(userId, offset, limit);
        List<ViewObject> vos=new ArrayList<>();
        for (Question question: questionList) {
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("followCount", followService.getFollowerCount(ConstantUtil.ENTITY_QUESTION, question.getId()));
            vo.set("user",userService.getUserById(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }


}
