# Validation as a Service (VaaS)

Uma aplicação robusta construída sobre **Quarkus** e **Kotlin**, com suporte a **PostgreSQL** (produção), **HSQLDB** (desenvolvimento), versionamento de API e integração com abstrações de validadores de identidade.

## Tecnologias e Arquitetura
- **Kotlin 2.0+**
- **Quarkus 3+**
- **Hibernate ORM com Panache** (Padrão DAO/Repository)
- **HSQLDB** (Em Memória para o Profile `%dev`)
- **PostgreSQL** (Profile `%prod`)
- **OpenAPI / Swagger** para documentação 

## Arquitetura e Modelagem

O diagrama de classes detalhado contendo a relação entre as Validações, Fontes de Dados e Autenticadores pode ser encontrado e renderizado no arquivo abaixo:
🔗 **Diagrama de Classes PlantUML**: [docs/diagrams/class_diagram.puml](docs/diagrams/class_diagram.puml)

## Requisitos Atendidos
- **Persistência**: RDBMS (JPA Panache).
- **APIs Versionadas**: `/v1/source` e `/v1/validation`.
- **ACL (Access Control List)**: Fontes têm indicativo se são públicas ou privadas. Para fontes privadas, é informada a lista de `client_ids` permitidos.
- **Checagem de ACL**: O header HTTP `X-Client-Id` é validado recursivamente nos testes.
- **Autenticação Dupla (Dual Auth)**: A Credencial do Source agora suporta enum `BASIC` e `OAUTH2`.
- **OCP nas Validações**: Pattern Strategy utilizado para acomodar os validadores base (Registration Validation, Facial, Documental, etc).
- **Totalmente Stateless**: O container processa chamadas individualmente sem armazenar sessão no backend (Pronto para escalar).

## Como Executar Localmente (HSQLDB)

Para rodar em modo desenvolvimento, o banco HSQLDB já sobe automaticamente em memória ao instanciar o app com a URI `jdbc:hsqldb:mem:vaas`:

```bash
# Necessório apenas Java 21+ instalado.
# No Windows execute:
mvnw.cmd quarkus:dev
# No Linux/Mac execute:
./mvnw quarkus:dev
```

A aplicação estará disponível em `http://localhost:8080`.

## Swagger UI / OpenAPI

🔗 **URL**: [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)

## Cobertura de Testes (Jacoco)

A aplicação conta com o plugin Jacoco integrado no ecossistema do Quarkus (`quarkus-jacoco`).

Para rodar os testes e gerar um relatório completo de cobertura (Coverage) do projeto inteiro:
```bash
mvn clean verify
```

Após o build com sucesso, o report interativo em formato HTML estará disponível na pasta:
👉 `target/jacoco-report/index.html`. Você pode abrir este arquivo diretamente no seu navegador ver detalhes da saúde das classes testadas.

## Como Gerar Imagem Docker (RNF-07)

Criamos um `Dockerfile` multi-stage pronto para containerizar o app:

```bash
docker build -t vaas-api .
docker run -i --rm -p 8080:8080 vaas-api
```

### Usando banco PostgreSQL em Produção (Profile `%prod`)

Para inicializar a base de dados de produção, utilize o script disponibilizado em [`src/main/resources/schema.sql`](src/main/resources/schema.sql) para realizar a criação (DDL) das tabelas no seu PostgreSQL.

Quando rodar o binário jar via java ou docker, defina as variáveis de ambiente:

```bash
docker run -e QUARKUS_PROFILE=prod \
           -e DB_URL=jdbc:postgresql://host.docker.internal:5432/vaas \
           -e DB_USER=postgres \
           -e DB_PASSWORD=admin \
           -p 8080:8080 vaas-api
```
