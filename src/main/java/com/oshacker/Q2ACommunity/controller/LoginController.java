package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger LOGGER= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage() {
        return "login";
    }

    @RequestMapping(path={"/reg"},method={RequestMethod.POST})
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password) {

        try {
            Map<String,String>  map=userService.register(username,password);
            if (map.containsKey("msg")) {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
            return "redirect:/";
        } catch (Exception e) {
            LOGGER.error("注册失败："+e.getMessage());
            return "login";
        }
    }
}
