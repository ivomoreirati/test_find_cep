# Projeto Busca de CEP

A aplicação basicamente expôe um endpoint "/v1/address" para busca de endereço através do CEP.
Obs: Para facilitar a interação com o contrato, disponibilizei via swagger no endereço:

http://localhost:8090/netshoes/cep/swagger-ui.html 

Method POST - localhost:8090/netshoes/cep/v1/address

Disposição dos seguintes HttpStatus de resposta: 201, 400, 404, 502

HTTP STATUS 201 - Request Body:
```json
{
  "cep": "04313001"
}
```
Response:
```
{
  "data": {
    "bairro": "Vila Guarani",
    "localidade": "São Paulo",
    "logradouro": "Avenida Leonardo da Vinci",
    "uf": "SP"
  }
}
```
HTTP STATUS 400 - Request Body:
```
{
  "cep": "0431300"
}
```
Response:
```
{
  "error": "Bad Request",
  "message": "Cep inválido",
  "path": "/netshoes/cep/v1/address",
  "status": 400,
  "timestamp": "2020-06-17T21:00:13.086Z"
}
```
HTTP STATUS 404 - Request Body:
```
{
  "cep": "11111111"
}
```
Response:
```
{
  "error": "Not Found",
  "message": "Cep não encontrado",
  "path": "/netshoes/cep/v1/address",
  "status": 404,
  "timestamp": "2020-06-17T21:00:13.086Z"
}
```
HTTP STATUS 502 - Request Body:
```
{
  "cep": "11111111"
}
```
Response:
```
{
  "error": "Bad Gateway",
  "message": "Servico externo de busca de cep está indisponivel!",
  "path": "/netshoes/cep/v1/address",
  "status": 404,
  "timestamp": "2020-06-17T21:00:13.086Z"
}
```
Validações:

Foi considerado um cep inválido com as seguintes situacoes:
Campo cep diferente de 8 caracteres. Ex: "0431300"
Campo cep não numerico. Ex: "04313-01"
Campo cep igual a zero. Ex: "00000000"
Campo cep igual a nulo ou vazio. Ex: "" ou null

Tentativa de encontrar um cep existente possível:

Tentativas de busca de um endereco a partir de um cep válido, substituindo com valor zero da direita para esquerda:
Ex: "42720111"..."42720110"..."42720100"..."42720000"(Endereço encontrado)

    1. Arquitetura MVC

Utilizei então a organização MVC, sendo dividido em controllers, services, clients, presenters e parameters, na sua essência. Para uma melhor organização do código e manutenção facilitada.

    2. Utilização do lombok

Também utilizei o lombok para deixar o código mais fácil de dar manutenção, através de suas features, e atendendo também o conceito de cleanCode.

    3. Utilização de interceptor e padronização de erros

Foi criado um objeto chamado "ErrorPresenter" para padronizar a saída de erros existentes e tratados. Utilizando uma classe interceptadora chamada "HandlerController", ou seja, interceptando cada Exception lançada e montado o erro customizado.

    4. Utilização de validations no contrato

Foi-se definido algumas validações prévias no contrato, como @Notnull, @NotBlank e @Size(Min=8 e Max=8), utilizando das constraints existentes no framework.

    5. Health Check

Configurei o serviço para deixar exposto algumas informações para monitoramento posterior (Health Check) através do framework Actuator do Spring.

    6. Utilização do feignClient

Uma biblioteca do NetFlix que nos facilita na hora da implementação dos clients externos, abstraindo a implementação dos métodos, somente passando poucas informações, por exemplo, passando simplesmente a url fim. Poderíamos também ativar a função Eureka para balanceamento de carga, além de se responsabilizar a descoberta do serviço disponível na cloud, mas não seria o nosso caso. 

    7. Utilização do mockMvc

Com a utilização do mockMvc, nos facilita na hora de testar os controllers, sendo assim é possível fazermos testes de integração.

    8. Utilização de mock para teste de unidades

Com dados mockados, temos a possibilidade de simular situações reais, sem chamarmos o serviço externo, por exemplo.

    9. Utilização de recursividade no metodo de tentativas de encontrar um resultado 

Para retorno rápido e situação de baixa complexidade, a recursividade seria possível neste ponto, pois trata-se de uma situacao controlada onde tenho o maximo de 8 tentativas, por conta do tamanho de caracteres do CEP, utilizando-se do clean code e elegância no codigo


    10.  Dockerizando aplicação
```
mvn clean install
docker build -f Dockerfile -t netshoes_test_find_cep .
docker run -p 8090:8090 -t netshoes_test_find_cep
```
