package com.bookstore.utility;

import com.bookstore.domain.Order;
import com.bookstore.domain.User;
//import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.core.env.Environment;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

@Component
public class MailConstructor {

    @Autowired
    private Environment env;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user, String password) {
        String url = contextPath + "/newUser?token=" + token;
        String message = "\nPlease click on this link to verify your email and edit your personal information."
                + "\nYour password is: \n" + password;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Le's Bookstore - New User");
        email.setText(url + message);
        email.setFrom(env.getProperty("support.email"));

        return email;
    }

    public MimeMessagePreparator constructOrderConfirmationEmail(User user, Order order, Locale locale) {
        Context context = new Context(locale); // Pass locale for i18n support
        context.setVariable("order", order);
        context.setVariable("user", user);
        context.setVariable("cartItemList", order.getCartItemList());

        String text = templateEngine.process("orderConfirmationEmailTemplate", context);

        return mimeMessage -> {
            MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
            email.setTo(user.getEmail());
            email.setSubject("Order Confirmation - " + order.getId());
            email.setText(text, true); // true indicates HTML content
            email.setFrom(new InternetAddress(env.getProperty("support.email", "no-reply@bookstore.com")));
        };
    }

    public void sendOrderConfirmationEmail(User user, Order order, Locale locale) {
        MimeMessagePreparator preparator = constructOrderConfirmationEmail(user, order, locale);
        mailSender.send(preparator);
    }

    public void sendPasswordResetEmail(User user, String token, String password) {
        SimpleMailMessage email = constructResetTokenEmail("http://localhost:8080", Locale.getDefault(), token, user, password);
        mailSender.send(email);
    }
}
