package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.model.Message;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.model.ViewObject;
import com.oshacker.Q2ACommunity.service.MessageService;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger= LoggerFactory.getLogger("MessageController.calss");

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(path="/msg/list",method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        try {
            if (hostHolder.getUser()==null) {
                return "redirect:/reglogin";
            } else {
                int localUserId = hostHolder.getUser().getId();
                List<Message> messages = messageService.getConversationList(localUserId, 0, 10);
                List<ViewObject> conversations = new ArrayList<ViewObject>();
                for (Message msg : messages) {
                    ViewObject vo = new ViewObject();
                    vo.set("conversation", msg);
                    //不想看到自己的信息，只想看到对方的信息
                    int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                    vo.set("user", userService.getUserById(targetId));
                    vo.set("unread",messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
                    conversations.add(vo);
                }
                model.addAttribute("conversations", conversations);
            }
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path="/msg/detail",method = {RequestMethod.GET})
    public String getConversationDetail(Model model,@RequestParam("conversationId") String conversationId) {
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("user",userService.getUserById(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages", messages);

            //更新未读信息的数量(自己添加，老师没有讲)
            messageService.updateConversationUnreadCount(hostHolder.getUser().getId(),conversationId);
        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path={"/msg/addMessage"},method={RequestMethod.POST})
    @ResponseBody
    public String addComment(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {

        try {
            if (hostHolder.getUser()==null) {//未登录
                //从popupAdd.js中可知，999表示未登录，跳转到登录页面
                return JSONUtil.getJSONString(999,"未登录");
            } else {
                User user= userService.selectByName(toName);
                if (user==null) {
                    return JSONUtil.getJSONString(1,"用户不存在");
                }
                Message message=new Message();
                message.setFromId(hostHolder.getUser().getId());
                message.setToId(user.getId());
                message.setContent(content);
                message.setCreatedDate(new Date());
                messageService.addMessage(message);
                return JSONUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("发送消息失败"+e.getMessage());
            return JSONUtil.getJSONString(1,"发信失败");
        }
    }
}
