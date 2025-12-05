# CarteiraClojure: Gerenciador de Carteira de Ações

## Descrição do Projeto

**CarteiraClojure** é uma aplicação para gerenciamento de carteira de ações desenvolvida como projeto final para a disciplina de Programação Funcional na Unifor. A aplicação é dividida em duas partes principais: um servidor API (backend) e um cliente de linha de comando (frontend).

---

## Arquitetura

O projeto adota uma arquitetura de duas camadas, ambas desenvolvidas em **Clojure**:

1.  **`backend` (Servidor API)**
    * Desenvolvido com **Ring** e **Compojure**.
    * Responsável por gerenciar o estado da carteira (registro de operações e saldo de ações).
    * Integra-se com a API **Alpha Vantage** para obter cotações atuais (`GLOBAL_QUOTE`) e históricas (`TIME_SERIES_DAILY`).
    * Disponibiliza rotas HTTP para todas as operações de compra, venda, extrato e consulta de saldo.

2.  **`carteirafront` (Cliente CLI)**
    * Aplicação em linha de comando (CLI).
    * Atua como interface para o usuário, interagindo com o `backend` através de requisições HTTP.
    * Possui um menu interativo para execução das operações.

---

## Pré-requisitos

Para executar a aplicação, você deve ter instalado:

1.  **Java Runtime Environment (JRE)** (Recomendado Java 8 ou superior).
2.  **Leiningen** (versão 2.0.0 ou superior).

---

## Configuração

A aplicação requer variáveis de ambiente para a chave de API e para a comunicação entre o frontend e o backend.

1.  Crie um arquivo chamado `.env` na raiz do projeto com base no `.env.example`:


2.  Edite o arquivo `.env` com os seguintes valores:

    ```ini
    ALPHAVANTAGE_API_KEY=sua_chave_aqui
    SUFIXO=(.SAO para consultar B3, vazio para global)
    BACKEND_URL=http://localhost:3000
    ```
    * A chave `ALPHAVANTAGE_API_KEY` é usada pelo backend para consultas externas.
    * A chave `BACKEND_URL` é usada pelo frontend para se conectar ao backend.

---

## Como Executar

A aplicação deve ser iniciada em duas etapas: primeiro o servidor backend, e depois o cliente frontend.

### 1. Iniciar o Backend

Navegue até o diretório `backend` e inicie o servidor:

```bash
cd backend
lein ring server
```
O servidor será iniciado e ficará disponível no endereço configurado em BACKEND_URL (padrão: http://localhost:3000).
2. Iniciar o Frontend (Cliente CLI)

Em uma nova janela do terminal, navegue até o diretório carteirafront e execute a aplicação

```bash
cd ../carteirafront
lein run app
```

O cliente CLI iniciará o Menu Principal, a partir do qual você pode interagir com o sistema

---

## Funcionalidades

O cliente CLI fornece um menu interativo com as seguintes opções:

| Opção | Menu | Função |
| :---: | :------------- | :-------- |
| **1** | Consultar acao | Consulta as cotações atuais de abertura, alta, baixa, preço e fechamento de uma ação, usando `requestConsultaAcao`. |
| **2** | Comprar acao | Registra uma operação de compra de uma quantidade de ações em uma data histórica específica, usando `requestCompraAcao`. |
| **3** | Vender acao | Registra uma operação de venda em uma data histórica, validando a quantidade disponível em carteira, usando `requestVendeAcao`. |
| **4** | Exibir extrato | Exibe o histórico completo de todas as operações de compra e venda registradas (período fixo "2020-01-01" a "2030-12-31"), usando `requestExtrato`. |
| **5** | Exibir extrato por periodo | Exibe o histórico de operações filtrado por uma data de início e fim fornecida pelo usuário, usando `requestExtrato`. |
| **6** | Exibir saldo | Exibe o saldo atual da carteira, mostrando a quantidade total de cada ação possuída, usando `requestSaldo`. |

---

## Licença

Esta aplicação está licenciada sob os termos da Eclipse Public License 2.0 (EPL-2.0).

O código-fonte pode, adicionalmente, ser disponibilizado sob as seguintes licenças secundárias, se as condições da EPL-2.0 forem satisfeitas: GNU General Public License, Versão 2 (ou posterior), com a GNU Classpath Exception.
