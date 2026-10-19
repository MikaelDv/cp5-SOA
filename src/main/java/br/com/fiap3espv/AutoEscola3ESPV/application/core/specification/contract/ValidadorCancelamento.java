package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;

public interface ValidadorCancelamento {
    void validar(Instrucao instrucao);
}
