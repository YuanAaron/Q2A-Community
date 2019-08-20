package com.oshacker.Q2ACommunity.async;

import com.alibaba.fastjson.JSONObject;
import com.oshacker.Q2ACommunity.utils.JedisAdapter;
import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//这里使用的是redis中的List，也可以使用BlockingQueue这样中间的一条队列来实现异步，
//一边在put，另一边在take。这两者只是存放的地方不同而已。

//使用redis的好处是：可以多台机器共享redis,即每台机器提交的事件都可以发送
//得到公共的redis。然后，多个消费机器可以从redis统一取。
@Service
public class EventProducer {

    @Autowired
    private JedisAdapter jedisAdapter;

    //把事件发送进队列
    public boolean fireEvent(EventModel eventModel) {
        try {

            String json = JSONObject.toJSONString(eventModel);//序列化
            String key= RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
