# 📚 Biblioteca Virtual  

Projeto desenvolvido em **Java** e **Spring** para simular o funcionamento de um sistema de **biblioteca virtual**.  

## 📝 Descrição  
Este projeto tem como objetivo simular o funcionamento de uma biblioteca virtual, onde:  
- **Clientes** podem realizar empréstimos de livros.  
- **Administradores** são responsáveis pelo gerenciamento do acervo.  

## 🏛 Arquitetura  
Optei por utilizar a **Arquitetura Hexagonal**, o que me permite aprofundar meus conhecimentos em padrões arquiteturais e garantir uma aplicação mais organizada, escalável e de fácil manutenção.  

## 🗄 Banco de Dados  
- Banco relacional: **PostgreSQL**  
- Persistência: **JPA**  

## 🔐 Autenticação e Autorização  
Foi utilizado o **Spring Security** com **tokens JWT**, garantindo segurança nas operações de login, autenticação e controle de acesso.  

## ⚙️ Perfis de Execução  
O projeto conta com diferentes perfis de execução:  
- **Dev**: ambiente local, onde todos os serviços devem estar rodando manualmente.  
- **Prod**: configurado para rodar com **Docker**, facilitando a execução da aplicação. Com as variáveis de ambiente dentro do .env configuradas corretamente, basta executar:  

```bash
docker-compose up
