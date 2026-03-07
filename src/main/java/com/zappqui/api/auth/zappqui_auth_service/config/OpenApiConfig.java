package com.zappqui.api.auth.zappqui_auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI zappquiAuthOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Zappqui Auth Service API")
                        .description("Serviço de autenticação e autorização da plataforma Zappqui. "
                                + "Fornece endpoints para criação de usuários, autenticação via JWT e validação de tokens.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Zappqui Team")
                                .email("contato@zappqui.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Servidor Local")));
    }
}

