package com.oshacker.Q2ACommunity.async;

import java.util.List;

public interface EventHandler {
    //event处理方法
    void dohandle(EventModel eventModel);

    //声明自己关心哪些事件类型,当有其中的事件发生时，就调用上面的dohanle方法
    List<EventType> getSupportEventTypes();

}
