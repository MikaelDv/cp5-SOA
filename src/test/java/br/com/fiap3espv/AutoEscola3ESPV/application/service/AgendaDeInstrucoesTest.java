package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.cancelamento.ValidadorAntecedenciaCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.cancelamento.ValidadorInstrucaoJaCancelada;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.AlunoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrutorRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.AlunoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.InstrucaoNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.proximaSegundaAs10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Teste unitário (sem Spring): regras de agendamento e cancelamento com repositórios simulados
class AgendaDeInstrucoesTest {
    InstrucaoRepository instrucaoRepository = mock(InstrucaoRepository.class);
    AlunoRepository alunoRepository = mock(AlunoRepository.class);
    InstrutorRepository instrutorRepository = mock(InstrutorRepository.class);
    ValidadorAgendamento validadorAgendamento = mock(ValidadorAgendamento.class);

    AgendaDeInstrucoes agenda;
    Aluno aluno;
    Instrutor instrutor;

    @BeforeEach
    void setUp() {
        agenda = new AgendaDeInstrucoes(
                instrucaoRepository,
                alunoRepository,
                instrutorRepository,
                new InstrutorEntityMapper(),
                List.of(validadorAgendamento),
                List.of(new ValidadorInstrucaoJaCancelada(), new ValidadorAntecedenciaCancelamento())
        );
        aluno = new Aluno(1L, "Aluno", "(11) 98765-4321", "aluno@email.com.br", "12345678901", dadosEndereco(), true);
        instrutor = new Instrutor(2L, "Instrutor", "(11) 91234-5678", "instrutor@email.com.br", "01234567890",
                Especialidade.MOTOS, dadosEndereco(), true);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrucaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Expectativa: escolher instrutor aleatório da especialidade quando o instrutor não é informado")
    void agendarCenario1() {
        LocalDateTime dataHora = proximaSegundaAs10();
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.MOTOS, dataHora)).thenReturn(instrutor);

        DetalhamentoAgendamento resultado = agenda.agendar(new DadosAgendamento(1L, null, Especialidade.MOTOS, dataHora));

        assertThat(resultado.nomeInstrutor()).isEqualTo("Instrutor");
        assertThat(resultado.nomeAluno()).isEqualTo("Aluno");
        verify(validadorAgendamento).validar(any());
        verify(instrucaoRepository).save(any(Instrucao.class));
    }

    @Test
    @DisplayName("Expectativa: exigir especialidade quando o instrutor não é informado")
    void agendarCenario2() {
        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, null, proximaSegundaAs10())))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Especialidade é obrigatória");
        verify(instrucaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: falhar quando não há instrutor disponível")
    void agendarCenario3() {
        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, Especialidade.VANS, proximaSegundaAs10())))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nenhum instrutor disponível");
    }

    @Test
    @DisplayName("Expectativa: falhar para aluno inexistente sem executar validadores")
    void agendarCenario4() {
        when(alunoRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(9L, null, Especialidade.MOTOS, proximaSegundaAs10())))
                .isInstanceOf(AlunoNotFoundException.class);
        verifyNoInteractions(validadorAgendamento);
    }

    @Test
    @DisplayName("Expectativa: cancelar instrução com mais de 24h de antecedência")
    void cancelarCenario1() {
        Instrucao instrucao = new Instrucao(10L, aluno, mock(InstrutorEntity.class), proximaSegundaAs10());
        when(instrucaoRepository.findById(10L)).thenReturn(Optional.of(instrucao));

        agenda.cancelar(new DadosCancelamento(10L, MotivoCancelamento.ALUNO_DESISTIU));

        assertThat(instrucao.isCancelada()).isTrue();
        assertThat(instrucao.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
    }

    @Test
    @DisplayName("Expectativa: impedir cancelamento com menos de 24h de antecedência")
    void cancelarCenario2() {
        Instrucao instrucao = new Instrucao(10L, aluno, mock(InstrutorEntity.class), LocalDateTime.now().plusHours(5));
        when(instrucaoRepository.findById(10L)).thenReturn(Optional.of(instrucao));

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamento(10L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("24 horas");
        assertThat(instrucao.isCancelada()).isFalse();
    }

    @Test
    @DisplayName("Expectativa: impedir cancelamento de instrução já cancelada")
    void cancelarCenario3() {
        Instrucao instrucao = new Instrucao(10L, aluno, mock(InstrutorEntity.class), proximaSegundaAs10(),
                MotivoCancelamento.INSTRUTOR_CANCELOU);
        when(instrucaoRepository.findById(10L)).thenReturn(Optional.of(instrucao));

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamento(10L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("já foi cancelada");
    }

    @Test
    @DisplayName("Expectativa: falhar ao cancelar instrução inexistente")
    void cancelarCenario4() {
        when(instrucaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamento(99L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(InstrucaoNotFoundException.class);
    }
}
