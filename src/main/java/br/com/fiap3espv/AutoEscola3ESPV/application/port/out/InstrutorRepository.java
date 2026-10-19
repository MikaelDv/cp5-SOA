package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InstrutorRepository {
    Page<Instrutor> findAllByAtivoTrue(Pageable pageable);

    Instrutor escolherInstrutorAleatorioDisponivel(Especialidade especialidade, LocalDateTime dataHora);

    boolean findAtivoById(Long id);

    boolean existsById(Long id);

    Optional<Instrutor> findById(Long id);

    Instrutor save(Instrutor instrutor);
}
