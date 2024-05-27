import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.processos.ProcessoGrupo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ProcessoAplicacao {

    Looca looca = new Looca();
    ProcessoGrupo grupoProcesso = looca.getGrupoDeProcessos();

    private Integer pid;

    private String nome;

    private Double uso_cpu;

    private Double uso_memoria;

    public ProcessoAplicacao(Integer pid, String nome, Double uso_cpu, Double uso_memoria) {
        this.pid = pid;
        this.nome = nome;
        this.uso_cpu = uso_cpu;
        this.uso_memoria = uso_memoria;
    }

    public ProcessoAplicacao() {
    }

    public void coletarDadosDeProcessos (JdbcTemplate con, Integer idServidor) {

        System.out.println("\nPROCESSOS");

        // 5 PRINCIPAIS PROCESSOS COM USOCPU ACIMA DE 50%
        List<Processo> listaProcessos = grupoProcesso.getProcessos();

        // Ordena a lista de processos com base no uso de CPU, do maior para o menor
        listaProcessos.sort((p1, p2) -> Double.compare(p2.getUsoCpu(), p1.getUsoCpu()));

        // Pega os 5 primeiros processos com maior uso de CPU
        List<Processo> top5Processos = listaProcessos.subList(0, Math.min(5, listaProcessos.size()));

        for (Processo processo : top5Processos) {
            if (processo.getUsoCpu() >= 50.0) {
                setNome(processo.getNome());
                setPid(processo.getPid());
                setUso_cpu(processo.getUsoCpu());
                setUso_memoria(processo.getUsoMemoria());

                System.out.println("PID: " + pid);
                System.out.println("Nome:" + nome);
                System.out.println("Uso de CPU: " + String.format("%.1f", uso_cpu));
                System.out.println("Uso de Memoria: " + String.format("%.1f", uso_memoria));

                // Verifica se o processo já existe no banco de dados
                Integer count = con.queryForObject("SELECT COUNT(*) FROM ProcessoRegistro WHERE pid = ? AND fk_servidor = ?",
                        Integer.class, pid, idServidor);

                if (count != null && count > 0) {
                    // Atualiza o registro existente
                    con.update("UPDATE ProcessoRegistro SET nome = ?, uso_cpu = ?, uso_memoria = ? WHERE pid = ? AND fk_servidor = ?",
                            nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, pid, idServidor);
                } else {
                    // Insere um novo registro
                    con.update("INSERT INTO ProcessoRegistro (pid, nome, uso_cpu, uso_memoria, fk_servidor) VALUES (?, ?, ?, ?, ?)",
                            pid, nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, idServidor);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "|" +
                "pid = " + pid +
                ", nome = " + nome + '\'' +
                ", uso_cpu = " + uso_cpu +
                ", uso_memoria = " + uso_memoria +
                '|';
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getUso_cpu() {
        return uso_cpu;
    }

    public void setUso_cpu(Double uso_cpu) {
        this.uso_cpu = uso_cpu;
    }

    public Double getUso_memoria() {
        return uso_memoria;
    }

    public void setUso_memoria(Double uso_memoria) {
        this.uso_memoria = uso_memoria;
    }
}

