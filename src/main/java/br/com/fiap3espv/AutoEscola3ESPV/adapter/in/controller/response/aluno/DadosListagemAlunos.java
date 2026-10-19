package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;

public record DadosListagemAlunos(
        Long id,
        String nome,
        String email,
        String cpf) {
    public DadosListagemAlunos(Aluno aluno) {
        this(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf());
    }
}
