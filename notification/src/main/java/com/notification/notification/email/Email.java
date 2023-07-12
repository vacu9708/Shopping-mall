package com.notification.notification.email;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@RestController
public class Email {
    @Autowired
    private JavaMailSender emailSender;
    @GetMapping("/sendEmail")
    void sendTextMessage() {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo("vacu9708@naver.com"); 
        message.setSubject("Test from spring boot"); 
        message.setText("Hello World \n Spring Boot Email");

        emailSender.send(message);
    }

    void sendMessageWithAttachment() throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage(); 
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

        messageHelper.setTo("vacu9708@naver.com"); 
        messageHelper.setSubject("Test from spring boot"); 
        messageHelper.setText("Hello World \n Spring Boot Email");
        
        FileSystemResource file = new FileSystemResource(new File("pathToAttachment"));
        messageHelper.addAttachment("Invoice", file);
    }
}
