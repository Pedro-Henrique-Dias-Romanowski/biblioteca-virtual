package com.pedrohenrique.bibliotecavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:messages.properties")
@SpringBootApplication
public class BibliotecavirtualApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecavirtualApplication.class, args);
	}

}
