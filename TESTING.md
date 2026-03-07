# 📋 Guia Completo de Testes - Zappqui Auth Service

Este documento descreve todos os testes de unidade e integração criados para o projeto Zappqui Auth Service.

## 📁 Estrutura de Testes

```
src/test/
├── java/com/zappqui/api/auth/zappqui_auth_service/
│   ├── AuthControllerIntegrationTest.java      (Testes de Integração)
│   ├── UserControllerIntegrationTest.java      (Testes de Integração)
│   ├── AuthServiceIntegrationTest.java         (Testes de Integração)
│   ├── UserServiceIntegrationTest.java         (Testes de Integração)
│   ├── model/
│   │   └── UserTest.java                       (Testes Unitários)
│   ├── security/
│   │   └── JwtUtilTest.java                    (Testes Unitários)
│   ├── service/
│   │   ├── AuthServiceTest.java                (Testes Unitários)
│   │   └── UserServiceTest.java                (Testes Unitários)
│   └── web/
│       ├── AuthControllerTest.java             (Testes Unitários)
│       ├── UserControllerTest.java             (Testes Unitários)
│       └── dto/
│           └── DTOTest.java                    (Testes Unitários)
└── resources/
    └── application-test.yaml                   (Configuração para Testes)
```

---

## 🧪 Testes Unitários

### 1. **UserServiceTest** ✅
- `testCreateUserSuccess()` - Deve criar um novo usuário com sucesso
- `testCreateUserAlreadyExists()` - Deve lançar exceção quando usuário já existe
- `testPasswordMatches()` - Deve validar senha corretamente
- `testPasswordMismatch()` - Deve rejeitar senha incorreta
- `testEmptyPasswordMismatch()` - Deve rejeitar senha vazia

**Cobertura**: Criação de usuário, validação de senha com BCrypt

### 2. **AuthServiceTest** ✅
- `testAuthenticateSuccess()` - Deve autenticar usuário com credenciais corretas
- `testAuthenticateFailureWrongPassword()` - Deve rejeitar autenticação com senha incorreta
- `testAuthenticateFailureUserNotFound()` - Deve rejeitar autenticação com usuário inexistente
- `testIssueTokenSuccess()` - Deve gerar token JWT válido
- `testValidateTokenSuccess()` - Deve validar token JWT válido
- `testValidateTokenFailure()` - Deve rejeitar token JWT inválido
- `testValidateEmptyToken()` - Deve rejeitar token JWT vazio
- `testGetSubjectSuccess()` - Deve extrair subject de token JWT válido
- `testGetSubjectFailure()` - Deve retornar null para subject de token inválido

**Cobertura**: Autenticação, geração e validação de JWT

### 3. **JwtUtilTest** ✅
- `testGenerateTokenSuccess()` - Deve gerar token JWT válido
- `testGenerateDifferentTokens()` - Deve gerar tokens diferentes para mesmo subject
- `testValidateTokenSuccess()` - Deve validar token gerado como válido
- `testValidateInvalidToken()` - Deve rejeitar token inválido
- `testValidateEmptyToken()` - Deve rejeitar token vazio
- `testValidateNullToken()` - Deve rejeitar token null
- `testGetSubjectSuccess()` - Deve extrair subject de token válido
- `testGetSubjectInvalidToken()` - Deve retornar null ao extrair subject de token inválido
- `testGetSubjectEmptyToken()` - Deve retornar null ao extrair subject de token vazio
- `testGenerateTokenWithDifferentTTL()` - Deve respeitar TTL ao gerar token
- `testGenerateTokenWithDefaultTTL()` - Deve usar TTL padrão quando TTL <= 0

**Cobertura**: Geração, validação e extração de JWT

### 4. **AuthControllerTest** ✅
- `testLoginSuccess()` - Deve fazer login com credenciais corretas
- `testLoginFailure()` - Deve rejeitar login com credenciais incorretas
- `testValidateTokenSuccess()` - Deve validar token JWT válido
- `testValidateTokenFailure()` - Deve retornar válido=false para token inválido
- `testLoginWithoutBody()` - Deve rejeitar requisição de login sem corpo
- `testValidateWithoutBody()` - Deve rejeitar requisição de validação sem corpo

**Cobertura**: Endpoints `/auth/login` e `/auth/validate`

### 5. **UserControllerTest** ✅
- `testCreateUserSuccess()` - Deve criar um novo usuário com sucesso
- `testCreateUserAlreadyExists()` - Deve rejeitar criação de usuário duplicado
- `testCreateUserWithoutBody()` - Deve rejeitar requisição de criação sem corpo
- `testCreateUserWithoutUsername()` - Deve rejeitar criação sem username
- `testCreateUserWithoutPassword()` - Deve rejeitar criação sem password
- `testCreateUserReturnsId()` - Deve retornar ID do usuário criado

**Cobertura**: Endpoint `POST /users`

