package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.port.out.InstrucaoRepository;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime inicioExpediente = dados.dataHora().withHour(6).withMinute(0);
        LocalDateTime fimExpediente = dados.dataHora().withHour(21 - 1).withMinute(0);
        boolean alunoReincidenciaDiaria = repository.existsByAlunoIdAndDataHoraBetweenAndMotivoCancelamentoIsNull(
                dados.idAluno(),
                inicioExpediente,
                fimExpediente
        );
        if (alunoReincidenciaDiaria) {
            throw new ValidacaoException("Permitida apenas uma instrução diária por aluno!");
        }
    }
}