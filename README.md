# Documentação: MuscleBuilder API

## 1\. Visão Geral

MuscleBuilder API é o backend para uma aplicação de acompanhamento de treinos. O projeto foi desenvolvido para fornecer uma solução robusta e intuitiva que ajuda tanto iniciantes quanto pessoas experientes a registrar, gerenciar e visualizar seu progresso nos treinos. Para manter os usuários engajados, a aplicação incorpora elementos de gamificação, transformando a jornada de treinos em uma experiência mais motivadora.

Este backend foi construído com Java e o ecossistema Spring, expondo uma API RESTful completa para ser consumida por qualquer cliente front-end.

### ✨ Core Features

  * **Gerenciamento de Usuários:** Sistema completo de registro, login e gerenciamento de perfis de usuário.
  * **Biblioteca de Exercícios:** Um catálogo de exercícios que podem ser filtrados por grupo muscular, equipamento, etc.
  * **Criação de Treinos Personalizados:** Permite que os usuários criem seus próprios templates de treino, adicionando exercícios e especificando séries, repetições e cargas.
  * **Registro de Atividades (Workout Logging):** Funcionalidade para que os usuários registrem em tempo real os treinos que realizam, capturando dados detalhados de cada exercício.
  * **Acompanhamento de Progresso:** Endpoints que fornecem dados para a visualização do progresso ao longo do tempo, como frequência de treinos e evolução de cargas.
  * **Gamificação:** Sistema inicial de conquistas e acompanhamento de sequências de treinos para aumentar a motivação do usuário.

## 2\. Tech Stack & Arquitetura

### 2.1. Tecnologias Principais

| Categoria                | Tecnologia                                                              |
| ------------------------ | ----------------------------------------------------------------------- |
| **Linguagem & Framework** | Java 17, Spring Boot 3+                                                 |
| **APIs** | Spring Web (RESTful)                                                    |
| **Persistência de Dados** | Spring Data JPA, Hibernate                                              |
| **Banco de Dados** | PostgreSQL (Produção), H2 (Desenvolvimento/Testes)                      |
| **Migrações de Schema** | Flyway                                                                  |
| **Segurança** | Spring Security                                                         |
| **Build & Dependências** | Maven                                                                   |
| **Testes** | JUnit 5, Mockito, MockMvc                                               |

### 2.2. Visão Geral da Arquitetura

Para garantir a manutenibilidade e uma clara separação de responsabilidades, o projeto segue uma **arquitetura de três camadas**:

1.  **`Controller` (Camada de Apresentação):** Responsável por expor os endpoints da API REST. Esta camada recebe as requisições HTTP, valida os dados de entrada através de DTOs (`Data Transfer Objects`) e delega a execução para a camada de Serviço.

2.  **`Service` (Camada de Negócios):** Onde reside a lógica de negócios central da aplicação. Os serviços orquestram as operações, implementam as regras de negócio e coordenam a interação entre os diferentes repositórios.

3.  **`Repository` (Camada de Acesso a Dados):** Abstrai a comunicação com o banco de dados. Utilizando as facilidades do Spring Data JPA, esta camada é composta por interfaces que definem as operações de CRUD e consultas personalizadas.

Para a **segurança**, a abordagem inicial utiliza um sistema de **autenticação baseado em sessão**, gerenciado pelo Spring Security. Essa escolha simplifica a configuração inicial e o gerenciamento de estado de autenticação no servidor, oferecendo um mecanismo de segurança robusto e bem estabelecido.

## 3\. Guia de Instalação e Execução

### 3.1. Pré-requisitos

  * JDK 17 ou superior
  * Apache Maven 3.8+
  * Git

### 3.2. Passos para Execução Local

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/marcossbento/musclebuilder.git
    cd musclebuilder
    ```

2.  **Configuração de Ambiente:**
    A aplicação utiliza perfis do Spring para diferentes ambientes. O perfil padrão é o `dev`.

      * **Desenvolvimento (`dev`):** Utiliza um banco de dados **H2 em memória**. Nenhuma configuração adicional é necessária. O console do H2 fica disponível em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:devdb`, User: `sa`, Password: em branco).
      * **Produção (`prod`):** Configurado para usar **PostgreSQL**. As credenciais devem ser ajustadas no arquivo `src/main/resources/application-prod.properties`.

3.  **Execute a aplicação com Maven:**

    ```bash
    mvn spring-boot:run
    ```

    A API estará em execução e acessível em `http://localhost:8080`.

## 4\. Estrutura do Banco de Dados

O versionamento do esquema do banco de dados é gerenciado pelo **Flyway**. Os scripts de migração estão localizados em `src/main/resources/db/migration`.

  * `V1__initial_schema.sql`: Cria as tabelas fundamentais: `users`, `exercises`, `workouts`, e a tabela de associação `workout_exercises`.
  * `V2__add_indices.sql`: Otimiza a performance adicionando índices em colunas frequentemente consultadas.
  * `V3__create_workout_exercise_logs.sql`: Adiciona as tabelas `workout_logs` e `exercise_logs`, essenciais para a funcionalidade de registro de treinos.

## 5\. Documentação da API (Endpoints)

A seguir, a descrição dos principais endpoints agrupados por recurso.

-----

