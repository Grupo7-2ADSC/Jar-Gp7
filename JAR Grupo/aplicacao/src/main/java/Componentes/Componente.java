package Componentes;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class Componente {

    private TipoComponente tipo;

    public Componente(TipoComponente tipo) {
        this.tipo = tipo;
    }

    public Componente() {

    }

    public TipoComponente getTipo() {
        return tipo;
    }

    public void setTipo(TipoComponente tipo) {
        this.tipo = tipo;
    }

    public abstract void coletarDadosFixos(JdbcTemplate con, Integer idServidor);

    public abstract void coletarDadosDinamicos(JdbcTemplate con, Integer idServidor);

    public abstract Integer getIdTipoComponente ();
}