### 6. **UserTest** ✅
- `testUserCreation()` - Deve criar usuário com username e passwordHash
- `testEmptyUserCreation()` - Deve criar usuário vazio
- `testSetUsername()` - Deve atualizar username
- `testSetPasswordHash()` - Deve atualizar passwordHash
- `testIdInitiallyNull()` - Deve retornar null para ID não atribuído
- `testSetId()` - Deve armazenar ID após atribuição
- `testMultipleUpdates()` - Deve manter dados após múltiplas modificações
- `testUsersDifferentiation()` - Deve diferenciar entre dois usuários diferentes

**Cobertura**: Entity User

### 7. **DTOTest** ✅
Teste agrupado com classes aninhadas:

#### LoginRequestTest
- `testLoginRequestCreation()` - Deve criar LoginRequest com valores
- `testLoginRequestEmptyCreation()` - Deve criar LoginRequest vazio
- `testSetUsername()` - Deve atualizar username
- `testSetPassword()` - Deve atualizar password
- `testSetTtlSeconds()` - Deve atualizar TTL

#### ValidateRequestTest
- `testValidateRequestCreation()` - Deve criar ValidateRequest com token
- `testValidateRequestEmptyCreation()` - Deve criar ValidateRequest vazio
- `testSetToken()` - Deve atualizar token

#### UserCreateRequestTest
- `testUserCreateRequestCreation()` - Deve criar UserCreateRequest com valores
- `testUserCreateRequestEmptyCreation()` - Deve criar UserCreateRequest vazio
- `testSetUsername()` - Deve atualizar username
- `testSetPassword()` - Deve atualizar password

#### TokenResponseTest
- `testTokenResponseCreation()` - Deve criar TokenResponse com token
- `testSetToken()` - Deve atualizar token

#### UserResponseTest
- `testUserResponseCreation()` - Deve criar UserResponse com valores
- `testSetUsername()` - Deve atualizar username

#### ValidateResponseTest
- `testValidateResponseCreationValid()` - Deve criar ValidateResponse válido
- `testValidateResponseCreationInvalid()` - Deve criar ValidateResponse inválido
- `testSetValid()` - Deve atualizar valid flag
- `testSetSubject()` - Deve atualizar subject

**Cobertura**: Todos os DTOs do projeto

---

## 🔗 Testes de Integração

### 1. **AuthControllerIntegrationTest** ✅
- `testCompleteAuthenticationFlow()` - Deve executar fluxo completo: criar usuário, fazer login e validar token
- `testLoginWithExistingUser()` - Deve fazer login com usuário existente
- `testLoginWithWrongPassword()` - Deve rejeitar login com senha incorreta
- `testLoginWithNonExistentUser()` - Deve rejeitar login com usuário inexistente
- `testTokenValidation()` - Deve validar token JWT gerado
- `testInvalidTokenValidation()` - Deve rejeitar token inválido
- `testCreateDuplicateUser()` - Deve rejeitar duplicação de usuário
- `testUserIsolation()` - Deve manter usuários isolados no banco de dados

**Cobertura**: Fluxo completo de autenticação com banco de dados

### 2. **UserServiceIntegrationTest** ✅
- `testCreateUserPersistence()` - Deve criar usuário e persistir no banco de dados
- `testRetrieveCreatedUser()` - Deve recuperar usuário criado do banco de dados
- `testCreateDuplicateUserFails()` - Deve falhar ao criar usuário duplicado
- `testPasswordValidationAfterPersistence()` - Deve validar senha corretamente após persistência
- `testIncorrectPasswordRejectionAfterPersistence()` - Deve rejeitar senha incorreta após persistência
- `testCreateMultipleUsers()` - Deve criar múltiplos usuários sem conflito
- `testPasswordEncodingIsUnique()` - Deve codificar senha diferente a cada criação
- `testEncodedPasswordIsDifferentFromOriginal()` - Deve validar que senha codificada não é igual à original
- `testUserExistsAfterCreation()` - Deve existir usuário após criação

**Cobertura**: UserService com banco de dados real

### 3. **AuthServiceIntegrationTest** ✅
- `testAuthenticateExistingUserSuccess()` - Deve autenticar usuário existente com credenciais corretas
- `testAuthenticateWithWrongPassword()` - Deve rejeitar autenticação com senha incorreta
- `testAuthenticateNonExistentUser()` - Deve rejeitar autenticação com usuário inexistente
- `testIssueTokenSuccess()` - Deve gerar token para usuário autenticado
- `testGeneratedTokenIsValid()` - Deve validar token gerado
- `testGetSubjectFromToken()` - Deve extrair subject correto do token
- `testValidateInvalidToken()` - Deve rejeitar token inválido
- `testGetSubjectFromInvalidToken()` - Deve retornar null para subject de token inválido
- `testCompleteAuthFlow()` - Deve executar fluxo completo: autenticar, emitir token e validar
- `testGenerateDifferentTokensForSameUser()` - Deve gerar tokens diferentes para mesmo usuário
- `testAuthenticationFailsAfterUserDeletion()` - Deve falhar ao autenticar após deletar usuário
- `testTokenWithDifferentTTL()` - Deve validar token com TTL diferente
- `testMultipleUsersTokenIsolation()` - Deve manter isolamento entre múltiplos usuários

