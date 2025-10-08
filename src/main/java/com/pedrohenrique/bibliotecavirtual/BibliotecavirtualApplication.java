package com.pedrohenrique.bibliotecavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@PropertySource("classpath:messages/global.properties")
@SpringBootApplication
@EnableAsync
public class BibliotecavirtualApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecavirtualApplication.class, args);
	}

}
