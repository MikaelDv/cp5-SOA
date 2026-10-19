package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorInstrutorAtivo implements ValidadorAgendamento {
    private final InstrutorRepository instrutorRepository;

    @Override
    public void validar(DadosAgendamento dados) {
        //Instrutor opcional: será escolhido aleatoriamente entre os ativos
        if (dados.idInstrutor() == null) {
            return;
        }
        boolean instrutorAtivo = instrutorRepository.findAtivoById(dados.idInstrutor());

        if (!instrutorAtivo) {
            throw new ValidacaoException("Instrução não pode ser agendada com instrutor inativo!");
        }
    }
}
