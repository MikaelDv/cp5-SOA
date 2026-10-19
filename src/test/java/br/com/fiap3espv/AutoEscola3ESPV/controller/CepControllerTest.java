package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.ConsultaCepPort;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.CepNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ServicoExternoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CepControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ConsultaCepPort consultaCep;

    @Test
    @DisplayName("Expectativa: retornar o endereço do CEP consultado")
    @WithMockUser
    void consultarCepCenario1() throws Exception {
        when(consultaCep.consultar("01234567")).thenReturn(dadosEndereco());

        mockMvc.perform(get("/cep/01234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Teste"))
                .andExpect(jsonPath("$.cidade").value("TestCity"));
    }

    @Test
    @DisplayName("Expectativa: retornar 404 para CEP inexistente e 503 se o ViaCEP estiver fora")
    @WithMockUser
    void consultarCepCenario2() throws Exception {
        when(consultaCep.consultar("99999999")).thenThrow(new CepNotFoundException("CEP informado não foi encontrado!"));
        when(consultaCep.consultar("01001000")).thenThrow(new ServicoExternoException("Falha ao consultar o serviço ViaCEP!", null));

        mockMvc.perform(get("/cep/99999999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/cep/01001000")).andExpect(status().isServiceUnavailable());
    }
}
