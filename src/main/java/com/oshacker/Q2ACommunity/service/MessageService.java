package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.MessageDAO;
import com.oshacker.Q2ACommunity.model.Message;
import com.oshacker.Q2ACommunity.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));//HTML过滤
        message.setContent(sensitiveFilter.filter(message.getContent()));//敏感词过滤
        return messageDAO.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId,offset,limit);
    }

    public int getConversationUnreadCount(int userId,String conversationId){
        return messageDAO.getConversationUnreadCount(userId,conversationId);
    }

    public void updateConversationUnreadCount(int userId,String conversation) {
        messageDAO.updateHasRead(userId,conversation,1);
    }
}
