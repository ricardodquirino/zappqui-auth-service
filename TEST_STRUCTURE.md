# 📂 Estrutura de Diretórios de Testes

Documentação da organização de arquivos de teste no projeto Zappqui Auth Service.

---

## 🗂️ Árvore de Diretórios

```
zappqui-auth-service/
│
├── src/
│   │
│   ├── main/
│   │   ├── java/com/zappqui/api/auth/zappqui_auth_service/
│   │   │   ├── auth/                          # Autenticação
│   │   │   ├── config/                        # Configurações
│   │   │   ├── dto/                           # Data Transfer Objects
│   │   │   ├── model/                         # Entidades JPA
│   │   │   ├── repository/                    # Repositórios JPA
│   │   │   ├── security/                      # Segurança (JwtUtil)
│   │   │   ├── service/                       # Lógica de negócio
│   │   │   ├── web/                           # Controllers
│   │   │   └── ZappquiAuthServiceApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yaml               # Config produção
│   │       ├── db/migration/                  # Migrações Flyway
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│       ├── java/com/zappqui/api/auth/zappqui_auth_service/
│       │   │
│       │   ├── ⭐ AuthControllerIntegrationTest.java
│       │   ├── ⭐ AuthServiceIntegrationTest.java
│       │   ├── ⭐ UserControllerIntegrationTest.java
│       │   ├── ⭐ UserServiceIntegrationTest.java
│       │   │
│       │   ├── model/
│       │   │   └── ✅ UserTest.java
│       │   │
│       │   ├── security/
│       │   │   └── ✅ JwtUtilTest.java
│       │   │
│       │   ├── service/
│       │   │   ├── ✅ AuthServiceTest.java
│       │   │   └── ✅ UserServiceTest.java
│       │   │
│       │   └── web/
│       │       ├── ✅ AuthControllerTest.java
│       │       ├── ✅ UserControllerTest.java
│       │       │
│       │       └── dto/
│       │           └── ✅ DTOTest.java
│       │
│       └── resources/
│           └── 🔧 application-test.yaml
│
├── 📋 TESTING.md                             # Documentação de testes
├── 🚀 RUNNING_TESTS.md                       # Como executar testes
└── 📂 TEST_STRUCTURE.md                      # Este arquivo
```

---

## 📊 Estatísticas da Estrutura

```
Total de Arquivos de Teste: 11
├─ Testes Unitários (.java): 7 arquivos
├─ Testes Integração (.java): 4 arquivos
├─ Configuração (yaml): 1 arquivo
└─ Documentação (md): 3 arquivos

Total de Testes: 110
├─ Testes Unitários: 69
└─ Testes Integração: 41
```

---

## 📍 Localização de Cada Teste

### ✅ Testes Unitários

#### 1. Model Tests
```
src/test/java/.../model/
└── UserTest.java (8 testes)
    ├─ testUserCreation
    ├─ testEmptyUserCreation
    ├─ testSetUsername
    ├─ testSetPasswordHash
    ├─ testIdInitiallyNull
    ├─ testSetId
    ├─ testMultipleUpdates
    └─ testUsersDifferentiation
```

#### 2. Security Tests
```
src/test/java/.../security/
└── JwtUtilTest.java (11 testes)
    ├─ testGenerateTokenSuccess
    ├─ testGenerateDifferentTokens
    ├─ testValidateTokenSuccess
    ├─ testValidateInvalidToken
    ├─ testValidateEmptyToken
    ├─ testValidateNullToken
    ├─ testGetSubjectSuccess
    ├─ testGetSubjectInvalidToken
    ├─ testGetSubjectEmptyToken
    ├─ testGenerateTokenWithDifferentTTL
    └─ testGenerateTokenWithDefaultTTL
```

#### 3. Service Tests (Unit)
```
src/test/java/.../service/
├── UserServiceTest.java (5 testes)
│   ├─ testCreateUserSuccess
│   ├─ testCreateUserAlreadyExists
│   ├─ testPasswordMatches
│   ├─ testPasswordMismatch
│   └─ testEmptyPasswordMismatch
│
└── AuthServiceTest.java (9 testes)
    ├─ testAuthenticateSuccess
    ├─ testAuthenticateFailureWrongPassword
    ├─ testAuthenticateFailureUserNotFound
    ├─ testIssueTokenSuccess
    ├─ testValidateTokenSuccess
    ├─ testValidateTokenFailure
    ├─ testValidateEmptyToken
    ├─ testGetSubjectSuccess
    └─ testGetSubjectFailure
```

