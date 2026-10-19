package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataHoraInformada = dados.dataHora();
        LocalDateTime agora = LocalDateTime.now();

        long antecedencia = Duration.between(agora, dataHoraInformada).toMinutes();

        if (antecedencia < 30) {
            throw new ValidacaoException("Necessária antecedência mínima de 30 min. para agendamento!");
        }
    }
}