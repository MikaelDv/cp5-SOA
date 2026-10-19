package br.com.fiap3espv.AutoEscola3ESPV.application.core.domain;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.Endereco;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;

import java.util.Objects;

public class Instrutor {
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cnh;
    private Especialidade especialidade;
    private Endereco endereco;
    private boolean ativo = true;

    public Instrutor() {
    }

    public Instrutor(
            Long id,
            String nome,
            String telefone,
            String email,
            String cnh,
            Especialidade especialidade,
            Endereco endereco,
            boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cnh = cnh;
        this.especialidade = especialidade;
        this.endereco = endereco;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCnh() {
        return cnh;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void atualizarInformacoes(
            String nome,
            String telefone,
            String email,
            Especialidade especialidade,
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
        if(especialidade != null) {
            this.especialidade = especialidade;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instrutor outro)) return false;
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