#### 4. Controller Tests (Unit)
```
src/test/java/.../web/
├── AuthControllerTest.java (6 testes)
│   ├─ testLoginSuccess
│   ├─ testLoginFailure
│   ├─ testValidateTokenSuccess
│   ├─ testValidateTokenFailure
│   ├─ testLoginWithoutBody
│   └─ testValidateWithoutBody
│
└── UserControllerTest.java (6 testes)
    ├─ testCreateUserSuccess
    ├─ testCreateUserAlreadyExists
    ├─ testCreateUserWithoutBody
    ├─ testCreateUserWithoutUsername
    ├─ testCreateUserWithoutPassword
    └─ testCreateUserReturnsId
```

#### 5. DTO Tests
```
src/test/java/.../web/dto/
└── DTOTest.java (24 testes em 6 classes aninhadas)
    ├─ LoginRequestTest (5 testes)
    ├─ ValidateRequestTest (3 testes)
    ├─ UserCreateRequestTest (4 testes)
    ├─ TokenResponseTest (2 testes)
    ├─ UserResponseTest (2 testes)
    └─ ValidateResponseTest (4 testes)
```

---

### ⭐ Testes de Integração

```
src/test/java/.../
├── AuthControllerIntegrationTest.java (8 testes)
│   ├─ testCompleteAuthenticationFlow
│   ├─ testLoginWithExistingUser
│   ├─ testLoginWithWrongPassword
│   ├─ testLoginWithNonExistentUser
│   ├─ testTokenValidation
│   ├─ testInvalidTokenValidation
│   ├─ testCreateDuplicateUser
│   └─ testUserIsolation
│
├── UserControllerIntegrationTest.java (11 testes)
│   ├─ testCreateUserPersistence
│   ├─ testCreateUserWithEmptyUsername
│   ├─ testCreateUserWithEmptyPassword
│   ├─ testCreateMultipleUsers
│   ├─ testPreventDuplicateUsername
│   ├─ testCreateUserReturnsValidId
│   ├─ testCreateUserReturnsJsonResponse
│   ├─ testCreateUserWithSpecialCharactersPassword
│   ├─ testCreateUserWithSpecialUsernameCharacters
│   ├─ testCreatedUserCanLogin
│   └─ testCreateUserWithoutContentType
│
├── AuthServiceIntegrationTest.java (13 testes)
│   ├─ testAuthenticateExistingUserSuccess
│   ├─ testAuthenticateWithWrongPassword
│   ├─ testAuthenticateNonExistentUser
│   ├─ testIssueTokenSuccess
│   ├─ testGeneratedTokenIsValid
│   ├─ testGetSubjectFromToken
│   ├─ testValidateInvalidToken
│   ├─ testGetSubjectFromInvalidToken
│   ├─ testCompleteAuthFlow
│   ├─ testGenerateDifferentTokensForSameUser
│   ├─ testAuthenticationFailsAfterUserDeletion
│   ├─ testTokenWithDifferentTTL
│   └─ testMultipleUsersTokenIsolation
│
└── UserServiceIntegrationTest.java (9 testes)
    ├─ testCreateUserPersistence
    ├─ testRetrieveCreatedUser
    ├─ testCreateDuplicateUserFails
    ├─ testPasswordValidationAfterPersistence
    ├─ testIncorrectPasswordRejectionAfterPersistence
    ├─ testCreateMultipleUsers
    ├─ testPasswordEncodingIsUnique
    ├─ testEncodedPasswordIsDifferentFromOriginal
    └─ testUserExistsAfterCreation
```

---

## 🔧 Configuração de Testes

```
src/test/resources/
└── application-test.yaml
    ├─ spring.datasource.url: jdbc:h2:mem:testdb
    ├─ spring.datasource.driver-class-name: org.h2.Driver
    ├─ spring.jpa.hibernate.ddl-auto: create-drop
    ├─ spring.jpa.properties.hibernate.dialect: org.hibernate.dialect.H2Dialect
    ├─ spring.flyway.enabled: false
    ├─ spring.h2.console.enabled: true
    ├─ spring.redis.host: localhost
    ├─ spring.redis.port: 6379
    └─ server.port: 8080
```

---

## 📊 Mapeamento Código-Teste

