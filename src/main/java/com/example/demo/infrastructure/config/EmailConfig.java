package com.example.demo.infrastructure.config;

import com.example.demo.infrastructure.mail.MailSenderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AllArgsConstructor
public class EmailConfig {
    @Autowired
    private Environment environment;

    @Bean
    public MailSenderService sender() {
        MailSenderService sender = new MailSenderService();
        sender.setFrom(environment.getProperty("spring.mail.username"));
        return sender;
    }
}
