# Zappqui Auth Service

Serviço de autenticação e autorização para a plataforma Zappqui, desenvolvido com Spring Boot 3.4.0 e Java 21. Fornece endpoints para autenticação de usuários, emissão de tokens JWT e validação de tokens.

## 📋 Sobre o Projeto

O **Zappqui Auth Service** é um microserviço responsável por:
- Autenticação de usuários
- Geração de tokens JWT
- Validação de tokens
- Gerenciamento de usuários
- Cache de dados com Caffeine

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.4.0
- **Gerenciador de Dependências**: Gradle
- **Banco de Dados**: PostgreSQL 17
- **Cache**: Redis 7 + Spring Cache com Caffeine
- **Autenticação**: Spring Security + JWT (JJWT 0.12.6)
- **Migração de BD**: Flyway
- **Logging**: Log4j2
- **Documentação da API**: Swagger OpenAPI 2.7.0
- **Monitoramento**: Spring Boot Actuator
- **Containerização**: Docker & Docker Compose

## 📦 Dependências Principais

```gradle
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-security
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-cache
- org.springframework.boot:spring-boot-starter-actuator
- io.jsonwebtoken:jjwt-api:0.12.6
- com.github.ben-manes.caffeine:caffeine:3.1.8
- org.postgresql:postgresql:42.7.3
- org.flywaydb:flyway-core
- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0
```

## 🚀 Como Baixar e Configurar

### Pré-requisitos

- **Java 21** ou superior
- **Gradle** (opcional, pode usar o gradlew incluído)
- **Docker & Docker Compose** (para execução containerizada)
- **PostgreSQL 17** (se rodar localmente sem Docker)
- **Redis 7** (para cache distribuído)

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/zappqui-auth-service.git
cd zappqui-auth-service
```

### 2. Configurar Variáveis de Ambiente (Opcional)

Se rodar localmente, certifique-se de que PostgreSQL e Redis estejam rodando com as seguintes credenciais padrão (em `application.yaml`):

- **Banco de Dados**: 
  - Host: `localhost:5433`
  - Database: `zappqui_auth`
  - User: `zappqui`
  - Password: `zappqui123`

- **Redis**:
  - Host: `localhost`
  - Port: `6379`

## 🏃 Como Rodar o Projeto

### Opção 1: Execução Local (sem Docker)

#### 1.1 Iniciar PostgreSQL e Redis

```bash
# PostgreSQL
docker run -d \
  --name zappqui-auth-db \
  -e POSTGRES_DB=zappqui_auth \
  -e POSTGRES_USER=zappqui \
  -e POSTGRES_PASSWORD=zappqui123 \
  -p 5433:5432 \
  postgres:17-alpine

# Redis
docker run -d \
  --name zappqui-auth-redis \
  -p 6379:6379 \
  redis:7-alpine
```

#### 1.2 Compilar e Executar a Aplicação

```bash
# Usando Gradle Wrapper (sem precisar instalar Gradle)
./gradlew clean build

# Executar a aplicação
./gradlew bootRun
```

Ou:

```bash
# Build JAR
./gradlew clean build

# Executar JAR
java -jar build/libs/zappqui-auth-service-1.0.0-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8081`

### Opção 2: Execução com Docker Compose (Recomendado)

#### 2.1 Iniciar todos os serviços

```bash
docker-compose up -d
```

Este comando inicia:
- **PostgreSQL 17** em `localhost:5433`
- **Redis 7** em `localhost:6379`
- **Aplicação Spring Boot** em `localhost:8081`

#### 2.2 Parar os serviços

```bash
docker-compose down
```

#### 2.3 Ver logs

```bash
docker-compose logs -f app
```

#### 2.4 Recriar containers

```bash
docker-compose up -d --build
```

## 📊 Banco de Dados

### Configuração PostgreSQL

- **Versão**: PostgreSQL 17 Alpine
- **Nome do banco**: `zappqui_auth`
- **Usuário**: `zappqui`
- **Senha**: `zappqui123`
- **Porta**: `5433` (mapeada para 5432 do container)

### Migrações (Flyway)

As migrações SQL estão localizadas em: `src/main/resources/db/migration/`

Exemplo:
- `V1__create_table_user.sql` - Criação da tabela de usuários

O Flyway executa automaticamente na inicialização da aplicação.

### Configuração Hibernate

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # Flyway gerencia o schema
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## 💾 Cache

### Redis + Caffeine (L2 Cache)

A aplicação utiliza dois níveis de cache:

1. **Caffeine** (L1 - In-Memory): Cache local rápido
2. **Redis** (L2 - Distribuído): Cache compartilhado entre instâncias

```yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

### Configuração do Gradle para Cache

```groovy
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'
```

