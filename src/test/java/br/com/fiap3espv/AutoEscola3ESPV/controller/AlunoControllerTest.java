package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosListagemAlunos;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AlunoService;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
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
public class AlunoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AlunoService service;

    DadosDetalhamentoAluno detalhamento = new DadosDetalhamentoAluno(
            1L, "Aluno Teste", "(11) 98765-4321", "aluno@email.com.br", "12345678901",
            new DadosEndereco(dadosEndereco()), true);

    @Test
    @DisplayName("Expectativa: cadastrar aluno e retornar 201 com Location")
    @WithMockUser
    void cadastrarAlunoCenario1() throws Exception {
        when(service.cadastrarAluno(any())).thenReturn(detalhamento);

        mockMvc.perform(post("/aluno").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "nome": "Aluno Teste",
                          "telefone": "(11) 98765-4321",
                          "email": "aluno@email.com.br",
                          "cpf": "12345678901",
                          "endereco": %s
                        }""".formatted(enderecoJson())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/aluno/1")))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Expectativa: retornar 400 para CPF inválido e endereço ausente")
    @WithMockUser
    void cadastrarAlunoCenario2() throws Exception {
        mockMvc.perform(post("/aluno").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "nome": "Aluno Teste",
                          "telefone": "(11) 98765-4321",
                          "email": "aluno@email.com.br",
                          "cpf": "123"
                        }"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field", org.hamcrest.Matchers.hasItems("cpf", "endereco")));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Expectativa: retornar 403 sem autenticação")
    void cadastrarAlunoCenario3() throws Exception {
        mockMvc.perform(get("/aluno"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Expectativa: listar alunos paginados")
    @WithMockUser
    void listarAlunos() throws Exception {
        when(service.listarAlunos(any())).thenReturn(new PageImpl<>(List.of(
                new DadosListagemAlunos(1L, "Aluno Teste", "aluno@email.com.br", "12345678901")
        )));

        mockMvc.perform(get("/aluno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Expectativa: retornar 404 para aluno inexistente")
    @WithMockUser
    void detalharAlunoInexistente() throws Exception {
        when(service.detalharAluno(99L)).thenThrow(new AlunoNotFoundException("ID do aluno informado não existe!"));

        mockMvc.perform(get("/aluno/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Expectativa: atualizar aluno e retornar 200")
    @WithMockUser
    void atualizarAluno() throws Exception {
        when(service.atualizarAluno(any())).thenReturn(detalhamento);

        mockMvc.perform(put("/aluno").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"nome\": \"Aluno Teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Expectativa: excluir aluno e retornar 204")
    @WithMockUser
    void excluirAluno() throws Exception {
        mockMvc.perform(delete("/aluno/1"))
                .andExpect(status().isNoContent());
        verify(service).excluirAluno(1L);
    }
}
