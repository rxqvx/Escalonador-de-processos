package projso;

import java.io.FileReader; // objeto para ler caracteres de um arquivo de texto
import java.io.BufferedReader;//objeto para leitura do  arquivo de entrada com buffer que resulta numa leitura mais eficiente
import java.io.BufferedWriter;// objeto para escrita de arquivos com buffer
import java.io.File;// objeto para criação de um arquivo na parte de leitura ou escrita de um texto
import java.io.FileWriter;// objeto para escrita em arquivos
import java.io.IOException;// exceção usado em aulas
import java.util.Queue;// biblioteca de fila para facilitar o processo de desenvolvimento contendo os metodos do fifo, ao remover o primeiro a ser inserido será o primeiro a ser removido
import java.util.LinkedList;// biblioteca necessária para a declaração de queues, segue a politica do fifo sendo eficiente na adição e remoção de elementos no início ou no final da fila, eu poderia usar o arraydeque por ter um desempenho semelhante
import java.util.ArrayList;//biblioteca apenas para armazenar os tempos de operações IO de um processo, facilita pois contem os metodos de um array

public class ProjetoSistemasOperacionais {

    static class File_writer {

        private String filePath;

        public File_writer(String filePath) {
            this.filePath = filePath;
        }

        public void add(String text, boolean isBreakLine) throws IOException {
            try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                if (isBreakLine) {
                    writer.write(text + "\n");

                } else {
                    writer.write(text);

                }
            }
        }

