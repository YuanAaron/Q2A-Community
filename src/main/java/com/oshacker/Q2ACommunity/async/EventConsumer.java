package com.oshacker.Q2ACommunity.async;

import com.alibaba.fastjson.JSON;
import com.oshacker.Q2ACommunity.utils.JedisAdapter;
import com.oshacker.Q2ACommunity.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    //1、当从队列中取出一个event（EventModel）时，EventConsumer根据event的EventType
    //找到所有相关的EventHandler，然后各个handler进行处理。因此，在程序起来的时候就要将
    //config初始化好，建立event和EventHandler之间的关系。

    //在生产者-消费者模型中，这里的Map<EventType,List<EventHandler>>相当于路由，
    //主要起消息分发的作用。
    private Map<EventType,List<EventHandler>> config=new HashMap<>();
    private ApplicationContext applicationContext;

    //2、初始化时会找到所有EventHanlder接口的实现类,然后通过getSupportEvent就可以找到
    //这个handler关心哪些event类型，然后就可以把event类型和handler的映射关系注册到config中，
    //当下次再有该事件类型进来的时候，就能找到它对应的那些handler，即List<EventHandler>。
    @Override
    public void afterPropertiesSet() throws Exception {
        //在Spring的上下文找到所有EventHanlder接口的实现类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //找到handler关心哪些事件类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                //把event类型和handler的映射关系注册到config中
                for (EventType type : eventTypes) {
                    //初始化时注册event的类型
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        //3、一直不断的从队列中取事件，取到后就找关心该事件的handler进行处理
        ExecutorService service= Executors.newFixedThreadPool(5);
        service.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    //移除并获取列表的最后一个元素
                    //超时时间设置为0，如果队列中没有事件，就一直阻塞直到发现可弹出元素为止
                    List<String> event = jedisAdapter.brpop(0, key);

                    //返回一个含有两个元素的列表，分别是弹出元素的key和value。
                    for (String message : event) {
                        if (message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);//反序列化得到事件对象

                        //找到所有关心该事件的handler处理该事件
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler eventHandler : config.get(eventModel.getType())) {
                            eventHandler.dohandle(eventModel);
                        }
                    }
                }
            }
        });
    }


    //因为afterPropertiesSet方法中用到了ApplicationContext，因此需要实现ApplicationContextAware接口以获取它。
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
