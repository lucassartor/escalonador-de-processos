import java.io.File;

public class BCP {
    //Atributos de controle do BCP
    private final int PID;
    private int programCounter;
    private final int prioridade;
    private int X;
    private int Y;
    private Estado estado;
    private int creditos;
    private int tempoBloqueio;
    private final Processo processo;

    BCP(int PID, int prioridade) {
        this.PID = PID;
        this.prioridade = prioridade;
        this.estado = Estado.PRONTO;
        this.programCounter = 0;
        this.creditos = prioridade;
        this.tempoBloqueio = 0;
        this.processo = new Processo(System.getProperty("user.dir") + File.separator + "programas" + File.separator + String.format("%02d", PID) + ".txt");
    }

    public String executar() {
        this.estado = Estado.EXECUTANDO;
        this.creditos--;
        return processo.executar(++this.programCounter);
    }

    public void diminuiTempoBloqueio() {
        this.tempoBloqueio--;
    }

    public void bloquear(int tempoBloqueio) {
        this.estado = Estado.BLOQUEADO;
        this.tempoBloqueio = tempoBloqueio;
    }

    public boolean temCreditos() {
        return this.creditos > 0;
    }

    public boolean terminouTempoBloqueio() {
        return tempoBloqueio == 0;
    }

    public int getCreditos() {
        return creditos;
    }

    public void redistribuirCreditos() {
        this.creditos = this.prioridade;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setX(int x) {
        this.X = x;
    }

    public void setY(int y) {
        this.Y = y;
    }

    public int getX() {
        return this.X;
    }

    public int getY() {
        return this.Y;
    }

    public String getNome() {
        return "TESTE-" + PID;
    }
}