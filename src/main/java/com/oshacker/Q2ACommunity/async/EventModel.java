package com.oshacker.Q2ACommunity.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    //事件类型，比如点赞、评论
    private EventType type;
    //触发者，谁点了赞、谁发表了评论
    private int actorId;
    // 载体:给谁点赞、评论了什么
    private int entityType;
    private int entityId;
    //载体的所有者，方便建立人与人之间的关联，发站内信通知等
    private int entityOwnerId;
    //扩展字段，类似于ViewObject，用于保留事件发生时的信息
    private Map<String,String> exts=new HashMap<>();

    //构造方法
    public EventModel() {}//反序列化(反射)需要默认构造函数

    public EventModel(EventType type) {
        this.type=type;
    }

    //为了能够实现链式调用，下面所有的set方法都进行了修改

    //这两个方法是为exts额外添加的
    public EventModel setExt(String key,String value) {
        exts.put(key,value);
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
