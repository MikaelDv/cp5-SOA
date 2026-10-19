package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.UsuarioService;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Perfil;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UsuarioService service;

    String cadastroJson = """
            {"login": "secretaria", "senha": "senha123", "perfil": "USER"}""";

    @Test
    @DisplayName("Expectativa: ADMIN cadastra usuário e a senha não é devolvida")
    @WithMockUser(roles = "ADMIN")
    void cadastrarUsuarioCenario1() throws Exception {
        when(service.cadastrarUsuario(any()))
                .thenReturn(new DadosDetalhamentoUsuario(2L, "secretaria", Perfil.USER, true));

        mockMvc.perform(post("/usuario").contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("secretaria"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("Expectativa: perfil USER não acessa o CRUD de usuários (403)")
    @WithMockUser(roles = "USER")
    void cadastrarUsuarioCenario2() throws Exception {
        mockMvc.perform(post("/usuario").contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/usuario"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Expectativa: retornar 400 para senha curta")
    @WithMockUser(roles = "ADMIN")
    void cadastrarUsuarioCenario3() throws Exception {
        mockMvc.perform(post("/usuario").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"x\", \"senha\": \"123\", \"perfil\": \"USER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("senha"));
    }

    @Test
    @DisplayName("Expectativa: retornar 400 quando o login já existe")
    @WithMockUser(roles = "ADMIN")
    void cadastrarUsuarioCenario4() throws Exception {
        when(service.cadastrarUsuario(any())).thenThrow(new ValidacaoException("Login informado já está em uso!"));

        mockMvc.perform(post("/usuario").contentType(MediaType.APPLICATION_JSON).content(cadastroJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Login informado já está em uso!"));
    }

    @Test
    @DisplayName("Expectativa: ADMIN lista, atualiza e exclui usuários")
    @WithMockUser(roles = "ADMIN")
    void listarAtualizarExcluir() throws Exception {
        when(service.listarUsuarios(any())).thenReturn(new PageImpl<>(List.of(
                new DadosDetalhamentoUsuario(1L, "admin", Perfil.ADMIN, true))));
        when(service.atualizarUsuario(any()))
                .thenReturn(new DadosDetalhamentoUsuario(2L, "secretaria", Perfil.ADMIN, true));

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].perfil").value("ADMIN"));
        mockMvc.perform(put("/usuario").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 2, \"perfil\": \"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
        mockMvc.perform(delete("/usuario/2"))
                .andExpect(status().isNoContent());
        verify(service).excluirUsuario(2L);
    }
}
