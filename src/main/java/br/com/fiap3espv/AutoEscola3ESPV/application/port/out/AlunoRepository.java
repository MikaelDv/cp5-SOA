package br.com.fiap3espv.AutoEscola3ESPV.application.port.out;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Page<Aluno> findAllByAtivoTrue(Pageable pageable);

    @Query("""
        select e.ativo
        from Aluno e
        where
        e.id = :id
    """)
    boolean findAtivoById(Long id);
}
