package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Instrucao;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.Especialidade;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record DetalhamentoAgendamento(
        Long id,

        @JsonProperty("nome_aluno")
        String nomeAluno,

        @JsonProperty("nome_instrutor")
        String nomeInstrutor,
        Especialidade especialidade,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        @JsonProperty("data_hora")
        LocalDateTime dataHora,

        @JsonProperty("motivo_cancelamento")
        MotivoCancelamento motivoCancelamento) {
    public DetalhamentoAgendamento(Instrucao instrucao) {
        this(
                instrucao.getId(),
                instrucao.getAluno().getNome(),
                instrucao.getInstrutor().getNome(),
                instrucao.getInstrutor().getEspecialidade(),
                instrucao.getDataHora(),
                instrucao.getMotivoCancelamento()
        );
    }
}
