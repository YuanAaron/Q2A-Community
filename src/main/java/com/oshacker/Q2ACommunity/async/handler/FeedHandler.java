package com.oshacker.Q2ACommunity.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.oshacker.Q2ACommunity.async.EventHandler;
import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.Feed;
import com.oshacker.Q2ACommunity.model.Question;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.service.FeedService;
import com.oshacker.Q2ACommunity.service.FollowService;
import com.oshacker.Q2ACommunity.service.QuestionService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import com.oshacker.Q2ACommunity.utils.JedisAdapter;
import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//当有人评论或关注了某个问题时，就发一条评论或关注的新鲜事出来
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel model) {
        Map<String,String> map=new HashMap<>();
        // 触发用户
        User actor = userService.getUserById(model.getActorId());
        if (actor==null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW  &&
                        model.getEntityType() == ConstantUtil.ENTITY_QUESTION)) {
            Question question = questionService.selectById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;

    }

    @Override
    public void dohandle(EventModel eventModel) {
        //构造一个新鲜事
        Feed feed=new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(eventModel.getActorId());
        feed.setType(eventModel.getType().getValue());
        feed.setData(buildFeedData(eventModel));
        if (feed.getData()==null) {
            //不支持的feed(buidFeedDate返回null)
            return;
        }
        feedService.addFeed(feed);

        //推增加的部分

        //获取所有粉丝
        List<Integer> followers = followService.getFollowers(ConstantUtil.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        //没登录时只能看系统队列
        followers.add(0);
        //给所有粉丝推事件（当发生事件时，就将该事件推进每个粉丝的timeline异步队列）
        for (int follower: followers) {
            String timelineKey= RedisKeyUtil.getBisTimeline(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        //只注册点赞类型的事件
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
    }
}