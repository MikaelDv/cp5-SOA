package br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.agendamento;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioFuncionamento implements ValidadorAgendamento {
    @Override
    public void validar(DadosAgendamento dados) {
        LocalDateTime dataHoraInformada = dados.dataHora();

        boolean domingo = dataHoraInformada.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        boolean preAbertura = dataHoraInformada.getHour() < 6;
        boolean posFechamento = dataHoraInformada.getHour() > (21 - 1);

        if (domingo || preAbertura || posFechamento) {
            throw new ValidacaoException("Tentativa de agendamento fora do horário de funcionamento!");
        }
    }
}