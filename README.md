# Coupon API - Desafio Técnico Spring Boot

API REST para gerenciamento e ciclo de vida de cupons promocionais desenvolvida em **Java 21 / Spring Boot 3.4**, seguindo os princípios de **Arquitetura em Camadas Pragmática** e **Entidade de Domínio Rica (Rich Domain Entity)**, balanceando clareza, alta manutenibilidade e ausência de complexidade acidental (*anti-over-engineering*).

---

## 🎯 Objetivo & Destaques da Arquitetura

O projeto foi construído para entregar **alto rigor técnico (padrão Pleno/Sênior)** com simplicidade e foco no negócio:

1. **Arquitetura em Camadas Pragmática**:
   - `model`: Entidade rica (`Coupon`) contendo anotações JPA e encapsulamento de regras de ciclo de vida (`delete()`, higienização de código, validação de desconto e expiração), evitando modelo anêmico.
   - `repository`: Spring Data JPA (`CouponRepository`) desacoplado de detalhes de infraestrutura.
   - `service`: Camada de aplicação e casos de uso (`CouponService`) orquestrando transações de forma limpa e direta.
   - `controller`: Exposição REST padronizada e documentada com OpenAPI v3 (`CouponController`).
   - `dto/request` e `dto/response`: DTOs dedicados com validação declarativa (`Jakarta Validation`).
   - `mapper`: Componente coeso (`CouponMapper`) para conversão explícita entre DTOs e entidade.
   - `exception`: Hierarquia de exceções de negócio e `GlobalExceptionHandler` padronizado.
   - `config`: Configurações centralizadas de Segurança (HTTP Basic) e Swagger.

2. **Persistência Confiável**:
   - Banco em memória **H2** (`jdbc:h2:mem:coupondb`).
   - Versionamento de banco com **Flyway** (`V1__create_coupon_table.sql`).
   - Validação de schema ativo via `spring.jpa.hibernate.ddl-auto=validate` (sem `update` em runtime).

3. **Segurança (Spring Security)**:
   - Autenticação **HTTP Basic** protegendo os endpoints `/coupon/**`.
   - Acesso público liberado ao **Swagger UI** e documentação da API (`/v3/api-docs/**`, `/swagger-ui/**`).

4. **Qualidade & Testes**:
   - Testes unitários da entidade rica cobrindo 100% das regras e casos de borda.
   - Testes unitários da camada de serviço com Mockito.
   - Testes de integração da camada web (`@WebMvcTest`) cobrindo cenários com sucesso, validação e autenticação.
   - Cobertura validada via **JaCoCo** (`mvn verify`).

5. **Containerização**:
   - `Dockerfile` multi-stage otimizado (JRE Alpine + usuário não-root).
   - `docker-compose.yml` pronto para execução com comando único.

---

## 📋 Regras de Negócio Implementadas

| Regra | Descrição | Comportamento na Aplicação |
|---|---|---|
| **Obrigatoriedade** | `code`, `description`, `discountValue`, `expirationDate` são obrigatórios. | Validados na borda (DTO) e na entidade de domínio. |
| **Formato do Código** | Alfanumérico com exatamente 6 caracteres. | Caracteres especiais e espaços são aceitos na entrada (`ABC-123`), higienizados pela entidade `Coupon`, resultando em exatamente 6 caracteres (`ABC123`). Entradas que resultem em tamanho diferente são rejeitadas com erro 400. |
| **Valor do Desconto** | Mínimo de 0,5 sem limite máximo. Saldo absoluto. | Validado pela entidade `Coupon` (rejeita valores < 0.5). |
| **Data de Expiração** | Não pode ser criada com data no passado. | Validada pela entidade `Coupon` comparando com o instante da requisição. |
| **Publicação** | Pode ser criado como publicado ou não. | Campo `published` booleano (default: `false`). |
| **Soft Delete** | Deleção lógica do cupom sem perda de histórico. | `DELETE /coupon/{id}` atualiza `status` para `DELETED`, preenche `deleted_at` e persiste. |
| **Proteção de Deleção** | Proibido deletar cupom já deletado. | O método de domínio `delete()` detecta status `DELETED` e lança `CouponAlreadyDeletedException` (HTTP 400). |

---

## 🚀 Como Executar

### Pré-requisitos
- JDK 21+ instalado (ou Docker).
- Maven 3.9+ (o projeto inclui o Maven Wrapper `mvnw`).

### 1. Executando Localmente com Maven Wrapper

```bash
# No diretório raiz do projeto:
./mvnw spring-boot:run
```
*(No Windows PowerShell: `.\mvnw.cmd spring-boot:run`)*

A aplicação iniciará na porta **8080**.

Os timestamps são persistidos como instantes UTC. A API os exibe no fuso
`America/Sao_Paulo`, e o H2 Console usa o mesmo fuso apenas para apresentação.

---

### 2. Executando com Docker Compose

```bash
# Build e inicialização do container
docker compose up --build

# Para parar:
docker compose down
```

---

## 🧪 Execução de Testes e Cobertura

Para rodar todos os testes unitários e de integração com relatório de cobertura JaCoCo:

```bash
./mvnw clean verify
```

O relatório interativo de cobertura estará disponível em:
`target/site/jacoco/index.html`

---

## 🔐 Autenticação & Credenciais

Os endpoints de negócio exigem autenticação **HTTP Basic**:

- **Usuário padrão**: `admin`
- **Senha padrão**: `admin123`

---

## 📖 Documentação da API (Swagger / OpenAPI)

Com a aplicação rodando, acesse a documentação interativa:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Dica**: No Swagger UI, clique no botão **Authorize** e insira as credenciais `admin` / `admin123` para testar os endpoints diretamente pela interface.

---

## 📡 Exemplos de Chamadas via cURL

### 1. Criar Cupom (`POST /coupon`)
```bash
curl -X POST http://localhost:8080/coupon \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ABC-123",
    "description": "Desconto de 80% de inauguração",
    "discountValue": 0.8,
    "expirationDate": "2026-12-31T23:59:59.000Z",
    "published": false
  }'
```
**Resposta (201 Created)**:
```json
{
  "id": "cef9d1e3-aae5-4ab6-a297-358c6032b1e7",
  "code": "ABC123",
  "description": "Desconto de 80% de inauguração",
  "discountValue": 0.8,
  "expirationDate": "2026-12-31T23:59:59Z",
  "status": "ACTIVE",
  "published": false,
  "redeemed": false
}
```

### 2. Consultar Cupom por ID (`GET /coupon/{id}`)
```bash
curl -X GET http://localhost:8080/coupon/cef9d1e3-aae5-4ab6-a297-358c6032b1e7 \
  -u admin:admin123
```

### 3. Deletar Cupom (`DELETE /coupon/{id}`)
```bash
curl -X DELETE http://localhost:8080/coupon/cef9d1e3-aae5-4ab6-a297-358c6032b1e7 \
  -u admin:admin123
```
**Resposta**: `204 No Content`

### 4. Tentar Deletar Cupom Já Deletado (Retorna 400 Bad Request)
```bash
curl -X DELETE http://localhost:8080/coupon/cef9d1e3-aae5-4ab6-a297-358c6032b1e7 \
  -u admin:admin123
```
**Resposta (400 Bad Request)**:
```json
{
  "timestamp": "2026-09-12T01:50:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Coupon with id 'cef9d1e3-aae5-4ab6-a297-358c6032b1e7' has already been deleted.",
  "path": "/coupon/cef9d1e3-aae5-4ab6-a297-358c6032b1e7"
}
```
