package br.com.fiap3espv.AutoEscola3ESPV.domain.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.proximaSegundaAs10;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InstrucaoRepositoryTest {
    @Autowired
    InstrucaoRepository instrucaoRepository;

    @Autowired
    TestEntityManager entityManager;

    Aluno aluno;
    InstrutorEntity instrutor;
    LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        dataHora = proximaSegundaAs10();
        aluno = new Aluno(null, "Aluno Teste", "(11) 98765-4321", "aluno@email.com.br",
                "12345678980", dadosEndereco(), true);
        entityManager.persist(aluno);
        instrutor = new InstrutorEntity(null, "Instrutor Teste", "(11) 91234-5678",
                "instrutor@email.com.br", "01234567890", Especialidade.CARROS, dadosEndereco(), true);
        entityManager.persist(instrutor);
    }

    @Test
    @DisplayName("Expectativa: detectar conflito de horário do instrutor")
    void existsByInstrutorIdAndDataHoraCenario1() {
        entityManager.persist(new Instrucao(null, aluno, instrutor, dataHora));

        boolean ocupado = instrucaoRepository
                .existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(instrutor.getId(), dataHora);

        assertThat(ocupado).isTrue();
    }

    @Test
    @DisplayName("Expectativa: instrução cancelada não gera conflito de horário")
    void existsByInstrutorIdAndDataHoraCenario2() {
        entityManager.persist(new Instrucao(null, aluno, instrutor, dataHora, MotivoCancelamento.OUTROS));

        boolean ocupado = instrucaoRepository
                .existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(instrutor.getId(), dataHora);

        assertThat(ocupado).isFalse();
    }

    @Test
    @DisplayName("Expectativa: detectar segunda instrução do aluno no mesmo dia")
    void existsByAlunoIdAndDataHoraBetween() {
        entityManager.persist(new Instrucao(null, aluno, instrutor, dataHora));

        boolean jaPossuiInstrucao = instrucaoRepository.existsByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
                aluno.getId(),
                dataHora.withHour(6),
                dataHora.withHour(20)
        );
        boolean outroDia = instrucaoRepository.existsByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
                aluno.getId(),
                dataHora.plusDays(1).withHour(6),
                dataHora.plusDays(1).withHour(20)
        );

        assertThat(jaPossuiInstrucao).isTrue();
        assertThat(outroDia).isFalse();
    }

    @Test
    @DisplayName("Expectativa: listagem não traz instruções canceladas")
    void findAllByMotivoCancelamentoIsNull() {
        entityManager.persist(new Instrucao(null, aluno, instrutor, dataHora));
        entityManager.persist(new Instrucao(null, aluno, instrutor, dataHora.plusDays(1), MotivoCancelamento.INSTRUTOR_CANCELOU));

        var pagina = instrucaoRepository.findAllByMotivoCancelamentoIsNull(PageRequest.of(0, 10));

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().getFirst().getDataHora()).isEqualTo(dataHora);
    }
}
