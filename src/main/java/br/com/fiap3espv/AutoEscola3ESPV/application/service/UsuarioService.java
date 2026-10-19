package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.UsuarioNotFoundException;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario dados) {
        validarLoginDisponivel(dados.login());
        Usuario usuario = new Usuario(
                null,
                dados.login(),
                encoder.encode(dados.senha()),
                dados.perfil(),
                true
        );
        return new DadosDetalhamentoUsuario(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Page<DadosDetalhamentoUsuario> listarUsuarios(Pageable pageable) {
        return repository
                .findAllByAtivoTrue(pageable)
                .map(DadosDetalhamentoUsuario::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoUsuario detalharUsuario(Long id) {
        return new DadosDetalhamentoUsuario(buscarUsuario(id));
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarUsuario(DadosAtualizacaoUsuario dados) {
        Usuario usuario = buscarUsuario(dados.id());
        if (dados.login() != null && !dados.login().equals(usuario.getLogin())) {
            validarLoginDisponivel(dados.login());
        }
        usuario.atualizarInformacoes(
                dados.login(),
                dados.senha() != null ? encoder.encode(dados.senha()) : null,
                dados.perfil()
        );
        return new DadosDetalhamentoUsuario(usuario);
    }

    @Transactional
    public void excluirUsuario(Long id) {
        buscarUsuario(id).excluir();
    }

    private void validarLoginDisponivel(String login) {
        if (repository.existsByLogin(login)) {
            throw new ValidacaoException("Login informado já está em uso!");
        }
    }

    private Usuario buscarUsuario(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        new UsuarioNotFoundException("ID do usuário informado não existe!"));
    }
}
