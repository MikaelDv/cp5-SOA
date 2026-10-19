# Auto Escola 3ESPV — API ReST de agendamento de instruções

Checkpoint 5 — **SOA e WebServices** (Prof. Carlos Eduardo Machado de Oliveira)

## Integrantes

| Nome | RM |
|------|----|
| Eric Darakjian | RM557082 |
| Kauã Soares Guimarães | RM559044 |
| Pietro Vitor Pezzente | RM557283 |
| Enzo Mikael Sanches | RM558887 |

## Tecnologias

- Java 21 (compatível com Java 17+ exigido) e Spring Boot 4
- Spring Web MVC, Spring Data JPA, Bean Validation, Flyway e MySQL
- Spring Security com JWT (`com.auth0:java-jwt`)
- SpringDoc OpenAPI (Swagger UI)
- `RestClient` para consumir a API externa ViaCEP
- JUnit 5, Mockito, MockMvc, `MockRestServiceServer` e H2 nos testes

## O que a API entrega

| Requisito | Onde está |
|-----------|-----------|
| CRUD de Instrutores | `GET/POST/PUT /instrutor`, `GET/DELETE /instrutor/{id}` |
| CRUD de Alunos | `GET/POST/PUT /aluno`, `GET/DELETE /aluno/{id}` |
| CRUD de Usuários (somente ADMIN) | `GET/POST/PUT /usuario`, `GET/DELETE /usuario/{id}` |
| Instruções: agendar, listar, detalhar, cancelar | `POST /instrucao`, `GET /instrucao`, `GET /instrucao/{id}`, `DELETE /instrucao` |
| **Consumo de API externa** | `GET /cep/{cep}` → `ViaCepClient` (`adapter/out/client/viacep`) consome `https://viacep.com.br` |
| **Documentação automática (Swagger)** | `http://localhost:8080/swagger-ui.html` e `http://localhost:8080/v3/api-docs` (cópia offline em `SwaggerOffLine.yaml`) |
| **CORS** | `config/webmvc/CorsConfig.java`, origens configuráveis em `api.security.allowed-origins` |
| **Testes automatizados** | `src/test/java` — 66 testes cobrindo cada entidade (veja abaixo) |

As exclusões de aluno, instrutor e usuário são lógicas (o registro fica com `ativo = false`).

### Regras de negócio do agendamento

- O horário de funcionamento é de segunda a sábado, das 06:00 às 21:00. Cada instrução dura 1 hora e começa em hora cheia.
- A instrução precisa ser agendada com pelo menos 30 minutos de antecedência.
- Cada aluno pode ter no máximo uma instrução por dia.
- Alunos e instrutores inativos não podem ser agendados.
- Um instrutor não pode ter duas instruções no mesmo horário.
- O instrutor é opcional. Se ele não for informado, a API escolhe aleatoriamente um instrutor ativo e livre da especialidade pedida.

### Regras de negócio do cancelamento

- É obrigatório informar o motivo: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`.
- O cancelamento precisa ser feito com pelo menos 24 horas de antecedência.
- Uma instrução já cancelada não pode ser cancelada de novo. Depois do cancelamento, o horário fica livre para outro agendamento.

Cada regra é uma classe separada em `application/core/specification` (`ValidadorAgendamento` e `ValidadorCancelamento`). Para criar uma regra nova, basta criar um novo `@Component`.

## Como executar

Pré-requisitos: JDK 17 ou superior (o projeto está configurado para 21) e MySQL rodando em `localhost:3306`.

```bash
# variáveis opcionais (valores padrão entre parênteses)
export DB_URL=jdbc:mysql://localhost:3306/autoescola3espv   # (cria o banco se não existir)
export DB_USERNAME=root                                     # (root)
export DB_PASSWORD=fiap                                     # (fiap)
export JWT_SECRET=um-segredo-forte                          # (12345678)
export CORS_ALLOWED_ORIGINS=http://localhost:5500,http://localhost:3000

./mvnw spring-boot:run
```

O Flyway cria as tabelas e o usuário administrador padrão. **Login:** `admin`, **senha:** `admin123`. Troque essa senha depois do primeiro acesso usando `PUT /usuario`.

### Usando pelo Swagger

1. Abra `http://localhost:8080/swagger-ui.html`.
2. Execute `POST /login` com `{"login": "admin", "senha": "admin123"}` e copie o `tokenJWT` da resposta.
3. Clique em **Authorize**, cole o token e use os demais endpoints.

### Exemplos de requisição

```http
POST /instrucao
Authorization: Bearer <token>
Content-Type: application/json

{ "id_aluno": 1, "especialidade": "CARROS", "data_hora": "26/10/2026 - 10:00" }
```

```http
DELETE /instrucao
Authorization: Bearer <token>
Content-Type: application/json

{ "id_instrucao": 1, "motivo": "ALUNO_DESISTIU" }
```

```http
GET /cep/01001000
Authorization: Bearer <token>
```

## Testes automatizados

```bash
./mvnw test
```

Os testes usam um banco **H2 em memória** (perfil `test`, `src/test/resources/application-test.properties`) com as mesmas migrations do Flyway. O ViaCEP é simulado, então não é preciso ter MySQL nem internet para rodar os testes.

| Entidade / recurso | Testes |
|--------------------|--------|
| Instrutor | `InstrutorControllerTest` e `InstrutorRepositoryTest` (escolha aleatória de instrutor disponível, inativos, cancelamentos) |
| Aluno | `AlunoControllerTest` e `AlunoRepositoryTest` |
| Usuário | `UsuarioControllerTest` (permissões ADMIN/USER), `UsuarioRepositoryTest` e `UsuarioServiceTest` (hash BCrypt) |
| Instrução | `InstrucaoControllerTest`, `InstrucaoRepositoryTest` e `AgendaDeInstrucoesTest` (regras de agendamento e cancelamento) |
| Login / JWT | `LoginControllerTest` (integração completa: login, token e acesso a um endpoint protegido) |
| API externa | `ViaCepClientTest` (`MockRestServiceServer`) e `CepControllerTest` |
| CORS e Swagger | `CorsAndSwaggerConfigTest` (preflight de origem permitida e de origem bloqueada, `/v3/api-docs` e Swagger UI) |

## Estrutura (arquitetura hexagonal)

```
adapter/in/controller     -> controllers REST, DTOs de request/response
adapter/out/repository    -> persistência JPA de Instrutor (entity, mapper, impl)
adapter/out/client        -> cliente HTTP do ViaCEP
adapter/out/security      -> UserDetailsService
application/core          -> domínio e regras de negócio (specifications)
application/port/out      -> portas de saída (repositórios, consulta de CEP)
application/service       -> casos de uso
config                    -> segurança/JWT, CORS e SpringDoc
```
