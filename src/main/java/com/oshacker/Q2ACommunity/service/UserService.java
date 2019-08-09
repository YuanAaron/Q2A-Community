package com.oshacker.Q2ACommunity.service;

import com.oshacker.Q2ACommunity.dao.UserDAO;
import com.oshacker.Q2ACommunity.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }
}
