package com.ikeyit.message.service.impl;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.message.domain.Email;
import com.ikeyit.message.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * 基于Thymeleaf模板的邮件服务
 */
@Service
public class ThymeleafEmailServiceImpl implements EmailService {

    static final Logger log = LoggerFactory.getLogger(ThymeleafEmailServiceImpl.class);

    String encoding = "UTF-8";

    TemplateEngine templateEngine;

    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    public ThymeleafEmailServiceImpl(JavaMailSender mailSender) {
        ClassLoaderTemplateResolver templateResolver =
                new ClassLoaderTemplateResolver();
        templateResolver.setCacheable(true);
        templateResolver.setCharacterEncoding(encoding);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/email/");
        templateResolver.setSuffix(".html");
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(Email email) {
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true, encoding);
            //邮件发送人
//            messageHelper.setFrom(email.getFrom());
            messageHelper.setFrom(from, email.getFrom());
            //邮件接收人
            messageHelper.setTo(email.getMailTo());
            //邮件主题
            messageHelper.setSubject(email.getSubject());
            //调用thymeleaf模板引擎生成HTML，如果需要国际化，可以改变locale
            Context context = new Context(null, email.getModel());
            String content = templateEngine.process(email.getTemplate(), context);
            messageHelper.setText(content, true);
            //发送
            //TODO 完善
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("邮件发送失败", e);
            //TODO 异常细化
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
