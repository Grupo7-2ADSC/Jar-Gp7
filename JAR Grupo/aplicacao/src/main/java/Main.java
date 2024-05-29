import Componentes.Disco;
import Componentes.MemoriaAplicacao;
import Componentes.ProcessadorAplicacao;
import Conexao.Conexao;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {

        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        JdbcTemplate conWin = conexao.getConexaoDBWIN();
        Timer timer = new Timer();

        Disco disco = new Disco();
        MemoriaAplicacao memoria = new MemoriaAplicacao();
        ProcessadorAplicacao processador = new ProcessadorAplicacao();
        ProcessoAplicacao processo = new ProcessoAplicacao();
        SistemaOperacional sistema = new SistemaOperacional();
        RedeAplicacao rede = new RedeAplicacao();

        System.out.println("\nOBTENDO HOSTNAME DO SERVIDOR");

        String hostName = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = InetAddress.getLocalHost().getHostName();
            System.out.println("HOSTNAME: " + hostName);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }

        Integer idServidor;
        try {
            idServidor = con.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, hostName);
            System.out.println("\nID Servidor: " + idServidor);
            idServidor = conWin.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, hostName);
            System.out.println("\nID Servidor: " + idServidor);
        } catch (Exception e) {
            idServidor = null;
            System.out.println("ID DO SERVIDOR NÃO ENCONTRADO: " + idServidor);
        }

//        Servidor servidor = new Servidor();
//        servidor.buscarDadosDoServidor(con, idServidor);
//
//        // Imprimindo os dados do servidor
//        System.out.println(servidor);

//      Coleta de dados fixos são executadas apenas uma vez
//      fora do timer.schedule (Bloco que se repete a cada intervalo de tempo)
        disco.coletarDadosFixos(con,conWin ,idServidor);
        memoria.coletarDadosFixos(con,conWin ,idServidor);
        processador.coletarDadosFixos(con,conWin ,idServidor);

        Integer finalIdServidor = idServidor;
        timer.schedule(new TimerTask() {
            public void run() {

                if (finalIdServidor != null) {

                    disco.coletarDadosDinamicos(con,conWin, finalIdServidor);
                    memoria.coletarDadosDinamicos(con,conWin,  finalIdServidor);
                    processador.coletarDadosDinamicos(con,conWin,  finalIdServidor);
                    processo.coletarDadosDeProcessos(con,conWin, finalIdServidor);
                    sistema.coletarDadosDeSistemaOperacional(con,conWin, finalIdServidor);
                    rede.coletarDadosDeRede(con,conWin, finalIdServidor);

                }
            }
        }, 0, 10000);
    }
}
