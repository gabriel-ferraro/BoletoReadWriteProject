# BoletoReadWriteProject

Projeto sendo desenvolvido na disciplina de integração de sistemas.

Com o ojetivo de praticar diferentes técnicas de integração entre sistemas, nesse projeto será usado um [hot folder](https://www.ibm.com/docs/en/ahts/4.0?topic=folders-setting-up-hot) para que uma aplicação Java possa fazer a escrita de arquivos como remessa de cobrança bancária no layout CNAB240 para Caixa Economica Federal, outra aplicação em Node fará a leitura do arquivo no hot folder.

Futuramente outras técnicas de integração de sistemas serão utilizadas, incrementando o projeto.

## Tecnologias

<details>
  <summary><b>Clique para expandir</b></summary>
  
  * Java
  * Node
  * Docker
  * Manipulação de arquivos com a técnica hot folder
  * Diversas bibliotecas de código
  
</details>

## Uso

<details>
  <summary><b>Clique para expandir</b></summary>
  
  Após clonar o projeto, inicialize o docker, depois disso é possível rodar o batch [run-application.bat](./run-application.bat) para testá-la "automaticamente".

  Alternativamente, também é possível rodar a aplicação seguindo alguns passos:

  - Inicializar docker
  - A partir do diretório raiz desse projeto, rodar no terminal o comando: docker compose up -d
  - to do.....

  É necessário seguir todos os passos para conseguir o resultado desejado, já que diferentes aplicações estarão atuando em conjunto para efetuar ações como persistência de dados, leitura e escrita.
  
</details>
