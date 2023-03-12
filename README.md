# BoletoReadWriteProject

Projeto sendo desenvolvido na disciplina de integração de sistemas.

Com o ojetivo de praticar diferentes técnicas de integração entre sistemas, nesse projeto será usado um "hot folder" para que uma aplicação Java possa fazer a escrita de arquivos como remessa de cobrança bancária no layout CNAB240 para Caixa Economica Federal, outra aplicação em "a ser decidido" fará a leitura do arquivo no hot folder.

Futuramente, outras técnicas de integração de sistemas serão utilizadas e incrementos serão gerados ao projeto.

## Tecnologias

<details>
  <summary><b>Clique para expandir</b></summary>
  
  * Java
  * "a ser decidido"
  * Docker
  * Manipulação de arquivos com a técnica hot folder
  * Diversas bibliotecas de código
  
</details>

## Uso

<details>
  <summary><b>Clique para expandir</b></summary>
  
  Após clonar o projeto, inicialize o docker, depois disso é possível rodar o batch run-application.bat para testá-la "automaticamente".

  Alternativamente, também é possível rodar a aplicação seguindo alguns passos:

  - Inicializar docker
  - Executar o script em BoletoReadWriteProject/databaseFiles/createClientesTable.sql
  - Executar o arquivo da aplicação em BoletoReadWriteProject/XLSMtoPostgresDB/app/src/main/java/XLSMtoPostgresDB\App.java
  - to do.....

  É necessário seguir todos os passos para conseguir o resultado desejado, já que diferentes aplicações estarão atuando em conjunto para efetuar ações como persistência de dados, leitura e escrita.
  
</details>
