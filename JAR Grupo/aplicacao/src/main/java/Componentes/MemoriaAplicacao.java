package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemoriaAplicacao extends Componente {

    Looca looca = new Looca();
    Memoria memoria = looca.getMemoria();

    private Long total;

    private Long uso;

    public MemoriaAplicacao(TipoComponente tipo, Long total, Long uso) {
        super(tipo);
        this.total = total;
        this.uso = uso;
    }

    public MemoriaAplicacao() {
    }

    @Override
    public Integer getIdTipoComponente() {

        setTipo(TipoComponente.MEMORIA);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con, Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();

        setTotal(memoria.getTotal());

        con.update("INSERT INTO Componente (total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
               Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_tipo_componente ,idServidor);
    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();
        Integer id_componente;

        //Pegando ID  do Componentes.Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor,id_tipo_componente);
        } catch (Exception e) {
            id_componente = null;
        }

        setUso(memoria.getEmUso());

        System.out.println("\nMEMORIA");

        System.out.println("Em Uso: " + Conversor.formatarBytes(uso));

        con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente);


    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUso() {
        return uso;
    }

    public void setUso(Long uso) {
        this.uso = uso;
    }
}
