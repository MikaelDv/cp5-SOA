package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@Tag(name = "Health check")
public class HealthCheckController {
    @GetMapping
    @Operation(summary = "Verificar se a API está no ar")
    public String healthCheck() {
        return "Verificação de integridade da API Auto Escola 3ESPV ok!";
    }
}