package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosAtualizacaoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosCadastroUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.response.usuario.DadosDetalhamentoUsuario;
import br.com.fiap3espv.AutoEscola3ESPV.application.service.UsuarioService;
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
@RequestMapping("/usuario")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Usuários", description = "CRUD de usuários da API (somente ADMIN)")
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    @Operation(summary = "Cadastrar usuário")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou login já em uso")
    public ResponseEntity<DadosDetalhamentoUsuario> cadastrarUsuario(
            @RequestBody @Valid DadosCadastroUsuario dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoUsuario dto = service.cadastrarUsuario(dados);
        URI uri = uriBuilder.path("/usuario/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar usuários ativos")
    public ResponseEntity<Page<DadosDetalhamentoUsuario>> listarUsuarios(
            @ParameterObject @PageableDefault(size = 10, sort = "login") Pageable pageable) {
        return ResponseEntity.ok(service.listarUsuarios(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar usuário")
    @ApiResponse(responseCode = "404", description = "Usuário inexistente")
    public ResponseEntity<DadosDetalhamentoUsuario> detalharUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalharUsuario(id));
    }

    @PutMapping
    @Operation(summary = "Atualizar usuário")
    @ApiResponse(responseCode = "404", description = "Usuário inexistente")
    public ResponseEntity<DadosDetalhamentoUsuario> atualizarUsuario(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        return ResponseEntity.ok(service.atualizarUsuario(dados));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir (inativar) usuário")
    @ApiResponse(responseCode = "204", description = "Usuário inativado")
    @ApiResponse(responseCode = "404", description = "Usuário inexistente")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        service.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
