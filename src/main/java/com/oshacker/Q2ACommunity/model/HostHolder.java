package com.oshacker.Q2ACommunity.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    //private User user; //如果多个用户/线程同时访问，user只能表示同一个用户

    /*相当于Map<thread,User> users，看起来是一个变量，实际上每个线程都有一份拷贝且
    所占用的内存是不同的，但都可以通过users来访问。当你getUser的时候，它会取出与当前
    线程关联的那个User对象。*/

    /*使用ThreadLocal的好处：
    如果多个用户/线程同时访问某个页面，且这些用户都已经登录，多个线程共用一个User对象就乱了，
    使用ThreadLocal每个线程都有自己的User对象*/
    private static ThreadLocal<User> users=new ThreadLocal<>();

    public User getUser() {
        return users.get(); //get的时候默认从当前线程提取User对象
    }

    public void setUser(User user) {
        users.set(user); //set的时候默认把当前线程作为key，User对象作为value
    }

    public void clear() {
        users.remove();
    }
}
