package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.application.service.CepService;
import br.com.fiap3espv.AutoEscola3ESPV.shared.vo.endereco.dto.DadosEndereco;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "CEP", description = "Consulta de endereço no serviço externo ViaCEP")
public class CepController {
    private final CepService service;

    @GetMapping("/{cep}")
    @Operation(summary = "Consultar endereço pelo CEP", description = "Consome a API pública ViaCEP (https://viacep.com.br) para preencher o endereço de alunos e instrutores")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado")
    @ApiResponse(responseCode = "404", description = "CEP inválido ou inexistente")
    @ApiResponse(responseCode = "503", description = "ViaCEP indisponível")
    public ResponseEntity<DadosEndereco> consultarCep(@PathVariable String cep) {
        return ResponseEntity.ok(service.consultarEndereco(cep));
    }
}
