package br.com.fiap3espv.AutoEscola3ESPV.domain.instrutor;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.InstrutorRepositoryImpl;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrutorRepository;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.proximaSegundaAs10;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({InstrutorRepositoryImpl.class, InstrutorEntityMapper.class})
public class InstrutorRepositoryTest {
    @Autowired
    InstrutorRepository instrutorRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Expectativa: retornar null quanto instrutor cadastrado não está disponível")
    void escolherInstrutorAleatorioDisponivelCenario1() {
        LocalDateTime proximaSegundaAs10 = proximaSegundaAs10();

        Aluno aluno = cadastrarAluno(
                "Aluno Teste",
                "(11) 98765-4321",
                "aluno@email.com.br",
                "12345678980"
        );

        InstrutorEntity instrutor = cadastrarInstrutor(
                "Instrutor Teste",
                "(11) 91234-5678",
                "instrutor@email.com.br",
                "01234567890",
                Especialidade.CAMINHOES,
                true
        );

        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, null);

        //when or act
        Instrutor instrutorDisponivel = instrutorRepository.escolherInstrutorAleatorioDisponivel(
                Especialidade.CAMINHOES,
                proximaSegundaAs10
        );
        //then or assert
        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: retornar instrutor quando ele está disponivel")
    void escolherInstrutorAleatorioDisponivelCenario2() {
        InstrutorEntity instrutor = cadastrarInstrutor(
                "Instrutor Teste",
                "(11) 91234-5678",
                "instrutor@email.com.br",
                "01234567890",
                Especialidade.CAMINHOES,
                true
        );

        //when or act
        Instrutor instrutorDisponivel = instrutorRepository.escolherInstrutorAleatorioDisponivel(
                Especialidade.CAMINHOES,
                proximaSegundaAs10()
        );
        //then or assert
        assertThat(instrutorDisponivel).isNotNull();
        assertThat(instrutorDisponivel.getId()).isEqualTo(instrutor.getId());
    }

    @Test
    @DisplayName("Expectativa: instrutor volta a ficar disponível quando a instrução é cancelada")
    void escolherInstrutorAleatorioDisponivelCenario3() {
        LocalDateTime proximaSegundaAs10 = proximaSegundaAs10();
        Aluno aluno = cadastrarAluno("Aluno Teste", "(11) 98765-4321", "aluno@email.com.br", "12345678980");
        InstrutorEntity instrutor = cadastrarInstrutor(
                "Instrutor Teste", "(11) 91234-5678", "instrutor@email.com.br", "01234567890",
                Especialidade.CAMINHOES, true);
        agendarInstrucao(aluno, instrutor, proximaSegundaAs10, MotivoCancelamento.ALUNO_DESISTIU);

        Instrutor instrutorDisponivel = instrutorRepository.escolherInstrutorAleatorioDisponivel(
                Especialidade.CAMINHOES,
                proximaSegundaAs10
        );

        assertThat(instrutorDisponivel).isNotNull();
    }

    @Test
    @DisplayName("Expectativa: não escolher instrutor inativo nem de outra especialidade")
    void escolherInstrutorAleatorioDisponivelCenario4() {
        cadastrarInstrutor("Inativo", "(11) 91234-5678", "inativo@email.com.br", "01234567890",
                Especialidade.MOTOS, false);
        cadastrarInstrutor("Carros", "(11) 91234-5679", "carros@email.com.br", "01234567891",
                Especialidade.CARROS, true);

        Instrutor instrutorDisponivel = instrutorRepository.escolherInstrutorAleatorioDisponivel(
                Especialidade.MOTOS,
                proximaSegundaAs10()
        );

        assertThat(instrutorDisponivel).isNull();
    }

    @Test
    @DisplayName("Expectativa: listar apenas instrutores ativos")
    void findAllByAtivoTrue() {
        cadastrarInstrutor("Ativo", "(11) 91234-5678", "ativo@email.com.br", "01234567890",
                Especialidade.MOTOS, true);
        cadastrarInstrutor("Inativo", "(11) 91234-5679", "inativo@email.com.br", "01234567891",
                Especialidade.MOTOS, false);

        var pagina = instrutorRepository.findAllByAtivoTrue(PageRequest.of(0, 10));

        assertThat(pagina.getContent())
                .extracting(Instrutor::getNome)
                .containsExactly("Ativo");
    }

    @Test
    @DisplayName("Expectativa: salvar instrutor do domínio e consultar se está ativo")
    void saveEFindAtivoById() {
        Instrutor salvo = instrutorRepository.save(new Instrutor(
                null, "Novo", "(11) 90000-0000", "novo@email.com.br", "11122233344",
                Especialidade.VANS, dadosEndereco(), true));

        assertThat(salvo.getId()).isNotNull();
        assertThat(instrutorRepository.existsById(salvo.getId())).isTrue();
        assertThat(instrutorRepository.findAtivoById(salvo.getId())).isTrue();
    }

    //Método para cadastrar aluno
    Aluno cadastrarAluno(String nome, String telefone, String email, String cpf) {
        Aluno aluno = new Aluno(
                null,
                nome,
                telefone,
                email,
                cpf,
                dadosEndereco(),
                true
        );
        entityManager.persist(aluno);
        return aluno;
    }

    //Método para cadastrar instrutor
    InstrutorEntity cadastrarInstrutor(String nome, String telefone, String email, String cnh, Especialidade especialidade, boolean ativo) {
        InstrutorEntity instrutor = new InstrutorEntity(
                null,
                nome,
                telefone,
                email,
                cnh,
                especialidade,
                dadosEndereco(),
                ativo
        );
        entityManager.persist(instrutor);
        return instrutor;
    }

    //Método para agendar instrução
    void agendarInstrucao(Aluno aluno, InstrutorEntity instrutor, LocalDateTime dataHora, MotivoCancelamento motivo) {
        Instrucao instrucao = new Instrucao(
                null,
                aluno,
                instrutor,
                dataHora,
                motivo
        );
        entityManager.persist(instrucao);
    }
}
