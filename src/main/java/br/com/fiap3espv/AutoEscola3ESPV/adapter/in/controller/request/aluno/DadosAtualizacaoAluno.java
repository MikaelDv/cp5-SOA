package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoAluno(
        @NotNull
        Long id,
        String nome,
        String telefone,

        @Email
        String email,
        DadosEndereco endereco) {
}
