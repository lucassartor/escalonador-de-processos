import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Escalonador {

    private final List<BCP> filaProntos;
    private final List<BCP> filaBloqueados;
    private int quantum;
    private final Logger logger;

    Escalonador() {
        this.filaProntos = new ArrayList<BCP>();
        this.filaBloqueados = new LinkedList<BCP>();
        logger = new Logger();
    }

    public void novoProcesso(BCP bcp) throws IOException {
        filaProntos.add(bcp);
        logger.escreveCarregando(bcp.getNome());
    }

    private void carregaQuantum(String caminhoBase) throws IOException {
        File arqQuantum = new File(caminhoBase + "/quantum.txt");
        int quantumLido = Integer.parseInt(new String(Files.readAllBytes(Paths.get(arqQuantum.toURI()))).trim());

        if (quantumLido < 0) {
            System.out.println("Quantum inválido");
            return;
        }

        this.quantum = quantumLido;
        logger.criaArquivo(quantumLido);
    }

    private void carregaListaDePrioridade(String caminhoBase) throws IOException {
        File listaDePrioridades = new File(caminhoBase + "/prioridades.txt");
        BufferedReader leitor = new BufferedReader(new FileReader(listaDePrioridades));

        String prioridade;
        int linha = 1;

        while((prioridade = leitor.readLine()) != null) {
            this.novoProcesso(new BCP(linha, Integer.parseInt(prioridade)));
            linha++;
        }

        leitor.close();
    }

    private void carregaProcessos() throws IOException {
        String caminhoBase = "programas";
        carregaQuantum(caminhoBase);
        carregaListaDePrioridade(caminhoBase);
    }

    private void verificaCreditosProntos() {
        if(filaProntos.stream().noneMatch(BCP::temCreditos))
            filaProntos.forEach(BCP::redistribuirCreditos);
    }

    private void ordenaProntos() {
        filaProntos.sort((o1, o2) -> Integer.compare(o2.getCreditos(), o1.getCreditos()));
    }

    private void diminuiTempoBloqueados() {
        filaBloqueados.forEach(BCP::diminuiTempoBloqueio);
    }

    private void verificaBloqueados() {
        if (!filaBloqueados.isEmpty() && filaBloqueados.get(0).terminouTempoBloqueio())
            filaProntos.add(filaBloqueados.remove(0));
    }

    public void executar() throws IOException {
        carregaProcessos();

        Relatorio report = new Relatorio(filaProntos.size());

        while (!filaProntos.isEmpty() || !filaBloqueados.isEmpty()) {
            diminuiTempoBloqueados();
            verificaBloqueados();

            if (filaProntos.isEmpty())
                continue;

            verificaCreditosProntos();
            ordenaProntos();

            BCP processoExecutando = filaProntos.get(0);

            // contador para quantidades de instruções executadas antes de sofrer a
            // interrupção seja por quantum ou por E/S
            int instrucoesExecutadasNoQuantum = 0;

            boolean parouAntes = false;
            int contQuantum = 0;


            String nomeProcesso = processoExecutando.getNome();

            // escrevendo o nome do processo em execução no arquivo
            logger.escreveExecutando(nomeProcesso);

            while (contQuantum < this.quantum) {
                // incrementando a quantidade de intruções executadas para report
                report.instrucaoExecutada();
                instrucoesExecutadasNoQuantum++;

                if (!processoExecutando.temCreditos()) {
                    logger.escreveInterrupcao(nomeProcesso, contQuantum);
                    parouAntes = true;
                    break;
                }

                String instrucaoAtual = processoExecutando.executar();

                if (instrucaoAtual.contains("X=") || instrucaoAtual.contains("Y=")) {

                    String[] atribuicao = instrucaoAtual.split("=");

                    if (atribuicao[0].equals("X")) {
                        processoExecutando.setX(Integer.parseInt(atribuicao[1]));
                    } else {
                        processoExecutando.setY(Integer.parseInt(atribuicao[1]));
                    }

                } else if (instrucaoAtual.contains("E/S")) {

                    // escrevendo processo que executa E/S
                    logger.escreveES(nomeProcesso);

                    //processoExecutando.setEstado(Estado.BLOQUEADO);
                    filaProntos.remove(0);
                    filaBloqueados.add(processoExecutando);

                    processoExecutando.bloquear(3);
                    parouAntes = true;

                    // escrevendo qnt de instruções executadas antes da interrupção de E/S
                    logger.escreveInterrupcao(nomeProcesso, instrucoesExecutadasNoQuantum);

                    //processoExecBCP.aumentaCP();
                    break;

                } else if (instrucaoAtual.contains("COM")) {

                } else if (instrucaoAtual.contains("SAIDA")) {
                    filaProntos.remove(0);
                    //tabProcessos.removeProcesso(processoExec);
                    parouAntes = true;

                    this.logger.escreveFinalizou(nomeProcesso, processoExecutando.getX(), processoExecutando.getY());
                    break;
                }

                contQuantum++;
            } // fim quantum do processo

            if (!parouAntes) {
                processoExecutando.setEstado(Estado.PRONTO);
                logger.escreveInterrupcao(nomeProcesso, instrucoesExecutadasNoQuantum);
            }

            // incrementando o contador de troca de processos
            report.trocaProcesso();
        } // fim processos para executar

        // escrevendo o relatório de médias e o quantum utilizado
        logger.escreveMediaTrocas(report.mediaTrocaDeProcessos());
        logger.escreveMediaIntrucoesPorQuantum(report.mediaIntrucoesPorQuantum());
        logger.escreveQuantum(this.quantum);
    }

    public static void main(String[] args) throws IOException {
        Escalonador esc = new Escalonador();
        esc.executar();
    }
}
