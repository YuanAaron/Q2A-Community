package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.model.Feed;
import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.service.FeedService;
import com.oshacker.Q2ACommunity.service.FollowService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import com.oshacker.Q2ACommunity.utils.JedisAdapter;
import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;

    //拉的模式：直接从数据库中将feed读取出来即可
    @RequestMapping(path="/pullfeeds",method = {RequestMethod.GET})
    public String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees=new ArrayList<>();
        if (localUserId!=0) { //已登录用户
            //localUserId关注的所有用户的id
            followees=followService.getFollowees(localUserId, ConstantUtil.ENTITY_USER,Integer.MAX_VALUE);

        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    //推的模式
    @RequestMapping(path="/pushfeeds",method = {RequestMethod.GET})
    public String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getBisTimeline(localUserId), 0, 10);
        List<Feed> feeds=new ArrayList<>();
        for (String feedId:feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if (feed!=null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
