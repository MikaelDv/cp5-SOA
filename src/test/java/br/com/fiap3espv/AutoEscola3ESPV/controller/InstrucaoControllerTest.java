package br.com.fiap3espv.AutoEscola3ESPV.controller;

import br.com.fiap3espv.AutoEscola3ESPV.application.service.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrucaoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.proximaSegundaAs10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class InstrucaoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JacksonTester<DadosAgendamento> dadosAgendamentoJson;

    @Autowired
    JacksonTester<DetalhamentoAgendamento> detalhamentoAgendamentoJson;

    @Autowired
    JacksonTester<DadosCancelamento> dadosCancelamentoJson;

    @MockitoBean
    AgendaDeInstrucoes agenda;

    @Test
    @DisplayName("Expectativa: retornar código 400 para informações inválidas")
    @WithMockUser
    void agendarInstrucaoCenario1() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucao"))
                .andReturn()
                .getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Expectativa: retornar código 201 e o JSON do agendamento para informações válidas")
    @WithMockUser
    void agendarInstrucaoCenario2() throws Exception {
        LocalDateTime dataHora = proximaSegundaAs10();
        Especialidade especialidade = Especialidade.CARROS;
        DetalhamentoAgendamento detalhamento =
                new DetalhamentoAgendamento(1L, "Aluno", "Instrutor", especialidade, dataHora, null);
        when(agenda.agendar(any())).thenReturn(detalhamento);

        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAgendamentoJson.write(
                                new DadosAgendamento(1L, null, especialidade, dataHora)
                        ).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).endsWith("/instrucao/1");
        assertThat(response.getContentAsString())
                .isEqualTo(detalhamentoAgendamentoJson.write(detalhamento).getJson());
    }

    @Test
    @DisplayName("Expectativa: retornar código 400 com a mensagem quando uma regra de negócio é violada")
    @WithMockUser
    void agendarInstrucaoCenario3() throws Exception {
        when(agenda.agendar(any())).thenThrow(new ValidacaoException("Instrutor ocupado na data/hora informada!"));

        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAgendamentoJson.write(
                                new DadosAgendamento(1L, 1L, null, proximaSegundaAs10())
                        ).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Instrutor ocupado");
    }

    @Test
    @DisplayName("Expectativa: retornar código 403 quando não há usuário autenticado")
    void agendarInstrucaoCenario4() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucao"))
                .andReturn()
                .getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Expectativa: retornar código 204 ao cancelar instrução com motivo")
    @WithMockUser
    void cancelarInstrucaoCenario1() throws Exception {
        DadosCancelamento dados = new DadosCancelamento(1L, MotivoCancelamento.ALUNO_DESISTIU);

        MockHttpServletResponse response = mockMvc
                .perform(delete("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCancelamentoJson.write(dados).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(agenda).cancelar(dados);
    }

    @Test
    @DisplayName("Expectativa: retornar código 400 ao cancelar sem informar o motivo")
    @WithMockUser
    void cancelarInstrucaoCenario2() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(delete("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_instrucao\": 1}"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("motivo");
    }

    @Test
    @DisplayName("Expectativa: retornar código 404 para instrução inexistente")
    @WithMockUser
    void detalharInstrucaoInexistente() throws Exception {
        when(agenda.detalhar(99L)).thenThrow(new InstrucaoNotFoundException("ID da instrução informada não existe!"));

        MockHttpServletResponse response = mockMvc
                .perform(get("/instrucao/99"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Expectativa: propagar 400 quando o cancelamento viola a antecedência mínima")
    @WithMockUser
    void cancelarInstrucaoCenario3() throws Exception {
        DadosCancelamento dados = new DadosCancelamento(1L, MotivoCancelamento.OUTROS);
        doThrow(new ValidacaoException("Cancelamento permitido apenas com antecedência mínima de 24 horas!"))
                .when(agenda).cancelar(dados);

        MockHttpServletResponse response = mockMvc
                .perform(delete("/instrucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCancelamentoJson.write(dados).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
