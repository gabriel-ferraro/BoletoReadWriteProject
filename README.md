# BoletoReadWriteProject

Projeto desenvolvido na disciplina de integração de sistemas.

Com o ojetivo de praticar diferentes técnicas de integração entre sistemas, nesse projeto serão usados:

<b>[hot folder](https://www.ibm.com/docs/en/ahts/4.0?topic=folders-setting-up-hot)</b> para que uma aplicação Java possa fazer a escrita de arquivos como remessa de cobrança bancária no layout CNAB240 para Caixa Economica Federal, outra aplicação em Node fará a leitura do arquivo no hot folder.

<b>[API Rest](https://www.redhat.com/pt-br/topics/api/what-is-a-rest-api)</b> documentada nos padrões da OpenAPI, para que seja possível gerar uma interface de consumo dos serviços da API, que deverá processar o pagamento caso o valor enviado seja exatamente o valor da dívida do cliente.

## Tecnologias

<details>
  <summary><b>Clique para expandir</b></summary>
  
  * Java
  * Node
  * Docker
  * Manipulação de arquivos com a técnica hot folder
  * API rest com especificação Open API
  * Diversas bibliotecas
  
</details>

## Uso

<details>
  <summary><b>Clique para expandir</b></summary>
  
  - Após clonar o projeto, inicialize o docker, depois execute o comando docker compose up --build no diretório raiz do projeto;
  - Rode o arquivo [run.bat](run.bat)

  Alternativamente:
  - Execute a aplicação [XLSMtoPostgresDB](/XLSMtoPostgresDB/app/src/main/java/XLSMtoPostgresDB/App.java) para persistir os dados de escrita na base de dados postgres;
  - Execute a aplicação [BoletoWriterJAVA](/boletoWriterJAVA/app/src/main/java/boletoWriterJAVA/App.java) para obter os registros da base de dados como remessas cnab240 no [hotFolder](hotFolder)
  
  É necessário seguir todos os passos para conseguir o resultado desejado, já que diferentes aplicações estarão atuando em conjunto para efetuar ações como persistência de dados, leitura e escrita.
  
</details>
