package com.oshacker.Q2ACommunity.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

//新鲜事的model
public class Feed {
    private int id;
    private int type;//关注类型的新鲜事、评论类型的新鲜事等
    private int userId;//新鲜事发布者
    private Date createdDate;
    //新鲜事的内容：json格式
    private String data;//A关注B（保存A、B）；A评论某个问题（保存A，问题，评论的summery）

    //辅助字段
    private JSONObject jsonObject=null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        jsonObject=JSONObject.parseObject(data); //为处理json串添加
    }

    public String get(String key) { //为处理json串添加
        return jsonObject == null ? null : jsonObject.getString(key);
    }


}
