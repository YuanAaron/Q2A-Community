package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.UserDAO;
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
        return map;
    }

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }
}
