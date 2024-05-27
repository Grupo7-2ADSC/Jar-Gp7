package Conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    private JdbcTemplate conexaoDoBanco;

    //Construtor de Configuração  do Banco
    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();

        //Drive de Conexão com o Banco
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource.setUrl("jdbc:mysql://localhost:3306/sentinel_system");
        dataSource.setUsername("root");
        dataSource.setPassword("#Gf53994706831");

        conexaoDoBanco = new JdbcTemplate(dataSource);

    }

    //Responsavel por retornar a conexão do Banco
    public JdbcTemplate getConexaoDoBanco () {
        return conexaoDoBanco;
    }
}
