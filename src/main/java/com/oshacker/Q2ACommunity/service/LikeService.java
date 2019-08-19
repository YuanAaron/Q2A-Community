package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private JedisAdapter jedisAdapter;

    //点赞
    public Long like(int userId, int entityType,int entityId) {
        //把userId添加到likeKey集合
        String likeKey= RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        //把userId从dislikeKey集合中删除
        String dislikeKey= RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.srem(dislikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    //点踩
    public Long dislike(int userId, int entityType,int entityId) {
        //把userId添加到dislikeKey集合
        String dislikeKey= RedisKeyUtil.getDislikeKey(entityType,entityId);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));

        //把userId从likeKey集合中删除
        String likeKey= RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    //点完赞或踩后高亮显示
    public int getLikeStatus(int userId, int entityType,int entityId) {
        String likeKey= RedisKeyUtil.getLikeKey(entityType,entityId);
        String dislikeKey= RedisKeyUtil.getDislikeKey(entityType,entityId);

        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {//点赞
            return 1;
        } else if (jedisAdapter.sismember(dislikeKey, String.valueOf(userId))){//点踩
            return -1;
        } else {//没有点击
            return 0;
        }
    }

    //当前有多少人喜欢
    public Long getlikeCount(int entityType,int entityId) {
        String likeKey= RedisKeyUtil.getLikeKey(entityType,entityId);
        return jedisAdapter.scard(likeKey);
    }
}
