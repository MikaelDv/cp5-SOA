package br.com.fiap3espv.AutoEscola3ESPV.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsAndSwaggerConfigTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: preflight CORS de origem autorizada é aceito")
    void corsCenario1() throws Exception {
        mockMvc.perform(options("/instrucao")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5500")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5500"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("Expectativa: preflight CORS de origem não autorizada é rejeitado")
    void corsCenario2() throws Exception {
        mockMvc.perform(options("/instrucao")
                        .header(HttpHeaders.ORIGIN, "http://site-malicioso.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    @DisplayName("Expectativa: documentação OpenAPI gerada automaticamente e acessível sem token")
    void swagger() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Auto Escola 3ESPV"))
                .andExpect(jsonPath("$.paths['/instrucao']").exists())
                .andExpect(jsonPath("$.paths['/aluno']").exists())
                .andExpect(jsonPath("$.paths['/usuario']").exists())
                .andExpect(jsonPath("$.paths['/cep/{cep}']").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearer-key.scheme").value("bearer"));
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
