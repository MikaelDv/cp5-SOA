package br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.mapper;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.out.repository.entity.InstrutorEntity;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrutor;
import org.springframework.stereotype.Component;

@Component
public class InstrutorEntityMapper {
    public Instrutor toDomain(InstrutorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Instrutor(
                entity.getId(),
                entity.getNome(),
                entity.getTelefone(),
                entity.getEmail(),
                entity.getCnh(),
                entity.getEspecialidade(),
                entity.getEndereco(),
                entity.isAtivo()
        );
    }

    public InstrutorEntity toEntity(Instrutor domain) {
        if (domain == null) {
            return null;
        }
        return new InstrutorEntity(
                domain.getId(),
                domain.getNome(),
                domain.getTelefone(),
                domain.getEmail(),
                domain.getCnh(),
                domain.getEspecialidade(),
                domain.getEndereco(),
                domain.isAtivo()
        );
    }
}
