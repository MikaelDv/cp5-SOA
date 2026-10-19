package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.instrutor.DadosListagemInstrutores;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.InstrutorService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instrutor")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Instrutores", description = "CRUD de instrutores")
public class InstrutorController {
    private final InstrutorService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar instrutor (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Instrutor cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "403", description = "Usuário sem perfil ADMIN")
    public ResponseEntity<DadosDetalhamentoInstrutor> cadastrarInstrutor(
            @RequestBody @Valid DadosCadastroInstrutor dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoInstrutor dto = service.cadastrarInstrutor(dados);
        URI uri = uriBuilder.path("/instrutor/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar instrutores ativos")
    public ResponseEntity<Page<DadosListagemInstrutores>> listarInstrutores(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarInstrutores(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar instrutor")
    @ApiResponse(responseCode = "404", description = "Instrutor inexistente")
    public ResponseEntity<DadosDetalhamentoInstrutor> detalharInstrutor(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharInstrutor(id));
    }

    @PutMapping
    @Operation(summary = "Atualizar instrutor")
    @ApiResponse(responseCode = "404", description = "Instrutor inexistente")
    public ResponseEntity<DadosDetalhamentoInstrutor> atualizarInstrutor(@RequestBody @Valid DadosAtualizacaoInstrutor dados) {
        return ResponseEntity.ok(service.atualizarInstrutor(dados));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir (inativar) instrutor")
    @ApiResponse(responseCode = "204", description = "Instrutor inativado")
    @ApiResponse(responseCode = "404", description = "Instrutor inexistente")
    public ResponseEntity<Void> excluirInstrutor(@PathVariable Long id) {
        service.excluirInstrutor(id);
        return ResponseEntity.noContent().build();
    }
}
