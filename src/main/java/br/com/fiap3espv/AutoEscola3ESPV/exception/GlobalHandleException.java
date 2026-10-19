package br.com.fiap3espv.AutoEscola3ESPV.exception;

import br.com.fiap3espv.AutoEscola3ESPV.exception.type.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalHandleException {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({
            AlunoNotFoundException.class,
            InstrutorNotFoundException.class,
            InstrucaoNotFoundException.class,
            UsuarioNotFoundException.class,
            CepNotFoundException.class
    })
    public ResponseEntity<DadosErro> handleRecursoNaoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>> handleBadRequestException(MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosBadRequest::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErro> handleCorpoInvalido() {
        return ResponseEntity.badRequest().body(new DadosErro("Corpo da requisição ausente ou mal formatado!"));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErro> handleRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest().body(new DadosErro(ex.getMessage()));
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<DadosErro> handleCredenciaisInvalidas() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DadosErro("Login e/ou senha inválidos!"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DadosErro> handleFalhaAutenticacao() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DadosErro("Falha na autenticação!"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DadosErro> handleAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DadosErro("Acesso negado!"));
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<DadosErro> handleServicoExterno(ServicoExternoException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new DadosErro(ex.getMessage()));
    }

    private record DadosBadRequest(String field, String message) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    private record DadosErro(String mensagem) {
    }
}
