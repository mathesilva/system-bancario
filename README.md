# 🏦 System Bank - Reconciliação Bancária (Spring Batch)

Este é um projeto de backend focado em processamento de dados em lote (Batch Processing) para conciliação bancária. Ele foi desenvolvido para demonstrar a construção de uma esteira ETL (Extract, Transform, Load) robusta e escalável, utilizando o ecossistema Spring.

##  Tecnologias Utilizadas

* **Java**
* **Spring Boot 3**
* **Spring Batch** (ETL e processamento em chunks)
* **Spring Data JPA / Hibernate**
* **PostgreSQL** (Banco de dados relacional)
* **Docker** (Para conteinerização do banco de dados)
* **Swagger / OpenAPI** (Documentação da API)

##  Arquitetura e Soluções Técnicas

O fluxo de processamento foi desenhado para ler arquivos CSV de alta volumetria, aplicar regras de negócio e persistir os dados no banco.

1.  **Extract (`FlatFileItemReader`):** Leitura segura do arquivo `bank-statements.csv` mapeando as linhas para um DTO de transferência para evitar quebras por formatação incorreta.
2.  **Transform (`ItemProcessor`):** Validação de regras de negócio e conciliação. O sistema verifica se a transação já existe e compara valores e datas, classificando o status como `CONCILIADO`, `DIVERGENTE` ou preparando para um novo `INSERT`.
3.  **Load (`RepositoryItemWriter`):** Gravação otimizada no banco de dados utilizando processamento em blocos (`chunk`).

###  Resolução de Desafio Arquitetural (Ciclo de Vida JPA)
Um dos principais desafios deste projeto foi a injeção de `UUIDs` externos (oriundos do CSV) diretamente na entidade. O comportamento padrão do Hibernate assumia que entidades com IDs preenchidos deveriam sofrer um `UPDATE`, gerando falhas de *Optimistic Locking* (`StaleObjectStateException`).

**Solução:** A entidade `Transacao` foi refatorada para implementar a interface `Persistable<UUID>`, desacoplando geradores automáticos e utilizando uma flag `@Transient` em memória. Isso devolveu o controle total sobre o estado da entidade, permitindo forçar `INSERTs` limpos no banco de dados.

##  Como Executar o Projeto

### Pré-requisitos
* Java 17+ instalado
* Docker e Docker Compose rodando
* Maven

### 1. Subir o Banco de Dados
Na raiz do projeto, execute o comando para subir o container do PostgreSQL:
```bash
docker-compose up -d 
```
### 2. Rodar a aplicacao
Você pode rodar diretamente pela sua IDE ou via terminal:
```bash
./mvnw spring-boot:run
```

### Como testar (Gatilho de Conciliacao):
O Job do Spring Batch está configurado para não rodar automaticamente na inicialização. Para disparar a esteira de processamento, utilize a API REST documentada no Swagger.

```bash
Com a aplicação rodando, acesse a interface do Swagger no navegador:
http://localhost:8080/swagger-ui.html

Expanda o endpoint de conciliação (ex: POST /api/v1/reconciliacao/start).

Clique em Try it out e depois em Execute.

Acompanhe os logs no console da aplicação mostrando as transações sendo processadas e gravadas no banco de dados!
```
### Dev
 - Matheus Faias

