# 🧪 Guia Prático de Execução de Testes

Este guia fornece instruções práticas para executar, depurar e analisar os testes do projeto.

---

## ⚡ Início Rápido

### Executar todos os testes
```bash
./gradlew test
```

**Saída esperada**:
```
BUILD SUCCESSFUL

Task :test

110 tests completed, 0 failed
```

---

## 🎯 Executar Testes por Categoria

### 1. Apenas Testes Unitários
```bash
./gradlew test --tests "*Test" -x "*IntegrationTest"
```

**Resultado**: 69 testes (sem integração)

### 2. Apenas Testes de Integração
```bash
./gradlew test --tests "*IntegrationTest"
```

**Resultado**: 41 testes

### 3. Testes de um Package Específico
```bash
./gradlew test --tests "com.zappqui.api.auth.zappqui_auth_service.service.*"
```

### 4. Testes de uma Classe Específica
```bash
./gradlew test --tests "UserServiceTest"
```

---

## 📋 Executar Testes por Componente

### Testes do UserService
```bash
./gradlew test --tests "*UserService*"
```

### Testes do AuthService
```bash
./gradlew test --tests "*AuthService*"
```

### Testes do JwtUtil
```bash
./gradlew test --tests "JwtUtilTest"
```

### Testes dos Controllers
```bash
./gradlew test --tests "*Controller*"
```

### Testes de Integração da Auth
```bash
./gradlew test --tests "Auth*IntegrationTest"
```

---

## 🔍 Executar com Modo Verbose

```bash
./gradlew test --info
```

**Mostra**:
- Cada teste sendo executado
- Tempo de execução
- Status de sucesso/falha
- Stack traces de erros

### Mais Detalhado
```bash
./gradlew test --debug
```

---

## 📊 Gerar Relatório de Testes

### Relatório HTML (padrão)
```bash
./gradlew test
# Abra: build/reports/tests/test/index.html
```

### Com Task Específica
```bash
./gradlew test testReport
```

---

## 📈 Cobertura de Código (JaCoCo)

### Gerar relatório de cobertura
```bash
./gradlew test jacocoTestReport
```

**Localização**: `build/reports/jacoco/test/html/index.html`

### Ver cobertura no console
```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

---

## 🚨 Depuração de Testes

### Executar um teste no debugger
```bash
./gradlew test --tests "UserServiceTest.testCreateUserSuccess" --debug-jvm
```

### Em IDE (IntelliJ IDEA)
1. Clique com botão direito no teste
2. Selecione "Debug 'testName'"
3. Use breakpoints normalmente

### Em VS Code com Test Explorer
1. Instale a extensão "Test Explorer for Java"
2. Clique em "Debug" ao lado do teste

---

## ❌ Solucionar Problemas

### Erro: "No tests found"
```bash
# Limpar build anterior
./gradlew clean

# Reexecutar
./gradlew test
```

### Erro: "Connection refused"
```
Erro: Conexão recusada no banco de dados

Solução: Verificar se application-test.yaml está usando H2
```

### Erro: "TestProfileValueResolver"
```bash
# Verificar se @ActiveProfiles("test") está configurado
# Verificar se application-test.yaml existe em src/test/resources
```

### Timeout em Testes
```bash
# Aumentar timeout
./gradlew test --gradle.daemon=false
```

---

## 📝 Saída de Teste Esperada

### Sucesso
```
BUILD SUCCESSFUL in 45s

Task :test
110 tests completed, 0 failed
```

### Com Falhas
```
BUILD FAILED in 1m 23s

Task :test FAILED

FAILURE: Build failed with an exception.

Tests that failed:
- com.zappqui...UserServiceTest > testCreateUserSuccess
```

---

## 📑 Interpretar Relatórios

### HTML Report (build/reports/tests/test/index.html)

**Seções**:
1. **Passing** - Testes bem-sucedidos (verde)
2. **Failing** - Testes com falha (vermelho)
3. **Skipped** - Testes ignorados (amarelo)

**Por classe**:
- Número de testes
- Taxa de sucesso
- Tempo de execução
- Detalhes de cada teste

### JaCoCo Report (build/reports/jacoco/test/html/index.html)

**Informações**:
- Cobertura por linha
- Cobertura por branch
- Classes mais testadas
- Métodos não cobertos

---

## 🎓 Exemplos de Uso

### Cenário 1: Desenvolver novo teste
```bash
# 1. Escrever o teste em UserServiceTest
# 2. Rodar apenas aquele teste
./gradlew test --tests "UserServiceTest.testNewFeature"

# 3. Se passar, rodar todos os testes do UserService
./gradlew test --tests "*UserService*"

# 4. Finalmente, rodar suite completa
./gradlew test
```

### Cenário 2: Refatorar código
```bash
# 1. Antes de refatorar, executar todos os testes
./gradlew test

# 2. Fazer refatoração
# 3. Reexecutar testes
./gradlew test

# 4. Verificar cobertura de código
./gradlew test jacocoTestReport
```

### Cenário 3: PR/Code Review
```bash
# 1. Executar testes completos
./gradlew test --info

# 2. Gerar relatório de cobertura
./gradlew test jacocoTestReport

# 3. Verificar relatório HTML
# build/reports/tests/test/index.html
# build/reports/jacoco/test/html/index.html
```

---

## 🔄 CI/CD Integration

### GitHub Actions
```yaml
- name: Run Tests
  run: ./gradlew test

- name: Generate Coverage Report
  run: ./gradlew test jacocoTestReport

- name: Upload Coverage
  uses: codecov/codecov-action@v3
  with:
    files: ./build/reports/jacoco/test/jacoco.xml
```

### GitLab CI
```yaml
test:
  script:
    - ./gradlew test
  artifacts:
    reports:
      junit: build/test-results/test/*.xml
    paths:
      - build/reports/tests/test/
      - build/reports/jacoco/test/
```

---

## 📊 Monitorar Testes

### Contar testes por arquivo
```bash
find src/test -name "*.java" -exec grep -l "@Test" {} \; | wc -l
```

### Contar número de testes
```bash
grep -r "@Test" src/test --include="*.java" | wc -l
```

### Listar todos os testes
```bash
./gradlew test --dry-run
```

---

## 🎯 Boas Práticas

1. **Sempre rodar testes antes de commit**
   ```bash
   ./gradlew test
   ```

2. **Incluir novos testes para novas funcionalidades**
   ```bash
   # Padrão: 1 teste unitário + 1 teste de integração por feature
   ```

3. **Manter cobertura acima de 80%**
   ```bash
   ./gradlew test jacocoTestReport
   ```

4. **Rodar suite completa antes de push**
   ```bash
   ./gradlew clean test
   ```

5. **Nomear testes descritivamente**
   ```java
   @Test
   @DisplayName("Deve criar usuário e persistir no banco de dados")
   void testCreateUserPersistence() { ... }
   ```

---

## 📞 Suporte e Dúvidas

Se encontrar problemas:

1. Verifique se `application-test.yaml` está em `src/test/resources/`
2. Certifique-se de que H2 está nas dependências do teste
3. Verifique se `@ActiveProfiles("test")` está configurado
4. Limpe o build: `./gradlew clean`
5. Reexecute: `./gradlew test`

---

**Última Atualização**: 2024
**Total de Testes**: 110
**Cobertura Esperada**: 85%+

