package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.FeedDAO;
import com.oshacker.Q2ACommunity.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    private FeedDAO feedDAO;

    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId()>0;
    }

    //拉模式
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId,userIds,count);
    }

    //推模式
    public Feed getFeedById(int id) {
        return feedDAO.getFeedById(id);
    }

}
