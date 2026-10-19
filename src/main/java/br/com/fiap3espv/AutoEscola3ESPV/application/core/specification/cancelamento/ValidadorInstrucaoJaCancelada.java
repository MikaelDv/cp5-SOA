package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.cancelamento;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstrucaoJaCancelada implements ValidadorCancelamento {
    @Override
    public void validar(Instrucao instrucao) {
        if (instrucao.isCancelada()) {
            throw new ValidacaoException("Instrução informada já foi cancelada!");
        }
    }
}
