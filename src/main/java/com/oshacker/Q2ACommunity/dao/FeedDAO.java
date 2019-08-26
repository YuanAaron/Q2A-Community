package com.oshacker.Q2ACommunity.dao;

import com.oshacker.Q2ACommunity.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedDAO {
    String TABLE_NAME="feed";
    String INSERT_FIELDS="type,user_id,created_date,data";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") ",
            "values(#{type},#{userId},#{createdDate},#{data})"})
    int addFeed(Feed feed);

    //如果没有登录，timeline展示的是所有人中最新的几条
    //如果登录了，timeline展示的是所有关注的人的新鲜事列表

    //拉模式
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

    //推模式
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Feed getFeedById(int id);


}

