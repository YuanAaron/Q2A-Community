package com.oshacker.Q2ACommunity.configuration;

import com.oshacker.Q2ACommunity.intercepter.LoginRequiredIntercepter;
import com.oshacker.Q2ACommunity.intercepter.PassportIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration //老师直接用的@Component 
public class IntercepterConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private PassportIntercepter passportIntercepter;

    @Autowired
    private LoginRequiredIntercepter loginRequiredIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注意这两个拦截器是有先后顺序的
        registry.addInterceptor(passportIntercepter);
        registry.addInterceptor(loginRequiredIntercepter).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
