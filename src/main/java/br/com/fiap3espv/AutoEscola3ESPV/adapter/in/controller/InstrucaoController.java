package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosCancelamento;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AgendaDeInstrucoes;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrucao.DetalhamentoAgendamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instrucao")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Instruções", description = "Agendamento e cancelamento de instruções")
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;

    @PostMapping
    @Operation(summary = "Agendar instrução", description = "Se o instrutor não for informado, um instrutor ativo e disponível da especialidade é escolhido aleatoriamente")
    @ApiResponse(responseCode = "201", description = "Instrução agendada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada")
    @ApiResponse(responseCode = "404", description = "Aluno ou instrutor inexistente")
    public ResponseEntity<DetalhamentoAgendamento> agendarInstrucao(
            @RequestBody @Valid DadosAgendamento dados,
            UriComponentsBuilder uriBuilder) {
        DetalhamentoAgendamento dto = agenda.agendar(dados);
        URI uri = uriBuilder.path("/instrucao/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar instruções agendadas (não canceladas)")
    public ResponseEntity<Page<DetalhamentoAgendamento>> listarInstrucoes(
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(agenda.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar instrução")
    @ApiResponse(responseCode = "404", description = "Instrução inexistente")
    public ResponseEntity<DetalhamentoAgendamento> detalharInstrucao(@PathVariable Long id) {
        return ResponseEntity.ok(agenda.detalhar(id));
    }

    @DeleteMapping
    @Operation(summary = "Cancelar instrução", description = "Exige motivo e antecedência mínima de 24 horas")
    @ApiResponse(responseCode = "204", description = "Instrução cancelada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada")
    @ApiResponse(responseCode = "404", description = "Instrução inexistente")
    public ResponseEntity<Void> cancelarInstrucao(@RequestBody @Valid DadosCancelamento dados) {
        agenda.cancelar(dados);
        return ResponseEntity.noContent().build();
    }
}
