# site-de-eventos
-----

# ⛩️ Xogum - Plataforma de Eventos

Este projeto trata-se de uma plataforma completa para criação e gerenciamento de eventos, desenvolvida em Java com o framework Spring Boot, além disso fizemos uso de HTML, CSS e JavaScript para o front-end. A plataforma permite que os usuários se cadastrem, comprem ingressos para eventos, e também que organizadores criem e gerenciem seus próprios eventos.

O principal objetivo do projeto foi aplicar de forma prática os conceitos aprendidos durante a disciplina de Programação Orientada a Objetos em um projeto robusto e funcional.

**🔗 [Acesse o protótipo inicial no Figma](https://www.figma.com/proto/crUJlQManLxhtRHAQgmPCG/Pr%C3%B3totipo?node-id=3-334&p=f&t=OzuDc1rNtagWhewR-1&scaling=scale-down&content-scaling=responsive&page-id=0%3A1&starting-point-node-id=3%3A334)**

## ✨ Funcionalidades Principais

A plataforma é dividida em dois tipos de interação principais: para **usuários** e para **organizadores**.

#### 👤 Para Usuários:

  - **Cadastro e Autenticação:** Sistema completo de criação de conta, login e logout, com gerenciamento de sessão.
  - **Busca de Eventos:** Visualização de todos os eventos na página inicial com uma barra de busca para filtrar por nome.
  - **Compra de Ingressos:** Fluxo de compra em múltiplas etapas, desde a seleção da quantidade até o preenchimento dos dados dos participantes.
  - **Uso de Cupons:** Possibilidade de aplicar códigos de cupom de desconto no momento da compra.
  - **Visualização e Impressão de Ingressos:** Acesso a uma área "Meus Ingressos" para visualizar, imprimir ou cancelar pedidos.

#### 🚀 Para Organizadores:

  - **Cadastro Diferenciado:** Organizadores podem se cadastrar fornecendo informações adicionais, como CNPJ e conta bancária.
  - **Criação de Eventos:** Formulário completo para criar novos eventos, definindo nome, data, local, preço, capacidade e cupons de desconto.
  - **Gerenciamento de Eventos:** Página dedicada para visualizar eventos criados, acompanhar a quantidade de ingressos vendidos e o status de cada um.
  - **Cancelamento de Eventos:** Permissão para cancelar um evento, o que atualiza automaticamente o status para todos os compradores.

## 🛠️ Como Executar o Projeto

Para executar o projeto localmente, siga os seguintes passos.

#### Pré-requisitos

  * **Java 17** ou superior
  * **Apache Maven**

#### Passos

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/seu-usuario/site_de_eventos.git
    cd site_de_eventos/site-de-eventos
    ```

2.  **Compile o projeto com o Maven:**
    *O Maven irá baixar todas as dependências e empacotar a aplicação.*

    ```bash
    mvn clean install
    ```

3.  **Execute a aplicação:**

    ```bash
    mvn spring-boot:run
    ```

    *Como alternativa, você pode executar o arquivo `.jar` gerado:*

    ```bash
    java -jar target/site-de-eventos-0.0.1-SNAPSHOT.jar
    ```

4.  **Acesse a plataforma**
    Abra seu navegador e acesse: `http://localhost:8080`

## 🧠 Arquitetura e Decisões de Projeto

O projeto foi estruturado para ser modular, extensível e de fácil manutenção, aplicando conceitos com destaque para o padrão arquitetural MVC (Model-View-Controller). O projeto é uma implementação clássica do padrão MVC, que é a base do Spring para aplicações web. Essa arquitetura separa as responsabilidades da aplicação em três componentes interconectados:

Model: Representa a estrutura dos dados e o estado das entidades. São as classes no pacote io.github.site_de_eventos.sitedeeventos.model (ex: `Usuario.java`, `Evento.java`).

View: É a camada de apresentação, responsável por exibir os dados ao usuário. No projeto, são os arquivos *.html na pasta resources/templates, que utilizam o Thymeleaf para renderizar as informações dinamicamente.

Controller: Atua como o intermediário. As classes no pacote ` ...controler` (ex: : ` EventoController.java`) recebem as requisições do usuário, acionam a lógica de negócio nos Services e Models, e selecionam a View apropriada para retornar a resposta.

Service: Esta é uma adição crucial ao MVC tradicional. As classes no pacote ` ...service` (ex: ` EventoService.java`, ` PedidoService.java`) contêm a lógica de negócio da aplicação. Elas são responsáveis por validar dados, aplicar regras, orquestrar operações com um ou mais repositórios e garantir a integridade do sistema.

1. Spring Boot e Maven: A Base do Projeto
   
  * **Gerenciamento de Dependências (Maven):** Em vez de adicionarmos bibliotecas (`.jar`) manualmente, nós as declaramos no `pom.xml`. O Maven se encarrega de baixar as versões corretas e suas dependências, evitando conflitos.

  * **Ciclo de Vida de Construção (Maven):** Comandos como `mvn clean package` orquestram todo o processo de compilação do código, processamento de recursos (HTML, CSS) e empacotamento da aplicação em um único arquivo `.jar` executável.

  * **Inversão de Controle e Injeção de Dependências (Spring Boot):** Este é o coração do Spring. Em vez de nossas classes criarem suas próprias dependências (ex: `new EventoService()`), nós delegamos essa responsabilidade ao Spring. Anotações como `@Autowired`, `@Controller`, `@Service` e `@Repository` permitem que o Spring "injete" as instâncias necessárias automaticamente, desacoplando os componentes e facilitando a manutenção e os testes.

2. Conceitos de Orientação a Objetos (OO)
   
  * **Encapsulamento:** As classes de modelo (`Usuario.java`, `Evento.java`) protegem seus dados (atributos) tornando-os privados e controlando o acesso através de métodos públicos (`getters` e `setters`).

  * **Herança:** A classe `Organizador` **é um** `Usuario`. Portanto, `Organizador.java` herda todos os atributos e métodos de `Usuario.java` e adiciona seus próprios comportamentos, como `cnpj` e `contaBancaria`, promovendo o reuso de código.

  * **Polimorfismo:** No `UsuarioController.java`, ao verificar se um usuário logado pode criar um evento, usamos `instanceof Organizador`. Isso permite que o sistema trate um objeto `Organizador` como um `Usuario` genérico na maior parte do tempo, mas execute um comportamento específico quando necessário.

  * **Abstração:** As classes de modelo abstraem entidades do mundo real. A classe `Evento.java` representa os dados e regras de um evento, sem se preocupar em como será exibida ou salva.


3. Contratos Definidos por Interfaces
As interfaces são um pilar fundamental do projeto, pois definem que as classes devem seguir, promovendo o baixo acoplamento.

  * **Camada de Repositório (`EventoRepository.java`):** A interface define *o que* um repositório deve fazer (ex: `save`, `findById`), mas não *como*. A implementação (`EventoArquivoRepository.java`) contém a lógica específica de manipulação de arquivos JSON.

      * **Relevância:** Se decidirmos trocar a persistência de arquivos JSON para um banco de dados SQL, precisaríamos apenas criar uma nova classe de implementação que segue o mesmo contrato. As camadas de serviço e controle, que dependem da interface, não precisariam de nenhuma alteração.

  * **Padrões de Projeto (`IEventoBuilder`, `ICalculoPrecoPedidoStrategy`):** As interfaces definem os contratos para os padrões Builder e Strategy, garantindo que todas as implementações concretas ofereçam os mesmos métodos, o que torna seu uso consistente.

4. Padrões de Projeto Aplicados

  * **Builder (`EventoBuilderConcreto.java`):**

      * **Problema:** Criar um objeto `Evento` ou `Usuario` com muitos campos opcionais levaria a construtores com muitos parâmetros, tornando o código difícil de ler e manter.
      * **Solução:** O padrão Builder oferece uma API fluente para construir o objeto passo a passo, tornando o código mais legível e a criação do objeto separada de sua representação.

  * **Strategy (`PedidoService.java`):**

      * **Problema:** A lógica de cálculo do preço de um pedido pode ter múltiplas variações (com taxa, com cupom, etc.). Colocar essa lógica em uma série de `if-else` tornaria o código rígido e difícil de estender.
      * **Solução:** A interface `ICalculoPrecoPedidoStrategy` define um contrato para qualquer algoritmo de cálculo. As classes `CalculoComCupomDesconto` e `CalculoComTaxaServico` fornecem implementações específicas. O `PedidoService` simplesmente escolhe e aplica a estratégia necessária sem conhecer os detalhes, tornando o sistema aberto a novas regras de preço.


5. Persistência de Dados em Arquivos JSON

  * **Mecanismo:** Para simplificar o setup, a persistência foi implementada usando arquivos JSON (`eventos.json`, `usuarios.json`), evitando a necessidade de um banco de dados externo.

  * **Implementação:** As classes de repositório (`*RepositoryImpl.java`) usam a biblioteca **Gson** para serializar (Java -\> JSON) e desserializar (JSON -\> Java) os dados. Os dados são mantidos em um `Map` em memória para acesso rápido, e qualquer alteração dispara a reescrita do arquivo, garantindo a persistência.

6. Testes com JUnit e Mockito

  * **Estratégia:** O foco foi em **testes de unidade** para a camada de serviço (`*ServiceTest.java`), onde a lógica de negócio reside.

  * **JUnit 5:** É a ferramenta padrão no ecossistema Java para estruturar os testes (`@Test`) e verificar os resultados (`assertEquals`).

  * **Mockito:** É essencial para **isolar** a classe que está sendo testada. Em `EventoServiceTest.java`, usamos `@Mock` para criar um `EventoRepository` falso. Com `when(...).thenReturn(...)`, instruímos o mock a retornar dados pré-definidos. Assim, testamos a lógica do `EventoService` independentemente de como o repositório realmente funciona.


👨‍💻 Equipe Responsável
Brenno Phelipe Silva dos Santos, Sibele Oliveira Cruz e Silas Santos da Silva.

Observação: Esse projeto trata-se de um atividade acadêmica realizada pelos alunos supracitados do Departamento de Computação (DCOMP) da Universidade Federal de Sergipe para a disciplina de Programação Orientada a Objetos, ministrada pelo Professor Kalil Bispo, no período de 2025.1.
