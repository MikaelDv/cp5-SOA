package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Perfil;

public record DadosDetalhamentoUsuario(
        Long id,
        String login,
        Perfil perfil,
        boolean ativo) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin(), usuario.getPerfil(), usuario.isAtivo());
    }
}
