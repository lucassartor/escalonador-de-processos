import java.io.*;

public class Logger {
    private File arq;

    public Logger() {
    }

    public void criaArquivo(int quantumLido) {
        try {
            String quantumStr = Integer.toString(quantumLido);
            int tam = quantumStr.length();

            if (tam < 2)
                quantumStr = "0" + quantumStr;

            File arq = new File(String.format("./logs/log%s.txt", quantumStr));

            if (!arq.createNewFile()) {
                System.out.println("Arquivo já existente, apagando conteúdo para regravar log...");

                RandomAccessFile rArq = new RandomAccessFile(arq, "rw");
                rArq.setLength(0);
                rArq.close();
            }

            this.arq = arq;
        } catch (IOException e) {
            System.out.println("erro de E/S.");
        }
    }

    public void fechaWriter(FileWriter fw, BufferedWriter bw) throws IOException {
        bw.close();
        fw.close();
    }

    private void escreveString(String str) throws IOException {
        FileWriter fw = new FileWriter(arq, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(str);
        bw.newLine();
        fechaWriter(fw, bw);
    }

    public void escreveCarregando(String nomeProcesso) throws IOException {
        String str = "Carregando " + nomeProcesso;

        escreveString(str);
    }

    public void escreveES(String nomeProcesso) throws IOException {
        String str = "E/S iniciada em " + nomeProcesso;

        escreveString(str);
    }

    public void escreveInterrupcao(String nomeProcesso, int instrucoesExecutadas) throws IOException {
        String str = "Interrompendo " + nomeProcesso + " após " + String.format("%d", instrucoesExecutadas)
                + " instruções";

        escreveString(str);
    }

    public void escreveFinalizou(String nomeProcesso, int regX, int regY) throws IOException {
        String str = nomeProcesso + " terminado. " + "X=" + String.format("%d", regX) + " Y="
                + String.format("%d", regY);

        escreveString(str);
    }

    public void escreveExecutando(String nomeProcesso) throws IOException {
        String str = "Executando " + nomeProcesso;

        escreveString(str);
    }

    public void escreveMediaTrocas(float mediaTrocas) throws IOException {
        String str = "MÉDIA DE TROCAS: " + String.format("%.1f", mediaTrocas);

        escreveString(str);
    }

    public void escreveMediaIntrucoesPorQuantum(float mediaIntrucoesPorQuantum) throws IOException {
        String str = "MÉDIA DE INSTRUÇÕES: " + String.format("%.1f", mediaIntrucoesPorQuantum);

        escreveString(str);
    }

    public void escreveQuantum(int quantum) throws IOException {
        String str = "QUANTUM: " + quantum;

        escreveString(str);
    }
}
