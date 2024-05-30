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
    public void coletarDadosFixos(JdbcTemplate con, JdbcTemplate conWin , Integer idServidor, Integer idServidorNuvem) {

        Integer id_tipo_componente = getIdTipoComponente();

        setNome(processador.getNome());

        Integer id_componente;
        Integer id_componente_nuvem;

        //Pegando ID  do Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor,id_tipo_componente);
            id_componente_nuvem = conWin.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidorNuvem,id_tipo_componente);

        } catch (Exception e) {
            id_componente = null;
            id_componente_nuvem = null;
        }

        if(id_componente == null) {

            con.update("INSERT INTO Componente (nome, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                    nome,
                    id_tipo_componente, idServidor);
        }

        if(id_componente_nuvem == null) {

            conWin.update("INSERT INTO Componente (nome, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                    nome,
                    id_tipo_componente, idServidorNuvem);
        }

    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, JdbcTemplate conWin ,Integer idServidor, Integer idServidorNuvem) {

        Integer id_tipo_componente = getIdTipoComponente();

        Integer id_componente;
        Integer id_componente_nuvem;

        //Pegando ID  do Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor,id_tipo_componente);
            id_componente_nuvem = conWin.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidorNuvem,id_tipo_componente);

        } catch (Exception e) {
            id_componente = null;
            id_componente_nuvem = null;
        }

        setUso(processador.getUso());

        System.out.println("\nPROCESSADOR");
        System.out.println("Em Uso: " + String.format("%.1f", uso));

        con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                String.format("%.1f", uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente);

        conWin.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                String.format("%.1f", uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente_nuvem);

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
