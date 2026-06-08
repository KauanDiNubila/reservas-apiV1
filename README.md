# Reservas API

API de gerenciamento de reservas de salas construída com arquitetura de microsserviços usando Spring Boot.

## Arquitetura

Cliente → API Gateway (8080) → auth-service (8084)
→ room-service (8081)
→ booking-service (8082)
→ user-service (8083)

## Serviços

| Serviço | Porta | Descrição |
|---|---|---|
| api-gateway | 8080 | Ponto de entrada, autenticação JWT e roteamento |
| auth-service | 8084 | Login local e OAuth2 com GitHub |
| room-service | 8081 | Gerenciamento de salas |
| booking-service | 8082 | Gerenciamento de reservas |
| user-service | 8083 | Gerenciamento de usuários |

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Cloud Gateway
- Spring Security + JWT
- OAuth2 com GitHub
- Spring Data JPA
- H2 Database
- Docker

## Como rodar localmente

### Pré-requisitos
- Java 21
- Maven
- IntelliJ IDEA

### Configuração

1. Clone o repositório
2. Abra cada serviço no IntelliJ como projeto separado
3. Configure as variáveis de ambiente no IntelliJ:
   **Run → Edit Configurations → Environment Variables**
4. JWT_SECRET=sua_chave_secreta
   GITHUB_CLIENT_ID=seu_client_id
   GITHUB_CLIENT_SECRET=seu_client_secret

#### Ordem de inicialização

Rode cada serviço pelo botão Run do IntelliJ nessa ordem:

1. `room-service` → porta 8081
2. `user-service` → porta 8083
3. `booking-service` → porta 8082
4. `auth-service` → porta 8084
5. `api-gateway` → porta 8080

### Primeiro acesso

Com todos os serviços rodando, registre um usuário admin:

POST http://localhost:8080/auth/register
{
"email": "admin@email.com",
"senha": "123456",
"role": "ADMIN"
}

### Variáveis de ambiente necessárias

```properties
JWT_SECRET=sua_chave_secreta
GITHUB_CLIENT_ID=seu_client_id
GITHUB_CLIENT_SECRET=seu_client_secret
```

## Endpoints principais

### Autenticação

POST /auth/register  → registrar usuário
POST /auth/login     → login local
GET  /auth/github    → login com GitHub

### Salas (requer ADMIN)

GET    /api/v1/rooms       → listar salas
POST   /api/v1/rooms       → criar sala
PUT    /api/v1/rooms/{id}  → atualizar sala
DELETE /api/v1/rooms/{id}  → remover sala

### Reservas (requer USER ou ADMIN)

GET   /api/v1/bookings              → listar reservas
POST  /api/v1/bookings              → criar reserva
PUT   /api/v1/bookings/{id}         → atualizar reserva
PATCH /api/v1/bookings/{id}/cancelar → cancelar reserva

### Usuários

GET    /api/v1/users       → listar usuários
POST   /api/v1/users       → criar usuário
PUT    /api/v1/users/{id}  → atualizar usuário
DELETE /api/v1/users/{id}  → remover usuário

## Regras de negócio

- Reservas não podem ter conflito de horário na mesma sala
- Intervalo semiaberto [início, fim) — fim igual ao início de outra reserva é permitido
- Reservas canceladas não entram na checagem de conflito
- Cancelamento é irreversível
- Salas inativas não podem ser reservadas

## Docker

```bash
docker-compose up --build
```

## Testes

```bash
mvn test
```

Testes de unidade cobrindo regras de negócio críticas:
- Conflito de horário
- Validação de datas
- Cancelamento de reservas
- Autenticação