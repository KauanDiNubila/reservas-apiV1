# Reservas API

API de gerenciamento de reservas de salas construída com arquitetura de microsserviços usando Spring Boot. O sistema permite cadastrar salas, usuários e gerenciar reservas com validação de conflito de horário, autenticação via JWT e login social com GitHub (OAuth2).

## Arquitetura

```
                          ┌─────────────────┐
   Interface Web  ──────► │  API Gateway    │ (8080)
   (frontend)             │  JWT + Roteamento│
                          └────────┬────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │                        │                         │
   ┌──────▼──────┐  ┌──────────────▼──┐  ┌─────────────┐  ┌──▼───────────┐
   │ auth-service│  │  room-service   │  │user-service │  │booking-service│
   │   (8084)    │  │    (8081)       │  │   (8083)    │  │   (8082)     │
   └──────┬──────┘  └────────┬────────┘  └──────┬──────┘  └──────┬───────┘
          │                  │                  │                │
     ┌────▼────┐       ┌─────▼────┐       ┌─────▼────┐     ┌─────▼─────┐
     │ auth_db │       │ room_db  │       │ user_db  │     │booking_db │
     └─────────┘       └──────────┘       └──────────┘     └───────────┘
                            PostgreSQL (Docker)
```

Cada microsserviço possui seu próprio banco de dados (database-per-service), seguindo o padrão de isolamento de dados em arquiteturas de microsserviços.

## Serviços

| Serviço         | Porta | Descrição                                       |
| --------------- | ----- | ----------------------------------------------- |
| api-gateway     | 8080  | Ponto de entrada, autenticação JWT e roteamento |
| auth-service    | 8084  | Login local e OAuth2 com GitHub                 |
| room-service    | 8081  | Gerenciamento de salas                          |
| booking-service | 8082  | Gerenciamento de reservas                       |
| user-service    | 8083  | Gerenciamento de usuários                       |

## Tecnologias

- Java 21
- Spring Boot 3.4
- Spring Cloud Gateway (WebFlux)
- Spring Security + JWT
- OAuth2 com GitHub
- Spring Data JPA
- PostgreSQL
- Flyway (migrations de banco)
- Swagger / OpenAPI (springdoc)
- Maven
- Docker
- JUnit 5 + Mockito (testes)

## Pré-requisitos

- Docker e Docker Compose
- (Opcional, para rodar sem Docker) Java 21 e Maven

## Como rodar com Docker (recomendado)

A aplicação inteira — banco de dados e todos os microsserviços — sobe com um único comando.

### 1. Configurar as variáveis de ambiente

Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp .env.example .env
```

Edite o `.env`. Os valores de banco e JWT já funcionam para desenvolvimento; o login via GitHub requer credenciais reais de um OAuth App (criado em **GitHub → Settings → Developer settings → OAuth Apps**, com callback `http://localhost:8080/auth/github/callback`).

### 2. Subir tudo

```bash
docker-compose up --build
```

Esse comando sobe o PostgreSQL (criando os quatro bancos automaticamente), compila e sobe os cinco microsserviços. As tabelas são criadas pelo Flyway na primeira execução de cada serviço.

Para parar:

```bash
docker-compose down
```

(adicione `-v` para também apagar os dados do banco)

## Como rodar localmente (sem Docker)

<details>
<summary>Clique para expandir</summary>

### 1. Subir o banco de dados (PostgreSQL no Docker)

```bash
docker run --name postgres-reservas -e POSTGRES_USER=reservas -e POSTGRES_PASSWORD=reservas123 -e POSTGRES_DB=reservas -e POSTGRES_HOST_AUTH_METHOD=md5 -p 5432:5432 -d postgres
```

### 2. Criar os bancos de cada serviço

```bash
docker exec -it postgres-reservas psql -U reservas -d reservas -c "CREATE DATABASE auth_db;"
docker exec -it postgres-reservas psql -U reservas -d reservas -c "CREATE DATABASE room_db;"
docker exec -it postgres-reservas psql -U reservas -d reservas -c "CREATE DATABASE booking_db;"
docker exec -it postgres-reservas psql -U reservas -d reservas -c "CREATE DATABASE user_db;"
```

