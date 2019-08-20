package com.oshacker.Q2ACommunity.async.handler;

import com.oshacker.Q2ACommunity.async.EventHandler;
import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//如果再有其他的事件处理函数，只需要实现EventHandler接口，
//很容易实现扩展。比如LoginExceptionHandler，即登录异常，
//就给它发一封邮件。
@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    private MailSender mailSender;

    @Override
    public void dohandle(EventModel eventModel) {
        //登录异常就发送邮件
        Map<String,Object> map=new HashMap<>();
        map.put("username",eventModel.getExt("username")); //模板中需要什么就传什么
        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"),"登录IP异常",
                "mails/login_exception.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN); //可以关注多种事件
    }

}
