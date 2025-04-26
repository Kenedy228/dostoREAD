package com.example.demo.Security;

import com.example.demo.Security.Utilities.ConfirmationCodeSender.Utilities.Sender;
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
    public Sender sender() {
        Sender sender = new Sender();
        sender.setFrom(environment.getProperty("spring.mail.username"));
        return sender;
    }
}
