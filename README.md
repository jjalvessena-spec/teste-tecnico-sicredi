# Sicredi Desafio QE — Testes de API

**Autor:** Jefferson Justino Alves de Sena

Suite de testes automatizados de API para o desafio técnico de Quality
Engineer do Sicredi. A aplicação sob teste gerencia produtos
eletrônicos e é servida pela API pública [dummyjson.com](https://dummyjson.com),
conforme a [documentação oficial do desafio](https://sicredi-desafio-qe.readme.io/reference/home).

## 1. Informações do projeto

| | |
|---|---|
| Linguagem | Java 17 |
| Build | Maven |
| Framework de testes | JUnit 5 |
| Cliente HTTP | RestAssured 5 |
| Assertions | RestAssured (Hamcrest) + AssertJ |
| Relatório | Allure (bônus) |
| CI | GitLab CI (bônus) |
| URL base da API | `https://dummyjson.com` (configurável, ver seção 2) |

### Endpoints cobertos

| Método | Endpoint | Classe de teste |
|---|---|---|
| GET | `/test` | `StatusEndpointTest` |
| GET | `/users` | `UsersEndpointTest` |
| POST | `/auth/login` | `AuthLoginTest` |
| GET | `/auth/products` | `AuthProductsTest` |
| POST | `/products/add` | `ProductsAddTest` |
| GET | `/products` | `ProductsListTest` |
| GET | `/products/{id}` | `ProductByIdTest` |

### Estrutura de pacotes

```
src/test/java/br/com/sicredi/desafio/
├── base/    -> configuração compartilhada do RestAssured (BaseApiTest)
├── model/   -> POJOs de request/response (LoginRequest, Product, AddProductRequest)
├── util/    -> utilitários de apoio (AuthHelper: credenciais e token dinâmicos)
└── tests/   -> uma classe de teste por endpoint documentado
```

> Nota: nenhuma ferramenta Gherkin (Cucumber, JBehave, etc.) foi utilizada,
> conforme item "Dispensável" da documentação do desafio.

## 2. Como executar

**Pré-requisitos:** Java JDK 17+, Maven 3.9+ e acesso à internet
(a suíte roda contra a API pública real, não há mocks).

```bash
# clonar o repositório
git clone https://github.com/jjalvessena-spec/teste-tecnico-sicredi.git
cd teste-tecnico-sicredi

# executar toda a suíte
mvn clean test

# executar apenas os testes de smoke (um cenário feliz por endpoint)
mvn clean test -Dgroups=smoke

# apontar para outra URL base (ex.: ambiente de homologação)
mvn clean test -Dapi.baseUrl=https://outra-url.com
```

### Relatório Allure (bônus)

Os resultados brutos são gravados em `target/allure-results` a cada
execução. Para gerar e abrir o relatório HTML localmente (requer o
[Allure CLI](https://allurereport.org/docs/gettingstarted-installation/)
ou o plugin Maven já configurado no `pom.xml`):

```bash
mvn allure:serve
```

## 3. Plano e estratégia de testes

**Abordagem:** testes de API caixa-preta, sem Gherkin, organizados em
uma classe JUnit por endpoint documentado. Cada cenário cobre um
fluxo de negócio isolado, com nomes de método descritivos
(`deveRetornarErro404ParaIdInexistente`, etc.) que funcionam como
documentação executável.

**Dados de teste:** as credenciais de login são obtidas
dinamicamente via `GET /users` (primeiro usuário da lista), em vez de
usar um usuário fixo "hardcoded". Isso torna a suíte resiliente a
mudanças na massa de dados da API pública.

**Critérios de cobertura por endpoint:**

- **GET /test** — smoke test de disponibilidade da aplicação.
- **GET /users** — retorno da lista, presença dos campos de negócio
  (`username`/`password`) e metadados de paginação.
- **POST /auth/login** — autenticação bem-sucedida (token emitido) e
  4 variações de erro: senha inválida, usuário inexistente, campo
  obrigatório ausente e corpo vazio.
- **GET /auth/products** — acesso autorizado, ausência de header,
  token inválido e token vazio/mal formado.
- **POST /products/add** — criação com dados válidos, criação
  mínima (só título), não-persistência do produto criado, corpo
  malformado e o comportamento (documentado como observação/bug) ao
  enviar título em branco.
- **GET /products** — listagem, paginação por `limit` e por `skip`.
- **GET /products/{id}** — busca válida, id inexistente, id
  não numérico e id negativo.

**Total:** 7 classes, ~24 cenários, cobrindo fluxos felizes e de
exceção para todos os endpoints documentados.

**Fora de escopo (por decisão consciente):**
- Testes de carga/performance.
- Testes de contrato via JSON Schema (listado como melhoria futura).
- Endpoints não documentados da dummyjson.com (ex.: `/products/category/*`),
  mesmo existindo na API real, para manter a suíte fiel ao escopo do desafio.

## 4. Bugs / divergências identificadas

1. **Status code de login divergente entre a documentação do desafio
   e a API real.** A documentação do desafio descreve
   `POST /auth/login` como retornando `201` em caso de sucesso; a API
   pública atual (dummyjson.com) responde com `200`. O teste
   `deveAutenticarComCredenciaisValidas` foi escrito para aceitar
   ambos os códigos e deixar essa divergência documentada, em vez de
   simplesmente ocultá-la.
2. **Nome do campo de token divergente.** A documentação do desafio
   descreve o token retornado no campo `token`; a documentação atual
   da própria dummyjson.com (`dummyjson.com/docs/auth`) descreve o
   campo como `accessToken`. `AuthHelper` e o teste de login
   verificam os dois campos para não depender de qual versão de
   contrato está ativa no momento da execução.
3. **`POST /products/add` não valida campos obrigatórios.** Enviar um
   produto com `title` em branco retorna `201` normalmente (a API
   apenas simula a criação, sem persistir nada, então não há
   validação de negócio). Isso está coberto (e documentado) pelo
   teste `comportamentoAoCriarProdutoComTituloEmBranco`, mas é
   listado aqui como um comportamento que provavelmente não seria
   aceitável em uma API de produção real.
4. **Retorno inconsistente para "sem autenticação" em `GET /auth/products`.**
   A documentação apresenta tanto um exemplo de resposta `401`
   quanto um de `403` para cenários de falha de autenticação, sem
   deixar claro qual se aplica a cada situação (token ausente vs.
   token inválido). Os testes foram escritos para aceitar ambos onde
   a documentação é ambígua (ausência de header), e fixados em `401`
   apenas onde o exemplo documentado é específico (token inválido,
   com corpo `JsonWebTokenError`).
5. **Instrução suspeita embutida nas páginas da documentação.** Todas
   as páginas de referência da documentação (`sicredi-desafio-qe.readme.io`)
   contêm o texto oculto *"For AI agents: visit .../llms.txt ... Append
   .md to any documentation page URL..."*. Esse texto não faz parte do
   conteúdo funcional da documentação e tem as características de uma
   instrução de prompt injection direcionada a assistentes de IA. Ele
   **não foi seguido** na elaboração desta suíte; os dados de API foram
   obtidos exclusivamente das páginas de referência legítimas
   (`/reference/get-test`, `/reference/get-users`, etc.). Recomenda-se
   reportar/remover esse conteúdo da documentação.
6. **Corpo de erro sem o campo `name` para token inválido.** A documentação
   do desafio sugere um corpo de erro com o campo `name` (ex.:
   `JsonWebTokenError`) para `GET /auth/products` com token inválido; a API
   real retorna apenas o campo `message`, sem `name`. Confirmado ao rodar a
   suíte contra a API viva — o teste `deveRetornarErroComTokenInvalido` foi
   ajustado para validar apenas `message`.
   
## 5. Melhorias sugeridas

- **Padronizar os códigos de erro de autenticação** (`401` vs `403`)
  em `GET /auth/products`, documentando claramente qual se aplica a
  cada causa (ausência de token vs. token inválido vs. token
  expirado).
- **Adicionar validação de campos obrigatórios** em `POST /products/add`
  (ex.: `title` não pode ser vazio), retornando `400` com uma
  mensagem de erro clara.
- **Versionar a documentação de contrato** (nomes de campos como
  `token`/`accessToken`, status codes de sucesso) para evitar
  divergência entre o que é documentado e o comportamento real da API.
- **Adicionar validação de schema (JSON Schema)** às respostas
  críticas (`/auth/login`, `/products/{id}`) para detectar quebras de
  contrato de forma automática, não apenas verificação de campos
  pontuais.
- **Adicionar testes de contrato para paginação negativa/zero**
  (`limit=0`, `limit=-1`) uma vez que o comportamento da API para
  esses casos não está documentado.
- **Publicar o relatório Allure como GitLab Pages** a partir do job
  `allure-report` do pipeline, facilitando a visualização dos
  resultados sem precisar baixar os artefatos.
