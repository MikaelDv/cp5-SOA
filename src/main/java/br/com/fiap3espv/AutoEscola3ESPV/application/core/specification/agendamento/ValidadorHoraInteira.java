package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidadorHoraInteira implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataHoraInformada = dados.dataHora();

        if(dataHoraInformada.getMinute() != 0) {
            throw new ValidacaoException("O horário deve ser preenchido em horas inteiras (ex: 09:00, 13:00, ...)");
        }
    }
}