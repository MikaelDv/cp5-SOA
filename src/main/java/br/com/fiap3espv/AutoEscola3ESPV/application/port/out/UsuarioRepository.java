package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByLogin(String username);

    boolean existsByLogin(String login);

    Page<Usuario> findAllByAtivoTrue(Pageable pageable);
}
