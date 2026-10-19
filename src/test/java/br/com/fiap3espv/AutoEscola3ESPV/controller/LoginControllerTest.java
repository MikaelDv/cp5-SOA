package br.com.fiap3espv.AutoEscola3ESPV.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Teste de integração: banco H2 + migrations + Spring Security + JWT, sem mocks
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: login do admin gera token JWT que libera os endpoints protegidos")
    void efetuarLoginCenario1() throws Exception {
        String resposta = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"senha\": \"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenJWT").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = JsonPath.read(resposta, "$.tokenJWT");

        mockMvc.perform(get("/usuario").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].login").value("admin"));
    }

    @Test
    @DisplayName("Expectativa: retornar 401 para senha incorreta")
    void efetuarLoginCenario2() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"senha\": \"errada\"}"))
                .andExpect(status().isUnauthorized());
    }
}
