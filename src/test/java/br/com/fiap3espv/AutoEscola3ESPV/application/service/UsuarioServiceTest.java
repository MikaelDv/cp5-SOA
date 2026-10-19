package br.com.fiap3espv.AutoEscola3ESPV.application.service;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Perfil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    UsuarioRepository repository = mock(UsuarioRepository.class);
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    UsuarioService service = new UsuarioService(repository, encoder);

    @Test
    @DisplayName("Expectativa: armazenar a senha com hash BCrypt ao cadastrar")
    void cadastrarUsuarioCenario1() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.cadastrarUsuario(new DadosCadastroUsuario("secretaria", "senha123", Perfil.USER));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isNotEqualTo("senha123");
        assertThat(encoder.matches("senha123", captor.getValue().getSenha())).isTrue();
    }

    @Test
    @DisplayName("Expectativa: impedir login duplicado")
    void cadastrarUsuarioCenario2() {
        when(repository.existsByLogin("admin")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrarUsuario(new DadosCadastroUsuario("admin", "senha123", Perfil.USER)))
                .isInstanceOf(ValidacaoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: atualizar somente os campos informados")
    void atualizarUsuario() {
        Usuario usuario = new Usuario(2L, "secretaria", "hash-antigo", Perfil.USER, true);
        when(repository.findById(2L)).thenReturn(Optional.of(usuario));

        service.atualizarUsuario(new DadosAtualizacaoUsuario(2L, null, null, Perfil.ADMIN));

        assertThat(usuario.getLogin()).isEqualTo("secretaria");
        assertThat(usuario.getSenha()).isEqualTo("hash-antigo");
        assertThat(usuario.getPerfil()).isEqualTo(Perfil.ADMIN);
    }
}
