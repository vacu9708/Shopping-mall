package com.notification.notification.email;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.notification.email.Dto.EmailDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;
    Executor threadPool = Executors.newFixedThreadPool(10);

   @KafkaListener(topics = {"email"}, containerFactory = "ListenerContainerFactoryString")
    void prepareEmail(String emailJson) throws JsonMappingException, JsonProcessingException {
        SimpleMailMessage email = new SimpleMailMessage();
        EmailDto emailDto=null;
        emailDto = new ObjectMapper().readValue(emailJson, EmailDto.class);
        System.out.println("Sending email! to "+ emailDto.getTo());

        email.setTo(emailDto.getTo()); 
        email.setSubject(emailDto.getSubject());
        email.setText(emailDto.getText());
        // emailSender.send(email); // Slow without threads!
        CompletableFuture.runAsync(() -> {
            try{
                emailSender.send(email);
            } catch(Exception e){
                System.out.println("Error sending email!");
                e.printStackTrace();
            }
        }, threadPool);
        // doesn't work!
        // CompletableFuture.runAsync(() -> {
        //     try{
        //         emailSender.send(email);
        //     } catch(Exception e){
        //         System.out.println("Error sending email!");
        //         e.printStackTrace();
        //     }
        // });
    }

    // void sendMessageWithAttachment() throws MessagingException {
    //     MimeMessage email = emailSender.createMimeMessage(); 
    //     MimeMessageHelper emailHelper = new MimeMessageHelper(email, true);

    //     emailHelper.setTo("vacu9708@naver.com"); 
    //     emailHelper.setSubject("Test from spring boot"); 
    //     emailHelper.setText("Hello World \n Spring Boot Email");
        
    //     FileSystemResource file = new FileSystemResource(new File("pathToAttachment"));
    //     emailHelper.addAttachment("Invoice", file);
    // }
}
