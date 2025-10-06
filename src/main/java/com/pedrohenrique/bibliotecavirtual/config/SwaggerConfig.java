package com.pedrohenrique.bibliotecavirtual.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bibliotecaVirtualOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca Virtual")
                        .description("Documentação da API da Biblioteca Virtual")
                        .version("1.0.0"));
    }
}
