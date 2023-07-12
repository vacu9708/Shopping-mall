package com.notification.notification.email;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

   @KafkaListener(topics = {"sendEmail"}, containerFactory = "ListenerContainerFactoryString", groupId = "group1")
    void sendTextMessage(String msg) throws JsonMappingException, JsonProcessingException {
        SimpleMailMessage email = new SimpleMailMessage();
        Email parsed = new ObjectMapper().readValue(msg, Email.class);

        email.setTo("vacu9708@naver.com"); 
        email.setSubject("Test from spring boot");
        email.setText(parsed.msg);

        emailSender.send(email);
    }

    void sendMessageWithAttachment() throws MessagingException {
        MimeMessage email = emailSender.createMimeMessage(); 
        MimeMessageHelper emailHelper = new MimeMessageHelper(email, true);

        emailHelper.setTo("vacu9708@naver.com"); 
        emailHelper.setSubject("Test from spring boot"); 
        emailHelper.setText("Hello World \n Spring Boot Email");
        
        FileSystemResource file = new FileSystemResource(new File("pathToAttachment"));
        emailHelper.addAttachment("Invoice", file);
    }
}
