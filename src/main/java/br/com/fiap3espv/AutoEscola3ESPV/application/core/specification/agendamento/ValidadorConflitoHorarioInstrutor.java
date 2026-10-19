package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorConflitoHorarioInstrutor implements ValidadorAgendamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        //Instrutor opcional: a escolha aleatória já considera apenas instrutores livres
        if (dados.idInstrutor() == null) {
            return;
        }
        boolean instrutorOcupado = repository.existsByInstrutorIdAndDataHoraAndMotivoCancelamentoIsNull(
                dados.idInstrutor(),
                dados.dataHora()
        );
        if (instrutorOcupado) {
            throw new ValidacaoException("Instrutor ocupado na data/hora informada!");
        }
    }
}