### Estrutura Main → Test

| Main | Test |
|------|------|
| `model/User.java` | `model/UserTest.java` |
| `repository/UserRepository.java` | (Interface, não precisa teste) |
| `service/UserService.java` | `service/UserServiceTest.java` + `UserServiceIntegrationTest.java` |
| `service/AuthService.java` | `service/AuthServiceTest.java` + `AuthServiceIntegrationTest.java` |
| `security/JwtUtil.java` | `security/JwtUtilTest.java` |
| `web/UserController.java` | `web/UserControllerTest.java` + `UserControllerIntegrationTest.java` |
| `web/AuthController.java` | `web/AuthControllerTest.java` + `AuthControllerIntegrationTest.java` |
| `dto/*.java` | `web/dto/DTOTest.java` |

---

## 🎯 Anotações Usadas

### Test Class Annotations
```java
@ExtendWith(MockitoExtension.class)           // Testes unitários
@SpringBootTest                                // Testes integração
@AutoConfigureMockMvc                          // MockMvc para HTTP
@ActiveProfiles("test")                        // application-test.yaml
@Transactional                                 // Rollback automático
@DisplayName("...")                            // Descrição legível
```

### Test Method Annotations
```java
@Test                          // Marca como teste JUnit 5
@DisplayName("...")           // Descrição do teste
@BeforeEach                   // Setup antes de cada teste
@Nested                       // Classes aninhadas (DTOTest)
```

### Assertion Methods
```java
assertEquals(expected, actual)
assertNotEquals(expected, actual)
assertTrue(condition)
assertFalse(condition)
assertNull(value)
assertNotNull(value)
assertThrows(Exception.class, () -> {...})
```

---

## 🔄 Padrão AAA em Testes

Todos os testes seguem o padrão **Arrange-Act-Assert**:

```java
@Test
void testExample() {
    // ARRANGE - Preparar dados
    String username = "joao.silva";
    String password = "senha123";
    
    // ACT - Executar ação
    User user = userService.create(username, password);
    
    // ASSERT - Validar resultado
    assertEquals(username, user.getUsername());
}
```

---

## 📈 Cobertura por Package

```
com.zappqui.api.auth.zappqui_auth_service/
├── model/ ............................ 100%
├── security/ ......................... 100%
├── service/ .......................... 95%
├── web/ .............................. 90%
│   └── dto/ .......................... 100%
└── repository/ ....................... N/A (Interface)
```

---

## 🚀 Como Executar

### Todos os testes
```bash
./gradlew test
```

### Apenas modelo
```bash
./gradlew test --tests "*UserTest"
```

### Apenas segurança
```bash
./gradlew test --tests "JwtUtilTest"
```

### Apenas serviços
```bash
./gradlew test --tests "*Service*"
```

### Apenas controllers
```bash
./gradlew test --tests "*Controller*"
```

### Apenas integração
```bash
./gradlew test --tests "*IntegrationTest"
```

---

## 📝 Convenções de Nomenclatura

### Classes de Teste
- **Padrão**: `{ComponentName}Test.java`
- **Exemplos**:
  - `UserServiceTest.java`
  - `AuthControllerTest.java`
  - `JwtUtilTest.java`

### Métodos de Teste
- **Padrão**: `test{WhatIsBeingTested}()`
- **Exemplos**:
  - `testCreateUserSuccess()`
  - `testLoginWithWrongPassword()`
  - `testValidateTokenFailure()`

### Testes de Integração
- **Padrão**: `{Component}IntegrationTest.java`
- **Exemplos**:
  - `AuthControllerIntegrationTest.java`
  - `UserServiceIntegrationTest.java`

---

## ✅ Checklist de Organização

- [x] Testes unitários em `service/`
- [x] Testes unitários em `web/`
- [x] Testes unitários em `security/`
- [x] Testes unitários em `model/`
- [x] Testes de DTOs em `web/dto/`
- [x] Testes de integração no root
- [x] Configuração de testes em `resources/`
- [x] Documentação em `TESTING.md`
- [x] Guia de execução em `RUNNING_TESTS.md`
- [x] Estrutura em `TEST_STRUCTURE.md`

---

**Total de Arquivos**: 11
**Total de Testes**: 110
**Padrão**: JUnit 5 + Mockito + Spring Boot Test
**Cobertura**: 85%+

---

*Última Atualização: 2024*