### 5.1. Autenticação

  * **Endpoint:** `/api/login`
  * **Método:** `POST`
  * **Controlador:** `AuthenticationController`
  * **Descrição:** Autentica um usuário com base em e-mail e senha. Em caso de sucesso, cria uma sessão HTTP e retorna um status `200 OK`.
  * **Request Body:** `LoginRequest`

-----

### 5.2. Usuários

  * **Endpoint Base:** `/api/users`
  * **Controlador:** `UserController`

| Método  | Path                  | Descrição                                                              | Request Body         |
| :------ | :-------------------- | :--------------------------------------------------------------------- | :------------------- |
| **POST** | `/register`           | Registra um novo usuário no sistema.                                   | `UserRegistrationDTO` |
| **GET** | `/{id}`               | Retorna os detalhes de um usuário específico pelo seu ID.              | -                    |
| **PUT** | `/{id}`               | Atualiza as informações de perfil de um usuário.                       | `UserDTO`            |
| **PATCH** | `/{id}/email`         | Atualiza o e-mail de um usuário. Requer a senha atual para confirmação. | `EmailUpdateDTO`     |
| **PATCH** | `/{id}/password`      | Atualiza a senha de um usuário.                                        | `PasswordUpdateDTO`  |
| **DELETE**| `/{id}`               | Remove um usuário do sistema.                                          | -                    |

-----

### 5.3. Exercícios

  * **Endpoint Base:** `/api/exercises`
  * **Controlador:** `ExerciseController`

| Método  | Path                           | Descrição                                                        | Request Body    |
| :------ | :----------------------------- | :--------------------------------------------------------------- | :-------------- |
| **POST** | `/`                            | Cria um novo exercício na base de dados (rota administrativa).     | `ExerciseDTO`   |
| **GET** | `/`                            | Retorna uma lista de todos os exercícios disponíveis.            | -               |
| **GET** | `/{id}`                        | Busca um exercício específico pelo seu ID.                       | -               |
| **GET** | `/muscle-group/{muscleGroup}`  | Retorna todos os exercícios de um determinado grupo muscular.    | -               |
| **GET** | `/search?name={name}`          | Busca por exercícios cujo nome corresponda ao termo pesquisado.    | -               |
| **PUT** | `/{id}`                        | Atualiza os dados de um exercício existente.                       | `ExerciseDTO`   |
| **DELETE**| `/{id}`                        | Remove um exercício da base de dados.                              | -               |

-----

### 5.4. Templates de Treino

  * **Endpoint Base:** `/api/workouts`
  * **Controlador:** `WorkoutController`

| Método  | Path    | Descrição                                                    | Request Body |
| :------ | :------ | :----------------------------------------------------------- | :----------- |
| **POST** | `/`     | Cria um novo template de treino para o usuário autenticado.    | `WorkoutDTO` |
| **GET** | `/`     | Lista todos os templates de treino do usuário autenticado.   | -            |
| **GET** | `/{id}` | Busca um template de treino específico pelo seu ID.            | -            |
| **PUT** | `/{id}` | Atualiza um template de treino existente.                    | `WorkoutDTO` |
| **DELETE**| `/{id}` | Remove um template de treino.                                | -            |

-----

### 5.5. Registro de Treinos Realizados

  * **Endpoint Base:** `/api/workout-logs`
  * **Controlador:** `WorkoutLogController`

| Método  | Path                  | Descrição                                                                         | Request Body          |
| :------ | :-------------------- | :-------------------------------------------------------------------------------- | :-------------------- |
| **POST** | `/start`              | Inicia um novo registro de treino (cria um `WorkoutLog` com status "IN\_PROGRESS"). | `StartWorkoutRequest` |
| **POST** | `/{logId}/exercises`  | Adiciona um exercício realizado a um registro de treino em andamento.             | `LogExerciseRequest`  |
| **POST** | `/{logId}/complete`   | Finaliza um treino, mudando seu status para "COMPLETED".                          | -                     |
| **GET** | `/`                   | Lista o histórico de todos os treinos já realizados pelo usuário.                 | -                     |
| **GET** | `/{logId}`            | Retorna os detalhes de um registro de treino específico.                          | -                     |

## 6\. Testes

O projeto adota uma estratégia de testes pragmática para garantir a qualidade e a robustez das funcionalidades críticas:

  * **Testes Unitários:** Focados na camada de **Serviço**, validam a lógica de negócios de forma isolada. Utilizam **Mockito** para mockar as dependências externas, como os repositórios, permitindo testar as regras de negócio sem a necessidade de um banco de dados.

      * Ex: `UserServiceTest.java`

  * **Testes de Integração:**

      * **Camada de Repositório:** Usam a anotação `@DataJpaTest` para testar as interfaces do Spring Data JPA contra o banco de dados **H2**. Isso garante que as consultas personalizadas (`@Query`) funcionem como esperado.
          * Ex: `WorkoutLogRepositoryTest.java`
      * **Camada de Controller:** Utilizam `MockMvc` para simular requisições HTTP aos endpoints da API. Esses testes verificam todo o fluxo, desde a requisição até a resposta, incluindo validações de DTOs, tratamento de exceções e a correta aplicação das regras de segurança.
          * Ex: `AuthenticationControllerTest.java`
