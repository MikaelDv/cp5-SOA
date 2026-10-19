package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.aluno.DadosListagemAlunos;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.AlunoService;
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
@RequestMapping("/aluno")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Alunos", description = "CRUD de alunos")
public class AlunoController {
    private final AlunoService service;

    @PostMapping
    @Operation(summary = "Cadastrar aluno")
    @ApiResponse(responseCode = "201", description = "Aluno cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<DadosDetalhamentoAluno> cadastrarAluno(
            @RequestBody @Valid DadosCadastroAluno dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoAluno dto = service.cadastrarAluno(dados);
        URI uri = uriBuilder.path("/aluno/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar alunos ativos")
    public ResponseEntity<Page<DadosListagemAlunos>> listarAlunos(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarAlunos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar aluno")
    @ApiResponse(responseCode = "404", description = "Aluno inexistente")
    public ResponseEntity<DadosDetalhamentoAluno> detalharAluno(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharAluno(id));
    }

    @PutMapping
    @Operation(summary = "Atualizar aluno")
    @ApiResponse(responseCode = "404", description = "Aluno inexistente")
    public ResponseEntity<DadosDetalhamentoAluno> atualizarAluno(@RequestBody @Valid DadosAtualizacaoAluno dados) {
        return ResponseEntity.ok(service.atualizarAluno(dados));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir (inativar) aluno")
    @ApiResponse(responseCode = "204", description = "Aluno inativado")
    @ApiResponse(responseCode = "404", description = "Aluno inexistente")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {
        service.excluirAluno(id);
        return ResponseEntity.noContent().build();
    }
}
