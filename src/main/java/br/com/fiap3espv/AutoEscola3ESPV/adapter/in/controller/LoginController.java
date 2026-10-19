package br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller;

import br.com.fiap3espv.AutoEscola3ESPV.adapter.in.controller.request.usuario.DadosLogin;
import br.com.fiap3espv.AutoEscola3ESPV.application.core.domain.Usuario;
import br.com.fiap3espv.AutoEscola3ESPV.config.security.DadosTokenJWT;
import br.com.fiap3espv.AutoEscola3ESPV.config.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação", description = "Geração do token JWT")
public class LoginController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    @Operation(summary = "Efetuar login", description = "Retorna o token JWT a ser informado no botão Authorize")
    @ApiResponse(responseCode = "200", description = "Login efetuado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosLogin dados) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        dados.login(),
                        dados.senha()
                );
        Authentication authentication = manager.authenticate(token);
        String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}