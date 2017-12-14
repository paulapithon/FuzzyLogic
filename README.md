# Android Fuzzy Integration
Esse projeto visa utilizar a biblioteca [Fuzzy Lite](https://www.fuzzylite.com/) em uma aplicação Android para implementação de um sistema Fuzzy que irá prever o tempo de parada das estações da fábrica da FCA, localizada em Recife, PE.

*Projeto desenvolvido para a cadeira de **Inteligência Artificial** (2017.2) da **Universidade de Pernambuco.***

## Funcionamento
O projeto consiste de duas entradas: o `problema` que gerou a parada da estação e quantas peças há no `buffer` entre essa estação parada e a próxima em movimento. Como saída, o sistema fuzzy vai sugerir uma `atividade` para ser realizada em determinado tempo, e qual a porcentagem dessa atividade ser executada com sucesso.

![alt text](https://i.imgur.com/r1bBwAL.png) ![alt text](https://i.imgur.com/kroVNqy.png) ![alt text](https://i.imgur.com/fN8XYFf.png)
