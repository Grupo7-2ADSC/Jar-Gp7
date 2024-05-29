package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Disco extends Componente {

    Looca looca = new Looca();
    DiscoGrupo grupoDisco = looca.getGrupoDeDiscos();
    List<Volume> volumeDiscos = grupoDisco.getVolumes();

    private String nome;

    private Long total;

    private Long uso;

    public Disco(TipoComponente tipo, String nome, Long total, Long uso) {
        super(tipo);
        this.nome = nome;
        this.total = total;
        this.uso = uso;
    }

    public Disco() {

    }

    @Override
    public Integer getIdTipoComponente() {

        setTipo(TipoComponente.DISCO);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con,JdbcTemplate conWin ,Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();

        for (Volume volume : volumeDiscos) {
            setNome(volume.getNome());
            setTotal(volume.getTotal());

            Integer id_componente;

            //Pegando ID  do Componentes.Componente
            try {
                id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?", Integer.class, idServidor,id_tipo_componente, nome);
            } catch (Exception e) {
                id_componente = null;
            }

            if (id_componente == null) {

                con.update("INSERT INTO Componente (nome, total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?, ?)",
                        nome,
                        Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_tipo_componente ,idServidor);

                conWin.update("INSERT INTO Componente (nome, total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?, ?)",
                        nome,
                        Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_tipo_componente ,idServidor);
            }
        }
    }

    @Override
    public void coletarDadosDinamicos (JdbcTemplate con, JdbcTemplate conWin , Integer idServidor) {

        Integer id_tipo_componente = getIdTipoComponente();
        Integer id_componente;

        //Pegando ID  do Componentes.Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor,id_tipo_componente);
        } catch (Exception e) {
            id_componente = null;
        }

        for (Volume volume : volumeDiscos) {
            setUso(volume.getTotal() - volume.getDisponivel());
            setNome(volume.getNome());

            System.out.println("\nDISCOS");

            System.out.println("Nome: " + nome);
            System.out.println("Em Uso: " + Conversor.formatarBytes(uso));

            con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                    Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                    id_componente);

            conWin.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                    Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                    id_componente);
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
