package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.cancelamento;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorAntecedenciaCancelamento implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao) {
        long antecedencia = Duration.between(LocalDateTime.now(), instrucao.getDataHora()).toHours();

        if (antecedencia < 24) {
            throw new ValidacaoException("Cancelamento permitido apenas com antecedência mínima de 24 horas!");
        }
    }
}