**Cobertura**: AuthService com banco de dados real

### 4. **UserControllerIntegrationTest** ✅
- `testCreateUserPersistence()` - Deve criar usuário e persistir no banco de dados
- `testCreateUserWithEmptyUsername()` - Deve rejeitar criação de usuário com username vazio
- `testCreateUserWithEmptyPassword()` - Deve rejeitar criação de usuário com password vazio
- `testCreateMultipleUsers()` - Deve criar múltiplos usuários sem conflito
- `testPreventDuplicateUsername()` - Deve impedir duplicação de username
- `testCreateUserReturnsValidId()` - Deve retornar ID do usuário criado
- `testCreateUserReturnsJsonResponse()` - Deve responder com Content-Type application/json
- `testCreateUserWithSpecialCharactersPassword()` - Deve aceitar passwords com caracteres especiais
- `testCreateUserWithSpecialUsernameCharacters()` - Deve aceitar usernames com pontos e underscores
- `testCreatedUserCanLogin()` - Deve validar que usuário criado pode fazer login
- `testCreateUserWithoutContentType()` - Deve rejeitar requisição sem Content-Type

**Cobertura**: UserController com banco de dados real

---

## 📊 Resumo de Cobertura

| Componente | Testes Unitários | Testes Integração | Total |
|---|---|---|---|
| **UserService** | 5 | 9 | 14 |
| **AuthService** | 9 | 13 | 22 |
| **JwtUtil** | 11 | - | 11 |
| **UserController** | 6 | 11 | 17 |
| **AuthController** | 6 | 8 | 14 |
| **User Model** | 8 | - | 8 |
| **DTOs** | 24 | - | 24 |
| **Total** | **69 testes** | **41 testes** | **110 testes** |

---

## 🚀 Como Executar os Testes

### Executar todos os testes
```bash
./gradlew test
```

### Executar apenas testes unitários
```bash
./gradlew test --tests "*Test" -x "*IntegrationTest"
```

### Executar apenas testes de integração
```bash
./gradlew test --tests "*IntegrationTest"
```

### Executar testes de uma classe específica
```bash
./gradlew test --tests "UserServiceTest"
```

### Executar testes com relatório detalhado
```bash
./gradlew test --info
```

### Gerar relatório de cobertura (com JaCoCo)
```bash
./gradlew test jacocoTestReport
```

---

## 🔧 Configuração dos Testes

### application-test.yaml
- **Banco de Dados**: H2 em memória (criação automática)
- **Flyway**: Desativado para testes (usa `ddl-auto: create-drop`)
- **Redis**: Simulado/Mockado
- **Port**: 8082

---

## ✅ Checklist de Testes

- [x] Testes de unidade para Services
- [x] Testes de unidade para Controllers (com Mock)
- [x] Testes de unidade para Entities
- [x] Testes de unidade para DTOs
- [x] Testes de unidade para Util (JwtUtil)
- [x] Testes de integração para Services (com BD real)
- [x] Testes de integração para Controllers (com BD real)
- [x] Fluxos completos de autenticação
- [x] Testes de isolamento de dados
- [x] Testes de validação e erro

---

## 📝 Boas Práticas Implementadas

1. ✅ **Nomenclatura clara**: Cada teste descreve exatamente o que está sendo testado
2. ✅ **Padrão AAA**: Arrange, Act, Assert em todos os testes
3. ✅ **Isolamento**: Dados de teste são criados e destruídos a cada teste
4. ✅ **Mockito**: Uso de mocks nos testes unitários
5. ✅ **Transações**: Testes de integração com `@Transactional` para limpeza automática
6. ✅ **ActiveProfiles**: Uso de `application-test.yaml` para configuração de testes
7. ✅ **DisplayName**: Descrições claras com `@DisplayName`
8. ✅ **Nested Classes**: Agrupamento lógico de testes relacionados (DTOs)
9. ✅ **BeforeEach**: Setup comum para todos os testes
10. ✅ **Assertions específicas**: Uso de assertEquals, assertTrue, assertFalse, etc.

---

## 🐛 Cobertura por Funcionalidade

### Autenticação e JWT
- ✅ Geração de tokens JWT
- ✅ Validação de tokens JWT
- ✅ Extração de subject de token
- ✅ Rejeição de tokens inválidos
- ✅ TTL de token

### Gerenciamento de Usuários
- ✅ Criação de usuário
- ✅ Prevenção de duplicatas
- ✅ Validação de senha com BCrypt
- ✅ Persistência no banco de dados
- ✅ Recuperação de usuário

### Controllers
- ✅ POST /users (criar usuário)
- ✅ POST /auth/login (autenticação)
- ✅ POST /auth/validate (validação de token)
- ✅ Validação de entrada (Body)
- ✅ Validação de Content-Type

### Fluxos Completos
- ✅ Criar usuário → Login → Validar token
- ✅ Múltiplos usuários
- ✅ Isolamento de dados
- ✅ Deletar e falhar autenticação

---

**Última Atualização**: 2024
**Total de Testes**: 110
**Taxa de Cobertura Esperada**: 85%+

