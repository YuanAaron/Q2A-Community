package com.oshacker.Q2ACommunity.async;

//对于enum类的通俗解释：
//EventType相当于数据库名，LIKE相当于表名，0对应Like表中的id字段
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);

    private int value;
    EventType(int value) {
        this.value=value;
    }
    public int getValue() {
        return value;
    }

}
