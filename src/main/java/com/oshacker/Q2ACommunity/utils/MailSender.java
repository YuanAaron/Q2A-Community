package com.oshacker.Q2ACommunity.utils;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Override//初始化mailSender
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("1500438364");//qq号
        mailSender.setPassword("jkfahwhadzdwhhca");//授权码
        mailSender.setHost("smtp.qq.com");//发送服务器
        mailSender.setPort(465);//发送服务器端口
        mailSender.setProtocol("smtps");//协议，类似https
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);//SSL
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    //发送邮件的接口
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("方@圆");//发件人昵称
            InternetAddress from = new InternetAddress(nick + "<1500438364@qq.com>");//发件人地址

            //邮件内容使用的模板
            Template tpl=freeMarkerConfigurer.getConfiguration().getTemplate(template);//通过模板名获取FreeMarker模板实例
            String result= FreeMarkerTemplateUtils.processTemplateIntoString(tpl,model);//解析模板并替换动态数据

            //封装一封邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(from);//发件人
            mimeMessageHelper.setTo(to);//收件人
            mimeMessageHelper.setSubject(subject);//邮件主题
            mimeMessageHelper.setText(result, true);//邮件内容，HTML形式展示
            mailSender.send(mimeMessage);//发送
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
}
