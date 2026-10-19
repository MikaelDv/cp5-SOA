package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Perfil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoUsuario(
        @NotNull
        Long id,
        String login,

        @Size(min = 6)
        String senha,
        Perfil perfil) {
}
