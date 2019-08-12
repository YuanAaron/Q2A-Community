package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.LoginTicketDAO;
import com.oshacker.Q2ACommunity.dao.UserDAO;
import com.oshacker.Q2ACommunity.model.LoginTicket;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket,1);
    }

    public Map<String,String> login(String username,String password) {
        Map<String,String> map=new HashMap<>();
        if (StringUtils.isBlank(username)) { //str==null||str.length()==0||str.trim().length()==0
            map.put("msg","用户名不能为空"); //好像已经在前端处理了
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg","密码不能为空"); //好像已经在前端处理了
            return map;
        }

        //检验用户名是否存在
        User user = userDAO.selectByName(username);
        if (user==null) {
            map.put("msg","用户名不存在");
            return map;
        }

        //用户名存在，检验密码是否正确
        if (!user.getPassword().equals(MD5Util.MD5(password+user.getSalt()))) {
            map.put("msg","密码错误");
            return map;
        }

        //用户名存在，密码正确，即登录成功

        //登录成功后后台生成ticket
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public String addLoginTicket(int userId) {
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(userId);
        Date now=new Date();
        now.setTime(now.getTime()+7*24*3600*1000);//有效期为7天
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public Map<String,String> register(String username,String password) {
        Map<String,String> map=new HashMap<>();
        if (StringUtils.isBlank(username)) { //str==null||str.length()==0||str.trim().length()==0
            map.put("msg","用户名不能为空"); //好像已经在前端处理了
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg","密码不能为空"); //好像已经在前端处理了
            return map;
        }

        //用户名不能重复
        User user = userDAO.selectByName(username);
        if (user!=null) {
            map.put("msg","用户名已存在");
            return map;
        }

        //其他用户合法性检测就先不做了，比如用户名必须是邮箱手机号，可以通过正则去判断，否则就不让注册

        //注册
        user=new User();
        user.setName(username);
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(MD5Util.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //注册成功，自动登录，同样后台生成ticket
        String ticket=addLoginTicket(user.getId());//t票和用户关联
        map.put("ticket",ticket);
        return map;
    }

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }
}
