package com.oshacker.Q2ACommunity.async.handler;

import com.oshacker.Q2ACommunity.async.EventHandler;
import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.model.Message;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.service.MessageService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

//关注handler
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void dohandle(EventModel eventModel) {
        //当发生关注类型的事件，就给被关注的人发站内信
        Message message=new Message();
        message.setFromId(ConstantUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user=userService.getUserById(eventModel.getActorId());
        if (eventModel.getEntityType()==ConstantUtil.ENTITY_QUESTION) {
            message.setContent("用户"+user.getName()+"关注了你的问题: http://127.0.0.1:8888/question/"+eventModel.getEntityId());
        } else if (eventModel.getEntityType()==ConstantUtil.ENTITY_USER) {
            message.setContent("用户"+user.getName()+"关注了你: http://127.0.0.1:8888/user/"+eventModel.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        //只注册点赞类型的事件
        return Arrays.asList(EventType.FOLLOW);
    }
}
