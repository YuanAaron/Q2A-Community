package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.utils.JedisAdapter;
import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    private JedisAdapter jedisAdapter;

    //第一步：通用关注接口

    //人关注实体(问题，用户，评论等)
    public boolean follow(int userId,int entityType,int entityId) {
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        //把人放到实体的粉丝列表中
        tx.zadd(followerKey,date.getTime(), String.valueOf(userId));//返回值为被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
        //把实体放到人的关注对象列表中
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);//返回值是一个list集合，每个元素是原子化事务的每个命令的返回值
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }

    //取消关注实体
    public boolean unfollow(int userId,int entityType,int entityId) {
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        //把人从实体的粉丝列表中移除
        tx.zrem(followerKey,String.valueOf(userId));
        //把实体从人的关注对象列表中移除
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }

    //第二步：粉丝列表分页

    //获取某一实体的所有粉丝的id
    public List<Integer> getFollowers(int entityType,int entityId,int count) {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }

    //获取某一实体的所有粉丝的id（有分页功能）
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count) {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,count));
    }

    //第三步：关注对象列表分页

    //获取某一用户的所有关注对象的id
    public List<Integer> getFollowees(int userId,int entityType,int count) {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    //获取某一用户的所有关注对象的id（有分页功能）
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count) {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }

    //辅助方法
    public List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids=new ArrayList<>();
        for (String str: idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    //第四步：粉丝或关注对象的数量；是否关注

    //某一实体有多少粉丝
    public long getFollowerCount(int entityType,int entityId) {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }

    //某一用户有多少关注对象
    public long getFolloweeCount(int userId,int entityType) {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    //某个用户是否关注了某个实体
    public boolean isFollower(int userId,int entityType,int entityId) {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }

}
