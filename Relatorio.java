public class Relatorio {

    private int qntTrocaProcesso;
    private int qntIntrucoesPorQuantum;
    private final int qntProcessos;

    public Relatorio(int qntProcessos) {
        this.qntTrocaProcesso = 0;
        this.qntIntrucoesPorQuantum = 0;
        this.qntProcessos = qntProcessos;
    }

    public void instrucaoExecutada() {
        qntIntrucoesPorQuantum++;
    }

    public void trocaProcesso() {
        qntTrocaProcesso++;
    }

    public float mediaIntrucoesPorQuantum() {
        return (float) qntIntrucoesPorQuantum / (float) qntTrocaProcesso;
    }

    public float mediaTrocaDeProcessos() {
        return (float) qntTrocaProcesso / (float) qntProcessos;
    }
}