package com.example.demo;

import com.example.demo.infrastructure.config.AdminBootstrapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class DostoReadApplication {

	public static void main(String[] args) {
		SpringApplication.run(DostoReadApplication.class, args);
	}

}
