package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Aluno")
@Table(name = "alunos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;

    @Embedded
    private Endereco endereco;
    private boolean ativo = true;

    public void atualizarInformacoes(
            String nome,
            String telefone,
            String email,
            Endereco endereco) {
        if(nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if(telefone != null && !telefone.isBlank()) {
            this.telefone = telefone;
        }
        if(email != null && !email.isBlank()) {
            this.email = email;
        }
        if(endereco != null) {
            this.endereco.atualizarInformacoes(
                    endereco.getLogradouro(),
                    endereco.getNumero(),
                    endereco.getComplemento(),
                    endereco.getBairro(),
                    endereco.getCidade(),
                    endereco.getUf(),
                    endereco.getCep()
            );
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