### 3. Configurar as variáveis de ambiente

No IntelliJ, em **Run → Edit Configurations → Environment Variables** do `auth-service`:

```
JWT_SECRET=sua_chave_secreta_de_pelo_menos_64_caracteres
GITHUB_CLIENT_ID=seu_client_id
GITHUB_CLIENT_SECRET=seu_client_secret
```

### 4. Subir os serviços

Rode cada serviço pelo botão Run do IntelliJ:

1. `room-service` → porta 8081
2. `user-service` → porta 8083
3. `booking-service` → porta 8082
4. `auth-service` → porta 8084
5. `api-gateway` → porta 8080

</details>

## Primeiro acesso

Com todos os serviços rodando, registre um usuário admin:

```
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "email": "admin@email.com",
  "senha": "123456",
  "role": "ADMIN"
}
```

Depois faça login para obter o token JWT:

```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "admin@email.com",
  "senha": "123456"
}
```

Use o token retornado no header `Authorization: Bearer <token>` nas demais requisições.

## Interface Web de demonstração

O projeto inclui uma interface web (HTML, CSS e JavaScript puro) na pasta `frontend/`, criada para demonstrar e testar o sistema funcionando de ponta a ponta sem precisar de ferramentas externas como Postman.

A interface consome a API através do gateway e permite:

- Fazer login (autenticação JWT)
- Cadastrar e listar salas
- Cadastrar e listar usuários
- Criar reservas, com tratamento visual do conflito de horário

**Como usar:** com a aplicação rodando (via Docker ou local), abra o arquivo `frontend/index.html` diretamente no navegador. A interface se conecta automaticamente ao gateway em `http://localhost:8080`.

## Documentação da API (Swagger)

Cada serviço expõe sua documentação interativa via Swagger UI:

| Serviço         | URL do Swagger                              |
| --------------- | ------------------------------------------- |
| auth-service    | http://localhost:8084/swagger-ui.html       |
| room-service    | http://localhost:8081/swagger-ui.html       |
| booking-service | http://localhost:8082/swagger-ui.html       |
| user-service    | http://localhost:8083/swagger-ui.html       |

## Endpoints principais

### Autenticação

```
POST /auth/register        → registrar usuário
POST /auth/login           → login local
GET  /auth/github          → login com GitHub
```

### Salas (requer ADMIN)

```
GET    /api/v1/rooms        → listar salas
POST   /api/v1/rooms        → criar sala
PUT    /api/v1/rooms/{id}   → atualizar sala
DELETE /api/v1/rooms/{id}   → remover sala
```

### Reservas (requer USER ou ADMIN)

```
GET   /api/v1/bookings              → listar reservas
POST  /api/v1/bookings              → criar reserva
PUT   /api/v1/bookings/{id}         → atualizar reserva
PATCH /api/v1/bookings/{id}/cancelar → cancelar reserva
```

### Usuários

```
GET    /api/v1/users        → listar usuários
POST   /api/v1/users        → criar usuário
PUT    /api/v1/users/{id}   → atualizar usuário
DELETE /api/v1/users/{id}   → remover usuário
```

## Regras de negócio

- Reservas não podem ter conflito de horário na mesma sala
- Intervalo semiaberto `[início, fim)` — o fim de uma reserva pode coincidir com o início de outra
- Reservas canceladas não entram na checagem de conflito
- Cancelamento é irreversível
- Salas inativas não podem ser reservadas

## Testes

```bash
mvn test
```

Testes de unidade cobrindo regras de negócio críticas:

- Conflito de horário
- Validação de datas
- Cancelamento de reservas
- Autenticação

## Segurança

- Autenticação stateless via JWT, validada centralmente no gateway
- Autorização baseada em papéis (ADMIN / USER)
- Credenciais sensíveis (JWT secret, OAuth do GitHub) são lidas de variáveis de ambiente, nunca versionadas no código