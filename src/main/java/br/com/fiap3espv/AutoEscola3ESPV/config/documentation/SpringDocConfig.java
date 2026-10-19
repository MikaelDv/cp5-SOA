package br.com.fiap3espv.AutoEscola3ESPV.config.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("Auto Escola 3ESPV")
                        .description("""
                                API REST para gerenciamento de instruções da Auto Escola 3ESPV.
                                Para acessar os endpoints protegidos, faça login em POST /login
                                e informe o token retornado no botão Authorize.""")
                        .version("v1")
                        .contact(new Contact()
                                .name("Auto Escola 3ESPV")
                                .email("autoescola@email.com.br")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                );
    }
}