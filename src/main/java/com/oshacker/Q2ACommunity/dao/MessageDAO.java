package com.oshacker.Q2ACommunity.dao;

import com.oshacker.Q2ACommunity.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME="message";
    String INSERT_FIELDS="from_id,to_id,content,has_read,conversation_id,created_date";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,") ",
            "values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where conversation_id=#{conversationId} " +
            "order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    //老师的做法：
    //SELECT COUNT(*) id,from_id,to_id,content,created_date,has_read,conversation_id
    //FROM (SELECT * FROM message ORDER BY created_date DESC) tt
    //GROUP BY conversation_id ORDER BY created_date DESC;
    //上面的语句我尝试了，但是并不会显示每组最新的结果。

    //我的做法：
    //1、SELECT MAX(created_date) cdate, COUNT(*) cnt FROM message GROUP BY conversation_id;
    //MAX(created_date)是为了分组后显示最新的结果;COUNT(*)是为了显示每组的个数

    //2、SELECT cnt id,from_id,to_id,content,created_date,has_read,conversation_id FROM message t1 INNER JOIN
    //(SELECT MAX(created_date) cdate,COUNT(*) cnt FROM message GROUP BY conversation_id) t2
    //ON t1.created_date=t2.cdate ORDER BY created_date DESC;
    @Select({"select cnt id,",INSERT_FIELDS," from ",TABLE_NAME," t1 INNER JOIN " +
            "(SELECT MAX(created_date) cdate,COUNT(*) cnt FROM ",TABLE_NAME,
            "WHERE from_id=#{userId} OR to_id=#{userId} GROUP BY conversation_id) t2 " +
            "ON t1.created_date=t2.cdate ORDER BY created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from ",TABLE_NAME," where has_read is null and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId,
                                   @Param("conversationId") String conversationId);

    @Update({"update ",TABLE_NAME," set has_read=#{hasRead} where to_id=#{userId} and conversation_id=#{conversationId}"})
    void updateHasRead(@Param("userId") int userId,
                       @Param("conversationId") String conversationId,
                       @Param("hasRead") int hasRead);
}
