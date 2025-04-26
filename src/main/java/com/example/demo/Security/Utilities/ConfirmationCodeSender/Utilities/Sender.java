package com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@NoArgsConstructor
public class Sender {
    @Autowired
    private JavaMailSender mailSender;
    private String from;

    public void send(String to, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("Проверочный код");
        simpleMailMessage.setSentDate(new Date());
        simpleMailMessage.setText("Ваш проверочный код: " + code);
        mailSender.send(simpleMailMessage);
    }
}
