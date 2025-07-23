# Sistema de Gestão Penitenciária ( SIGEPEN )

<!-- See https://ileriayo.github.io/markdown-badges/#usage -->
![Mantainer](https://img.shields.io/badge/manteiner-SESP-%23ED8B00.svg?style=for-the-badge&l&color=green)
![Client](https://img.shields.io/badge/cliente-policia%20penal-%23ED8B00.svg?style=for-the-badge&color=blue)

![Docker](https://img.shields.io/badge/docker-20.10+-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/docker%20Compose-1.27+-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Apache Maven](https://img.shields.io/badge/maven-3.6.3+-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Java](https://img.shields.io/badge/java-8-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

<!-- Breve descrição do projeto. -->
O sistema de gestão penitenciária tem como objetivo controlar e gerenciar todas as  penitenciárias do estado de Mato Grosso.

## Pré-requisitos

<!-- Pré-requisitos para execução do projeto. -->
Antes de começar, certifique-se de que seu ambiente está [configurado](https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/wiki-gesis-how-to/-/wikis/Acessar_rede_SESP%5BVPN%5D) e com a conexão estabelecida com a **VPN** da SESP.

### Tecnologias e Ferramentas

<!-- Descreva as ferramentas utilizadas para o desenvolvimento da aplicação e como preparar o ambiente de local. -->

#### Eclipse

1. Acesse `Window` > `Preferences` > `Java` > `Installed JREs`.
2. Clique em `Add...`, escolha `Standard VM` e configure o caminho para o diretório de instalação do `JDK 8`.
3. Defina o JDK 8 como padrão selecionando-o e clicando em `Apply and Close`.
4. Certifique-se de que o projeto está configurado para utilizar o JDK 8: clique com o botão direito no projeto > `Properties` > `Java Build Path` > `Libraries`, e adicione o `JDK 8`.

#### IntelliJ

1. Acesse `File` > `Project Structure` > `Project`.
2. Em `Project SDK`, selecione o `JDK 8`. Caso não esteja listado, clique em `New...` e adicione o caminho de instalação do `JDK 8`.
3. Em `Project language level`, selecione `8 - Lambdas, type annotations, etc`.

## Integrações

<!-- Descreva as integrações que o sistema possui. -->

## Detalhes dos ambientes

<!-- Descreva informações importantes dos ambientes. (Desenvolvimento, homologação e produção) -->

## Construção e Execução

<!-- Descreva o passo a passo de como baixar e executar o projeto localmente com todas as suas dependências. -->

### Clone o repositório

Clone o repositório **SIGEPEN** para sua máquina local:

```bash
git clone https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/source/sigepen.git

cd sigepen
```

### Limpeza do ambiente

Limpe a construção existente e crie o pacote do projeto:

```bash
mvn clean package
```

Este comando irá:

- Baixar as dependências do Maven especificadas no `pom.xml`.
- Compilar o código-fonte.
- Criar o arquivo `.war` no diretório `target/`.

### Executar Docker Compose

Para rodar o projeto em contêineres, utilize o Docker Compose com o seguinte comando:

```bash
docker compose up -d --build
```

Esse comando irá:

- Construir as imagens Docker definidas no `Dockerfile-dev`.
- Subir os serviços definidos no `docker-compose.yml` em segundo plano (-d).

### Verificar os contêineres
Verifique se os contêineres estão rodando corretamente:

```bash
docker container ls
```

### 5. Parar os contêineres
Para parar e remover os contêineres, execute o seguinte comando:

```bash
docker compose down
```

## Debug Remoto

Para realizar o debug remoto da aplicação Wildfly rodando no contêiner Docker, siga os passos abaixo:

### Expondo a porta de debug no Docker Compose

A porta de debug padrão do Wildfly é a **8787** e já está exposta no `docker-compose.yml`.

### Configurando o Remote Debug no IntelliJ ou Eclipse

#### IntelliJ IDEA

1. Vá em **Run** > **Edit Configurations**.
2. Clique em **+** e selecione **Remote**.
3. Dê um nome para a configuração (por exemplo, `Wildfly Debug`).
4. No campo **Host**, coloque `localhost`.
5. No campo **Port**, coloque **8787**.
6. A configuração de **Command line arguments for remote JVM** deve ser: `-agentlib
   =transport=dt_socket,server=y,suspend=n,address=8787`
7. Clique em **OK** para salvar e iniciar a sessão de debug.

#### Eclipse

1. Vá em **Run** > **Debug Configurations**.
2. Clique com o botão direito em **Remote Java Application** e selecione **New Configuration**.
3. No campo **Project**, escolha o projeto **SIGEPEN**.
4. No campo **Host**, coloque `localhost`.
5. No campo **Port**, coloque **8787**.
6. Clique em **Apply** e depois em **Debug**.

> **Atenção** \
> Certifique-se de que as portas definidas no `docker-compose.yml` estão livres em sua máquina. \
> Sempre que houver alterações no código, execute novamente o `mvn clean package` e o `docker-compose up -d --build` para aplicar as mudanças.

## Referências

<!-- Adicione links de documentações utilizadas para a implementação do projeto ou de configuração do ambiente. -->
- [Documentação do Sistema](https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/wiki-docs/sigepen)
- [Acesso a rede SESP ( VPN )](https://gitlab.sesp.mt.gov.br/sesp/sti/sistemas/wiki-gesis-how-to/-/wikis/Acessar_rede_SESP%5BVPN%5D)
