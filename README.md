# 📚 Biblioteca Virtual

Projeto desenvolvido em **Java** e **Spring** para simular o funcionamento de um sistema de **biblioteca virtual**.

---

## 📝 Descrição

Este projeto tem como objetivo simular o funcionamento de uma biblioteca virtual, onde:

* **Clientes** podem realizar empréstimos de livros.
* **Administradores** são responsáveis pelo gerenciamento do acervo.

---

## 🏛 Arquitetura

Foi utilizada a **Arquitetura Hexagonal**, permitindo aprofundar conhecimentos em padrões arquiteturais e garantir uma aplicação mais organizada, escalável e de fácil manutenção.

---

## 🗄 Banco de Dados

* Banco relacional: **PostgreSQL**
* Persistência: **JPA**
* Versionamento de banco: **Flyway**, garantindo migrações seguras e rastreáveis.

---

## 🔐 Autenticação e Autorização

O sistema utiliza **Spring Security** com **tokens JWT**, garantindo:

* Autenticação segura
* Controle de acesso
* Operações de login protegidas

---

## 🧾 Documentação (Swagger)

Para documentação da API, foi utilizado o **Swagger**, possibilitando visualizar e testar os endpoints de forma simples.

Acesse pelo navegador:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📧 Envio de E-mails

A aplicação conta com envio automático de e-mails nas seguintes ações:

* Cadastro de clientes
* Realização de empréstimos
* Devolução de livros

---

## 🔄 CI/CD

O projeto utiliza um arquivo simples de **CI/CD**, onde a cada **push** na branch `main` ou abertura de **Pull Request**, o pipeline executa:

```
mvn -B package --file pom.xml
```

Isso garante que novas atualizações não quebrem o código existente.

---

## ⚙️ Perfis de Execução

A aplicação conta com diferentes perfis:

### **Dev**

* Ambiente local
* Todos os serviços devem ser executados manualmente

### **Prod**

* Configurado para rodar com **Docker**
* Com as variáveis de ambiente corretas no arquivo `.env`, basta executar:

```
docker-compose up
```

---
