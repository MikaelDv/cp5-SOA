package br.com.fiap3espv.AutoEscola3ESPV.domain.usuario;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.UsuarioRepository;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Perfil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Expectativa: migration cria o usuário admin com perfil ADMIN")
    void findByLoginCenario1() {
        UserDetails admin = usuarioRepository.findByLogin("admin");

        assertThat(admin).isNotNull();
        assertThat(admin.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("Expectativa: retornar null para login inexistente")
    void findByLoginCenario2() {
        assertThat(usuarioRepository.findByLogin("nao.existe")).isNull();
    }

    @Test
    @DisplayName("Expectativa: usuário comum possui apenas ROLE_USER e fica desabilitado ao ser excluído")
    void perfilUsuarioComum() {
        Usuario usuario = new Usuario(null, "comum", "hash", Perfil.USER, true);
        entityManager.persist(usuario);
        usuario.excluir();
        entityManager.flush();

        UserDetails encontrado = usuarioRepository.findByLogin("comum");

        assertThat(usuarioRepository.existsByLogin("comum")).isTrue();
        assertThat(encontrado.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
        assertThat(encontrado.isEnabled()).isFalse();
    }
}
