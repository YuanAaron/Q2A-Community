package com.oshacker.Q2ACommunity.intercepter;

import com.oshacker.Q2ACommunity.dao.LoginTicketDAO;
import com.oshacker.Q2ACommunity.model.HostHolder;
import com.oshacker.Q2ACommunity.model.LoginTicket;
import com.oshacker.Q2ACommunity.model.User;
import com.oshacker.Q2ACommunity.service.UserService;
import com.oshacker.Q2ACommunity.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

//用户身份的验证(知道浏览页面的是谁)
//PassportIntercepter做了那么多工作，就是为了构造能够通过控制反转随处访问的HostHolder,进而获得user
@Component
public class PassportIntercepter implements HandlerInterceptor {

    @Autowired
    private LoginTicketDAO loginTicketDAO;
    
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "ticket");
        if (cookie!=null) {
            String ticket = cookie.getValue();
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);

            if (loginTicket==null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus()!=0) {
                return true; //放行
            }

            //当你访问任何页面时（发送不同的请求），都会通过请求中的token来获取登录的user
            User user = userService.getUserById(loginTicket.getUserId());
            //将当前线程及其对应的user对象保存到HostHolder类的users中
            hostHolder.setUser(user);
        }
        return true; //放行
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView!=null) {
            //在Controller处理完成后、渲染前，将user放到模板渲染的上下文,这样在模板中就可以直接访问user
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear(); //渲染结束后，清除掉当前线程对应的user
    }
}
