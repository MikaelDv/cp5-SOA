package br.com.fiap3espv.AutoEscola3ESPV.domain.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.AlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static br.com.fiap3espv.AutoEscola3ESPV.support.DadosTeste.dadosEndereco;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AlunoRepositoryTest {
    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Expectativa: retornar true para aluno ativo")
    void findAtivoByIdCenario1() {
        Aluno aluno = cadastrarAluno("Aluno Ativo", "ativo@email.com.br", "12345678901", true);

        assertThat(alunoRepository.findAtivoById(aluno.getId())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: retornar false para aluno excluído (inativo)")
    void findAtivoByIdCenario2() {
        Aluno aluno = cadastrarAluno("Aluno Excluido", "excluido@email.com.br", "12345678902", true);
        aluno.excluir();
        entityManager.flush();

        assertThat(alunoRepository.findAtivoById(aluno.getId())).isFalse();
    }

    @Test
    @DisplayName("Expectativa: listar apenas alunos ativos")
    void findAllByAtivoTrue() {
        cadastrarAluno("Aluno Ativo", "ativo@email.com.br", "12345678901", true);
        cadastrarAluno("Aluno Inativo", "inativo@email.com.br", "12345678902", false);

        var pagina = alunoRepository.findAllByAtivoTrue(PageRequest.of(0, 10));

        assertThat(pagina.getContent())
                .extracting(Aluno::getNome)
                .containsExactly("Aluno Ativo");
    }

    Aluno cadastrarAluno(String nome, String email, String cpf, boolean ativo) {
        Aluno aluno = new Aluno(null, nome, "(11) 98765-4321", email, cpf, dadosEndereco(), ativo);
        entityManager.persist(aluno);
        return aluno;
    }
}
