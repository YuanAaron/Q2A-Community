package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventProducer;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.model.ViewObject;
import com.oshacker.Q2ACommunity.service.CommentService;
import com.oshacker.Q2ACommunity.service.FollowService;
import com.oshacker.Q2ACommunity.service.QuestionService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import com.oshacker.Q2ACommunity.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path={"/followUser"},method={RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);//与前端的约定：跳转到登录界面
        }

        boolean ret=followService.follow(hostHolder.getUser().getId(), ConstantUtil.ENTITY_USER, userId);

        //发送关注事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(ConstantUtil.ENTITY_USER)
                .setEntityId(userId).setEntityOwnerId(userId));

        //返回关注的人数
        return JSONUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),ConstantUtil.ENTITY_USER)));
    }

    @RequestMapping(path={"/unfollowUser"},method={RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);//与前端的约定：跳转到登录界面
        }

        boolean ret=followService.unfollow(hostHolder.getUser().getId(), ConstantUtil.ENTITY_USER, userId);

        //发送取关事件
//        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
//                .setActorId(hostHolder.getUser().getId())
//                .setEntityType(ConstantUtil.ENTITY_USER)
//                .setEntityId(userId).setEntityOwnerId(userId));

        //返回关注的人数
        return JSONUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),ConstantUtil.ENTITY_USER)));
    }

    @RequestMapping(path={"/followQuestion"},method={RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);//与前端的约定：跳转到登录界面
        }

        Question question=questionService.selectById(questionId);
        if (question==null) {
            return JSONUtil.getJSONString(1,"问题不存在");
        }

        boolean ret=followService.follow(hostHolder.getUser().getId(), ConstantUtil.ENTITY_QUESTION, questionId);

        //发送关注事件
//        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
//                .setActorId(hostHolder.getUser().getId())
//                .setEntityType(ConstantUtil.ENTITY_QUESTION)
//                .setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        //被关注的问题下方有关注人的头像、姓名信息
        Map<String,Object> info=new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());//作为点击头像/名字的超链接
        info.put("count",followService.getFollowerCount(ConstantUtil.ENTITY_QUESTION,questionId));
        return JSONUtil.getJSONString(ret?0:1,info);
    }

    @RequestMapping(path={"/unfollowQuestion"},method={RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser()==null) {
            return JSONUtil.getJSONString(999);//与前端的约定：跳转到登录界面
        }

        Question question=questionService.selectById(questionId);
        if (question==null) {
            return JSONUtil.getJSONString(1,"问题不存在");
        }

        boolean ret=followService.unfollow(hostHolder.getUser().getId(), ConstantUtil.ENTITY_QUESTION, questionId);

        //发送取关事件
//        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
//                .setActorId(hostHolder.getUser().getId())
//                .setEntityType(ConstantUtil.ENTITY_QUESTION)
//                .setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(ConstantUtil.ENTITY_QUESTION, questionId));
        //返回关注的人数
        return JSONUtil.getJSONString(ret?0:1,info);
    }

    //粉丝列表
    @RequestMapping(path={"/user/{uid}/followers"},method={RequestMethod.GET})
    public String followers(@PathVariable("uid") int userId,Model model) {
        List<Integer> followerIds = followService.getFollowers(ConstantUtil.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser()==null) {
            model.addAttribute("followers",getUsersInfo(0,followerIds));//0表示未登录
        } else {
            model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }

        model.addAttribute("curUser",userService.getUserById(userId));
        model.addAttribute("followerCount",followService.getFollowerCount(ConstantUtil.ENTITY_USER,userId));
        return "followers";
    }

    //关注对象列表
    @RequestMapping(path={"/user/{uid}/followees"},method={RequestMethod.GET})
    public String followees(@PathVariable("uid") int userId, Model model) {
        List<Integer> followeeIds = followService.getFollowees(userId, ConstantUtil.ENTITY_USER, 0, 10);
        if (hostHolder.getUser()==null) {
            model.addAttribute("followees",getUsersInfo(0,followeeIds));//0表示未登录
        } else {
            model.addAttribute("followees",getUsersInfo(hostHolder.getUser().getId(),followeeIds));
        }

        model.addAttribute("curUser",userService.getUserById(userId));
        model.addAttribute("followeeCount",followService.getFolloweeCount(userId,ConstantUtil.ENTITY_USER));
        return "followees";
    }

    public List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos=new ArrayList<>();
        for (int uid :userIds) {
            User user = userService.getUserById(uid);
            if (user==null)
                continue;

            ViewObject vo=new ViewObject();
            vo.set("user",user);
            vo.set("commentCount",commentService.getUserCommentCount(uid));
            vo.set("followerCount",followService.getFollowerCount(ConstantUtil.ENTITY_USER,uid));
            vo.set("followeeCount",followService.getFolloweeCount(uid,ConstantUtil.ENTITY_USER));
            //加入localUserId和uid是否是关注关系
            if (localUserId!=0) { //localUserId==0表示未登录
                vo.set("followed",followService.isFollower(localUserId,ConstantUtil.ENTITY_USER,uid));
            } else {
                vo.set("followed",false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