        public void clear() throws IOException {
            try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Não escrever nada no arquivo irá limpar o conteúdo existente.
            }
        }
    }

    static class Processo {

        String pid;
        int duracao, chegada, cpuTime, quantumTime, queueTime;
        ArrayList<Integer> operacoesIO;

        public Processo(String pid, int duracao, int chegada, ArrayList<Integer> operacoesIO) {
            this.pid = pid;
            this.duracao = duracao;
            this.chegada = chegada;
            this.operacoesIO = operacoesIO;
        }

        private Processo() {
        }

        public void setDuracao(int duracao) {
            this.duracao = duracao;
        }

        public void addCpuTime() {
            this.cpuTime += 1;
        }

        public void addQueueTime() {
            this.queueTime += 1;
        }

        public void addQuantumTime() {
            this.quantumTime += 1;
        }

        public void resetProcessQuantumTime() {
            this.quantumTime = 0;
        }

    }

    public static void printQueue(Queue<Processo> Fila, Processo cpu, File_writer writer) throws IOException {
        if (Fila.isEmpty()) {
            writer.add("FILA: Nao ha processos na fila", true);
        } else {
            writer.add("FILA: ", false);
            for (Processo process : Fila) {
                writer.add(process.pid + "(" + process.duracao + ") ", false);

            }
            writer.add("", true);
        }
    }

    public static Processo addToQueue(ArrayList<Processo> processos, Queue<Processo> fila, int chegada) {
        for (int i = 0; i < processos.size(); i++) {
            Processo process = processos.get(i);

            if (process.chegada == chegada) {
                fila.offer(process);
                return process;
            }
        }
        return null;
    }

    public static boolean verifyIO(Processo cpu) {
        cpu.addCpuTime();//adiciona o tempo em que ele fica na cpu

        for (int i = 0; i < cpu.operacoesIO.size(); i++) {
//            System.out.println(cpu.pid + " " + (cpu.operacoesIO.get(i)) + " " + (cpu.cpuTime));
            if (cpu.operacoesIO.get(i) < cpu.cpuTime) {
                cpu.operacoesIO.remove(0);//remove o primeiro elemento do array de operacoes IO

                return true;
            }
        }

        return false;
    }

    public static void roundRobin(int Quantum, ArrayList<Processo> processos) throws IOException, InterruptedException {
        String filePath = "saida.txt"; // Nome do arquivo
        File file = new File(filePath);
        String absolutePath = file.getAbsolutePath();
        File_writer writer = new File_writer(absolutePath);
        Processo cpu = new Processo();
        Queue<Processo> fila = new LinkedList<>();
        ArrayList<Integer> auxProcessos = new ArrayList<Integer>();

        for (int w = 0; w < processos.size(); w++) {// para printar as duracoes dos processos no final dos logs
            auxProcessos.add(processos.get(w).duracao);
        }

        int tempo = 0;

        writer.clear();
        writer.add(
"***********************************\n"+
"***** ESCALONADOR ROUND ROBIN *****\n"+
"-----------------------------------\n"+
"------- INICIANDO SIMULACAO -------\n"+
"-----------------------------------\n", false);

        writer.add("Quantum: " + Quantum, true);

        addToQueue(processos, fila, 0);//colocando primeiro processo na fila

        System.out.println("Simulacao em tempo real");

        while (true) {

            if (cpu == null && fila.isEmpty()) {
                writer.add("ACABARAM OS PROCESSOS!!!", true);

                break;

            } else {

                cpu = fila.poll(); // Obter o primeiro processo da fila e consequentemente saindo da fila e virando o processo atual da cpu

                while (cpu != null && cpu.duracao > -1) {

                    writer.add("********** TEMPO " + tempo + " *************", true);
                    // --------------printar se houve operação io ----------------------
                    boolean hasOperationIOTime = verifyIO(cpu);//verifica se o processo da cpu recebeu uma operação IO
                    if (hasOperationIOTime) {
                        writer.add("#[evento] OPERACAO I/O <" + cpu.pid + ">", true);
                        // trocar pro proximo processo fila e devolve o processo atual da cpu pra fila
                        fila.offer(cpu);//devolve pra fila
                        cpu.resetProcessQuantumTime();
                        cpu = fila.poll();//colocamos o proximo processo da fila na cpu
                        cpu.addCpuTime();//adicionamos o tempo total que ele fica na cpu

                    }
                    // -------------- fim printar se houve operação io ----------------------

                    // --------------printar se houve chegada ----------------------
                    if (tempo != 0) {//tirando o primeiro processo que eu já adicionei na fila, veremos se chega mais algum no estado de pronto

                        Processo newProcess = addToQueue(processos, fila, tempo);//verifica se algum dos processos chegou na fila
                        if (newProcess != null) {
                            writer.add("#[evento] CHEGADA <" + newProcess.pid + ">", true);
                        }
                    }
                    // --------------fim printar se houve chegada ----------------------

                    // --------------printar se a duracao ultrapassou o valor de quantum ----------------------
                    /* explicando "cpu.resetProcessQuantumTime();". O processo só pode ficar na cpu, 
                    se o tempo que ele fica na cpu for menor ou igual o valor de quantum, 
                    como eu devolvi o processo atual pra fila, esse tempo que fica na cpu é resetado e 
                    a contagem começa novamente quando esse processo voltar pra cpu*/
                    if (cpu.quantumTime == Quantum) {
                        writer.add("#[evento] FIM QUANTUM <" + cpu.pid + ">", true);
                        fila.offer(cpu);//devolve pra fila

                        cpu.resetProcessQuantumTime();

                        cpu = fila.poll();

                    }
                    // --------------fim printar se a duracao ultrapassou o valor de quantum ----------------------
                    // --------------printar se houve encerração de processo ----------------------
                    if (cpu.duracao < 1) {
                        writer.add("#[evento] ENCERRANDO <" + cpu.pid + ">", true);
                        cpu.resetProcessQuantumTime();

                        cpu = fila.poll();
                    }
                    // --------------fim printar se houve encerração de processo ----------------------

                    //------- printar fila e cpu ---------------------------
                    printQueue(fila, cpu, writer);
                    if (cpu != null) {
                        System.out.print(cpu.pid + " | ");
                    }

                    Thread.sleep(500);
                    if (cpu != null) {//se tiver algum processo na cpu printa cpu, se não acabou os processos
                        writer.add("CPU: " + cpu.pid + "(" + cpu.duracao + ")", true);
                    }

                    //------- fim printar fila e cpu ---------------------------
                    tempo++;
                    if (cpu != null) {
                        cpu.addQuantumTime();// adiciona a duracao em que o processo fica na cpu, durante essa estadia
                        cpu.setDuracao(cpu.duracao - 1);//diminui a duracao toda vez que tiver na cpu, se a duracao for menor do que um, remover da cpu 
                    }
                    for (Processo processo : fila) {
                        processo.addQueueTime();//adiciona +1 tempo pros processos que tão na fila, para eu poder calcular posteriormente o tempo de espera médio
                    }
                }

            }

        }
        //---- print gráfico de gantt -------------
        System.out.println("\n------------------");
        for (int i = 0; i < auxProcessos.size(); i++) {
            System.out.print("P" + (i + 1) + " | ");

            for (int j = 0; j < auxProcessos.get(i); j++) {
                System.out.print("#");
            }

            System.out.println("");
        }
        System.out.println("------------------");

        //-----------------------

        //------- print tempo de espera de cada processo ------

        for(Processo processo : processos){
            System.out.println("Processo " + processo.pid + " esperou "+ processo.queueTime);
        }
        //-----------------------------------------------------
//------ print tempo de espera médio -------------------
        int numerator = 0;

        for (Processo processo : processos) {
            numerator += processo.queueTime;
        }

        float averageWaitingTime = numerator / processos.size();

        System.out.println("Tempo de espera medio: " + averageWaitingTime);
//------ fim print tempo de espera médio -------------------

        writer.add("-----------------------------------\n"+
"------- Encerrando simulacao ------\n"+
"-----------------------------------\n", false);

    }

    public static void main(String[] args) {
        String quantum;

        if (args.length > 0 && !args[0].isEmpty()) {
            quantum = args[0];
        } else {
            quantum = "4";
        }

        try {
            ArrayList<Processo> processos = new ArrayList<Processo>();

            String filePath = "arquivo.txt"; // Nome do arquivo
            File file = new File(filePath);
            String absolutePath = file.getAbsolutePath();

            try (BufferedReader br = new BufferedReader(new FileReader(absolutePath))) {
                String linha;

                while ((linha = br.readLine()) != null) {

                    String[] palavras = linha.split(" ");

                    if (palavras.length >= 2) {
                        String pid = palavras[0];
                        int duracao = Integer.parseInt(palavras[1]);
                        int chegada = Integer.parseInt(palavras[2]);

                        ArrayList<Integer> operacoesIO = new ArrayList<Integer>();

                        if (palavras.length >= 4) {
                            String[] operacoesIOStr = palavras[3].split(",");
                            for (String operacaoStr : operacoesIOStr) {
                                operacoesIO.add(Integer.parseInt(operacaoStr));
                            }
                        }

                        processos.add(new Processo(pid, duracao, chegada, operacoesIO));
                    }

                }
            }
            roundRobin(Integer.parseInt(quantum), processos);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
