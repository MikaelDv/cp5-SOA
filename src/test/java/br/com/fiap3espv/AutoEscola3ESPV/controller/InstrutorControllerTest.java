package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosListagemInstrutores;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.InstrutorService;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrutorNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.enderecoJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InstrutorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InstrutorService service;

    String cadastroJson = """
            {
              "nome": "Instrutor Teste",
              "telefone": "(11) 91234-5678",
              "email": "instrutor@email.com.br",
              "cnh": "01234567890",
              "especialidade": "CARROS",
              "endereco": %s
            }""".formatted(enderecoJson());

    DadosDetalhamentoInstrutor detalhamento = new DadosDetalhamentoInstrutor(
            1L, "Instrutor Teste", "(11) 91234-5678", "instrutor@email.com.br", "01234567890",
            Especialidade.CARROS, new DadosEndereco(dadosEndereco()), true);

    @Test
    @DisplayName("Expectativa: ADMIN cadastra instrutor e recebe 201 com Location")
    @WithMockUser(roles = "ADMIN")
    void cadastrarInstrutorCenario1() throws Exception {
        when(service.cadastrarInstrutor(any())).thenReturn(detalhamento);

        mockMvc.perform(post("/instrutor").contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/instrutor/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.especialidade").value("CARROS"));
    }

    @Test
    @DisplayName("Expectativa: usuário sem perfil ADMIN recebe 403 ao cadastrar instrutor")
    @WithMockUser(roles = "USER")
    void cadastrarInstrutorCenario2() throws Exception {
        mockMvc.perform(post("/instrutor").contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Expectativa: retornar 400 com os campos inválidos")
    @WithMockUser(roles = "ADMIN")
    void cadastrarInstrutorCenario3() throws Exception {
        mockMvc.perform(post("/instrutor").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\", \"email\": \"invalido\", \"cnh\": \"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field", org.hamcrest.Matchers.hasItems("nome", "email", "cnh", "especialidade")));
    }

    @Test
    @DisplayName("Expectativa: listar instrutores paginados")
    @WithMockUser
    void listarInstrutores() throws Exception {
        when(service.listarInstrutores(any())).thenReturn(new PageImpl<>(List.of(
                new DadosListagemInstrutores(1L, "Instrutor Teste", "instrutor@email.com.br", Especialidade.CARROS)
        )));

        mockMvc.perform(get("/instrutor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Instrutor Teste"));
    }

    @Test
    @DisplayName("Expectativa: retornar 404 para instrutor inexistente")
    @WithMockUser
    void detalharInstrutorInexistente() throws Exception {
        when(service.detalharInstrutor(99L)).thenThrow(new InstrutorNotFoundException("ID do instrutor informado não existe!"));

        mockMvc.perform(get("/instrutor/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("ID do instrutor informado não existe!"));
    }

    @Test
    @DisplayName("Expectativa: atualizar instrutor e retornar 200")
    @WithMockUser
    void atualizarInstrutor() throws Exception {
        when(service.atualizarInstrutor(any())).thenReturn(detalhamento);

        mockMvc.perform(put("/instrutor").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"telefone\": \"(11) 91234-5678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Expectativa: excluir instrutor e retornar 204")
    @WithMockUser
    void excluirInstrutor() throws Exception {
        mockMvc.perform(delete("/instrutor/1"))
                .andExpect(status().isNoContent());
        verify(service).excluirInstrutor(1L);
    }
}
