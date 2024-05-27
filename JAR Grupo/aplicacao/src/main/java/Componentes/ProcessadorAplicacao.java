package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processador.Processador;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProcessadorAplicacao extends Componente {

    Looca looca = new Looca();
    Processador processador = looca.getProcessador();

    private String nome;

    private Double uso;

    public ProcessadorAplicacao(TipoComponente tipo, String nome, Double uso) {
        super(tipo);
        this.nome = nome;
        this.uso = uso;
    }

    public ProcessadorAplicacao() {

    }

    @Override
    public Integer getIdTipoComponente() {
        setTipo(TipoComponente.CPU);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con, Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();

        setNome(processador.getNome());

        con.update("INSERT INTO Componente (nome, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                nome,
                id_tipo_componente, idServidor);

    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();
        Integer id_componente;

        setUso(processador.getUso());

        //Pegando ID  do Componentes.Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor,id_tipo_componente);
        } catch (Exception e) {
            id_componente = null;
        }

        System.out.println("\nPROCESSADOR");
        System.out.println("Em Uso: " + String.format("%.1f", uso));

        con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                String.format("%.1f", uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente);

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getUso() {
        return uso;
    }

    public void setUso(Double uso) {
        this.uso = uso;
    }
}