## 🔗 Endpoints da API

### 1. Health Check (Spring Boot Actuator)

**GET** `/actuator/health`

Verifica o status da aplicação e suas dependências.

**Response (200 OK)**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "redis": {
      "status": "UP"
    }
  }
}
```

### 2. Criar Usuário

**POST** `/users`

Cria um novo usuário no sistema.

> ⚠️ **Regras de senha**: mínimo 8 caracteres, incluindo pelo menos 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial (`@$!%*?&`).

**Request Body**:
```json
{
  "username": "joao.silva",
  "password": "Senha@123"
}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "username": "joao.silva"
}
```

**Status Codes**:
- `200 OK` - Usuário criado com sucesso
- `400 Bad Request` - Dados inválidos (username duplicado, senha fraca, campos obrigatórios ausentes)

### 3. Login (Autenticação)

**POST** `/auth/login`

Autentica um usuário e retorna um token JWT.

**Request Body**:
```json
{
  "username": "joao.silva",
  "password": "Senha@123",
  "ttlSeconds": 3600
}
```

> O campo `ttlSeconds` é opcional. Se for `0` ou não informado, o token terá validade padrão de 900 segundos (15 minutos).

**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Status Codes**:
- `200 OK` - Login bem-sucedido
- `401 Unauthorized` - Credenciais inválidas

### 4. Validar Token

**POST** `/auth/validate`

Valida um token JWT e retorna o subject (username).

**Request Body**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response (200 OK) — Token válido**:
```json
{
  "valid": true,
  "subject": "joao.silva"
}
```

**Response (200 OK) — Token inválido/expirado**:
```json
{
  "valid": false,
  "subject": null
}
```

## 📚 Documentação da API (Swagger / OpenAPI)

A API possui documentação interativa completa via **Swagger UI**, gerada automaticamente pelo **SpringDoc OpenAPI 2.7.0**.

### Como acessar

Com a aplicação rodando, acesse:

| Recurso | URL |
|---|---|
| **Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **OpenAPI JSON** | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) |
| **OpenAPI YAML** | [http://localhost:8081/v3/api-docs.yaml](http://localhost:8081/v3/api-docs.yaml) |

### O que está documentado

O Swagger inclui documentação completa de todos os endpoints da API:

- **Autenticação** (`/auth`)
  - `POST /auth/login` — Login e geração de token JWT
  - `POST /auth/validate` — Validação de token JWT
- **Usuários** (`/users`)
  - `POST /users` — Criação de novo usuário

Cada endpoint inclui:
- ✅ Descrição detalhada da operação
- ✅ Exemplos de request/response
- ✅ Schemas dos DTOs com exemplos de valores
- ✅ Códigos de status HTTP documentados
- ✅ Validações de campos obrigatórios

### Testando via Swagger UI

1. Acesse `http://localhost:8081/swagger-ui.html`
2. Expanda o endpoint desejado
3. Clique em **"Try it out"**
4. Preencha os campos do request body
5. Clique em **"Execute"**
6. Veja a resposta diretamente na interface

## 🔐 Segurança

- **Spring Security** para autenticação e autorização
- **JWT (JJWT 0.12.6)** para tokens seguros
- **Senha**: Codificada com BCrypt (padrão do Spring Security)

## 📝 Logs

A aplicação utiliza **Log4j2** para logging estruturado.

Arquivo de configuração: (criar se necessário) `src/main/resources/log4j2.xml`

## 🧪 Testes

Executar testes unitários:

```bash
./gradlew test
```

A aplicação inclui:
- Spring Boot Test
- Spring Security Test
- H2 Database (para testes em memória)

## 📋 Variáveis de Ambiente (Docker Compose)

```yaml
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-auth:5432/zappqui_auth
SPRING_DATASOURCE_USERNAME=zappqui
SPRING_DATASOURCE_PASSWORD=zappqui123
SPRING_REDIS_HOST=redis-auth
SPRING_REDIS_PORT=6379
```

## 🐛 Troubleshooting

### Erro: Conexão recusada no PostgreSQL

```
org.postgresql.util.PSQLException: Connection to localhost:5433 refused
```

**Solução**: Certifique-se de que o PostgreSQL está rodando:

```bash
docker ps | grep zappqui-auth-db
```

### Erro: Redis não disponível

```
redis.clients.jedis.exceptions.JedisConnectionException
```

**Solução**: Reinicie o Redis:

```bash
docker-compose restart redis-auth
```

### Limpeza completa

```bash
docker-compose down -v
docker-compose up -d
```

## 📞 Suporte

Para dúvidas ou issues, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

## 📄 Licença

Este projeto está licenciado sob a MIT License. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido com ❤️ para Zappqui**
