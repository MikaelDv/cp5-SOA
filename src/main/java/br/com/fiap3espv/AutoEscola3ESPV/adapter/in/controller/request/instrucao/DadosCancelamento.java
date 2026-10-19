package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao;

import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.enumeration.MotivoCancelamento;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public record DadosCancelamento(
        @NotNull
        @JsonAlias(value = "id_instrucao")
        Long idInstrucao,

        @NotNull
        MotivoCancelamento motivo) {
}
