Instruções para rodar o programa

Já no diretório do arquivo ProjetoSistemasOperacionais.java (src>projso>ProjetoSistemasOperacionais.java), é necessário ter um arquivo chamado "arquivo.txt" na qual você inserirá as entradas dos processos no seguinte formato:
• A primeira palavra mostra o nome do processo (PID);
• A segunda palavra mostra a duração do processo;
• A terceira palavra mostra o instante de chegada do processo;
• Caso haja a quarta palavra, esta indicará se o processo realiza operações de I/O;

Exemplo: 
P1 9 10 2,4,6,8
P2 10 4 5
P3 5 0 2
P4 7 1 3,6
P5 2 17

Você pode optar por rodar dentro do Apache Netbeans IDE 13 ou pelo terminal. Caso escolha pelo terminal, você pode escolher o valor de quantum apenas inserindo um número como parâmetro (caso não insira nenhum parâmetro, terá como valor padrão o número 4), veja o exemplo:

para rodar pelo terminal rode os seguintes comandos:
- javac *.java
- java ProjetoSistemasOperacionais.java 5

Esse "5" é o parâmetro que define o valor de quantum, certifique-se de substituir para o valor desejado.

caso não encontre o main, rode os seguintes comandos:
- javac *.java
- java *.java


para rodar pela IDE:
- abra o projeto
- clique com o botão direito do mouse em cima do arquivo ProjetoSistemasOperacionais.java
- clique em Run File (Shift + F6)

em seguida deverá ser gerado o arquivo "saida.txt" e começar a simulação pelo terminal
