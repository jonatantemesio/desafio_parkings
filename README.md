# Parking Management System

Este projeto é um sistema de gerenciamento de estacionamento desenvolvido com Spring Boot e MySQL.

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 17
- Maven

## Configuração do Ambiente

### 1. Subir os containers com Docker Compose

Para iniciar os serviços de banco de dados MySQL e phpMyAdmin, execute o seguinte comando na /localdev do projeto:

```bash
docker-compose up -d
```

Isso irá:
- Criar um container MySQL na porta 3306
- Criar um container phpMyAdmin na porta 8080
- Criar um volume para persistência dos dados do MySQL
- Executar o script SQL de inicialização (`db/init.sql`)

### 2. Acessar o phpMyAdmin

Após os containers estarem em execução, você pode acessar o phpMyAdmin em:
[http://localhost:8080](http://localhost:8080)

Credenciais:
- Servidor: `mysql`
- Usuário: `myuser`
- Senha: `mypassword`

### 3. Configuração da Aplicação Spring Boot

A aplicação está configurada com as seguintes propriedades (arquivo `application.properties`):

```properties
# Configurações básicas da aplicação
spring.application.name=parking_management
server.port=8088
spring.main.allow-circular-references=true
spring.main.allow-bean-definition-overriding=true

# Configuração do MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/parkings
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configurações do Hibernate/JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração de encoding
spring.datasource.sql-script-encoding=UTF-8

# Configuração de logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Executando a Aplicação

Para compilar e executar a aplicação Spring Boot:

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em [http://localhost:8088](http://localhost:8088) (porta configurada nas propriedades).

## Estrutura do Projeto

```
parking_management/
├── .idea/               # Configurações do IntelliJ IDEA
├── .mvn/                # Configurações do Maven Wrapper
├── db/
│   └── init.sql         # Script SQL de inicialização do banco
├── src/                 # Código fonte da aplicação
├── target/              # Artefatos gerados pelo build
├── docker-compose.yml   # Configuração dos containers Docker
├── pom.xml              # Configuração do Maven
└── README.md            # Este arquivo
```

## Configurações Importantes

### Banco de Dados
- A aplicação usa MySQL na porta 3306
- O banco de dados é inicializado com o script `db/init.sql`
- Configuração JPA está definida para `update` (atualiza o schema automaticamente)

### Logging
- SQL gerado pelo Hibernate é exibido no console (nível DEBUG)
- Os parâmetros das queries são exibidos (nível TRACE)

### Portas
- Aplicação Spring Boot: 8088
- phpMyAdmin: 8080
- MySQL: 3306

## Dependências Principais

- Spring Boot 3.4.4
- Spring Web MVC
- Spring Data JPA
- MySQL Connector
- Lombok
- Dozer Mapper
- H2 Database (para testes)
- Spring Validation

## Comandos Úteis

- Parar os containers:
  ```bash
  docker-compose down
  ```

- Rebuildar e reiniciar os containers:
  ```bash
  docker-compose up -d --build
  ```

- Visualizar logs dos containers:
  ```bash
  docker-compose logs -f
  ```

- Limpar e reconstruir o projeto:
  ```bash
  mvn clean install
  ```

## Documentação da API (Swagger)
 - A aplicação expõe uma documentação interativa da API utilizando o Swagger.
  - Após iniciar a aplicação, você pode acessar a documentação em: http://localhost:8088/swagger-ui/index.html

## Observações

- O script `init.sql` na pasta `db` será executado automaticamente quando o container MySQL for iniciado pela primeira vez.
- Os dados do MySQL são persistidos em um volume Docker chamado `mysql_data`.
- A aplicação está configurada para permitir referências circulares e sobrescrita de beans.
- O logging detalhado do Hibernate ajuda no desenvolvimento, mas pode ser reduzido em produção.
