package com.oshacker.Q2ACommunity.utils;

public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String BIS_LIKE="LIKE";
    private static final String BIS_DISLIKE="DISLIKE";

    private static final String BIS_EVENTQUEUE="EVENTQUEUE";

    private static final String BIS_FOLLOWER="FOLLOWER";//粉丝
    private static final String BIS_FOLLOWEE="FOLLOWEE";//关注对象

//    private static final String BIS_TIMELINE="TIMELINE";


    public static String getLikeKey(int entityType,int entityId) {
        return BIS_LIKE+SPLIT+ String.valueOf(entityType)+SPLIT+ String.valueOf(entityId);
    }

    public static String getDislikeKey(int entityType,int entityId){
        return BIS_DISLIKE+SPLIT+ String.valueOf(entityType)+SPLIT+ String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIS_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType,int entityId) {
        return BIS_FOLLOWER+SPLIT+ String.valueOf(entityType)+SPLIT+ String.valueOf(entityId);
    }

    public static String getFolloweeKey(int userId,int entityType) {
        return BIS_FOLLOWEE+SPLIT+ String.valueOf(userId)+SPLIT+ String.valueOf(entityType);
    }

//    public static String getBisTimeline(int userId) {
//        return BIS_TIMELINE+SPLIT+ String.valueOf(userId);
//    }

}
