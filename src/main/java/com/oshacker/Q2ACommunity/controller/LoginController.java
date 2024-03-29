package com.oshacker.Q2ACommunity.controller;

import com.oshacker.Q2ACommunity.async.EventModel;
import com.oshacker.Q2ACommunity.async.EventProducer;
import com.oshacker.Q2ACommunity.async.EventType;
import com.oshacker.Q2ACommunity.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path={"/logout"},method={RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path={"/login"},method={RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme",defaultValue = "false") Boolean rememberme,
                        HttpServletResponse response,
                        @RequestParam(value = "next",required = false) String next) {
        try {
            Map<String,String>  map=userService.login(username,password);
            //向浏览器下发ticket
            if (map.containsKey("ticket")) {
                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(5*24*3600);
                }
                response.addCookie(cookie);

                //假如登录异常，就把登录异常事件发送到队列
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setExt("username",username)
                        .setExt("email","1500438364@qq.com"));//收件人

                if (!StringUtils.isBlank(next)) {
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            LOGGER.error("登录失败："+e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model,@RequestParam(value = "next",required = false) String next) {
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path={"/reg"},method={RequestMethod.POST})
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                           HttpServletResponse response,
                           @RequestParam(value = "next",required = false) String next) {

        try {
            Map<String,String>  map=userService.register(username,password);
            //向浏览器下发ticket
            if (map.containsKey("ticket")) {
                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(5*24*3600);
                }
                response.addCookie(cookie);
                if (!StringUtils.isBlank(next)) {
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            LOGGER.error("注册失败："+e.getMessage());
            return "login";
        }
    }
}
