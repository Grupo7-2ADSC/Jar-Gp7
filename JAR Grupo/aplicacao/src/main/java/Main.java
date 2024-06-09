import Componentes.Disco;
import Componentes.MemoriaAplicacao;
import Componentes.ProcessadorAplicacao;
import Conexao.Conexao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.github.britooo.looca.api.core.Looca;
import Logs.Logs;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Main {

    public static void main(String[] args) {

        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        JdbcTemplate conWin = conexao.getConexaoDBWIN();
        Timer timer = new Timer();

        Looca looca = new Looca();
        Logs log = new Logs();

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
            hostName = inetAddress.getHostName();
            System.out.println("HOSTNAME: " + hostName);

            String data;
            log.setSistemaOperacional(looca.getSistema().getSistemaOperacional());
            log.setHostName(looca.getRede().getParametros().getHostName());
            data = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
            log.setData(data);
            log.setMensagem("O ID do servidor foi capturado com sucesso");

            System.out.println(log.toString().replace("idMaquina: null\n", "").replace("\t",""));

            String dataArquivo = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String nomeArquivoLog = dataArquivo + ".txt";
            try (FileWriter writer = new FileWriter(nomeArquivoLog, true)) {
                String logString = log.toString().replace("idMaquina: null\n", "").replace("\t", "");
                writer.write(logString);
            } catch (IOException u) {
                System.out.println("Erro ao gerar log" + u.getMessage());
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }

        Integer idServidorLocal = null;
        Integer idServidorNuvem = null;
        Integer idEmpresaLocal = null;

        try {
            // Buscar informações da empresa na nuvem
            Map<String, Object> empresaNuvem = conWin.queryForMap(
                    "SELECT e.id_empresa, e.cnpj, e.nome FROM Empresa e " +
                            "JOIN Servidor s ON e.id_empresa = s.fk_empresa " +
                            "WHERE s.host_name = ?", hostName);

            if (empresaNuvem != null) {
                // Verificar se a empresa existe no banco local
                try {
                    idEmpresaLocal = con.queryForObject(
                            "SELECT id_empresa FROM Empresa WHERE cnpj = ?", Integer.class, empresaNuvem.get("cnpj"));
                } catch (Exception e) {
                    // Empresa não encontrada no banco local
                }

                if (idEmpresaLocal == null) {
                    // Inserir a empresa no banco local
                    con.update(
                            "INSERT INTO Empresa (cnpj, nome) VALUES (?, ?)",
                            empresaNuvem.get("cnpj"),
                            empresaNuvem.get("nome")
                    );
                    System.out.println("Empresa inserida no banco local.");

                    // Obter o id da empresa inserida
                    idEmpresaLocal = con.queryForObject(
                            "SELECT id_empresa FROM Empresa WHERE cnpj = ?", Integer.class, empresaNuvem.get("cnpj"));
                } else {
                    System.out.println("Empresa já existe no banco local.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERRO AO BUSCAR OU INSERIR EMPRESA.");
        }

        try {
            // Verificar se o servidor existe na nuvem
            idServidorNuvem = conWin.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, hostName);
            System.out.println("\nIDNuvem Servidor: " + idServidorNuvem);

            // Buscar informações do servidor na nuvem e mapear para a classe Servidor
            Servidor servidorNuvem = conWin.queryForObject(
                    "SELECT id_servidor, nome, host_name, fk_empresa FROM Servidor WHERE id_servidor = ?",
                    new BeanPropertyRowMapper<>(Servidor.class),
                    idServidorNuvem);

            try {
                // Verificar se o servidor existe no banco local
                idServidorLocal = con.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, hostName);
                System.out.println("\nIDLocal Servidor: " + idServidorLocal);
            } catch (Exception e) {
                // Servidor não encontrado no banco local, inserir servidor coletado da nuvem
                con.update(
                        "INSERT INTO Servidor (nome, host_name, fk_empresa) VALUES (?, ?, ?)",
                        servidorNuvem.getNome(),
                        servidorNuvem.getHost_name(),
                        idEmpresaLocal  // Usar idEmpresaLocal para garantir a integridade referencial
                );
                System.out.println("Servidor inserido no banco local.");

                // Obter o id do servidor inserido
                idServidorLocal = con.queryForObject(
                        "SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, servidorNuvem.getHost_name());
            }

        } catch (Exception e) {
            System.out.println("SERVIDOR NÃO ENCONTRADO NA NUVEM OU ERRO NA INSERÇÃO.");
            e.printStackTrace();
        }

//        Servidor servidor = new Servidor();
//        servidor.buscarDadosDoServidor(con, idServidor);
//
//        // Imprimindo os dados do servidor
//        System.out.println(servidor);

//      Coleta de dados fixos são executadas apenas uma vez
//      fora do timer.schedule (Bloco que se repete a cada intervalo de tempo)

        memoria.coletarDadosFixos(con,conWin ,idServidorLocal, idServidorNuvem);
        processador.coletarDadosFixos(con,conWin ,idServidorLocal, idServidorNuvem);
        disco.coletarDadosFixos(con,conWin ,idServidorLocal, idServidorNuvem);

        Integer finalIdServidor = idServidorLocal;
        Integer finalIdServidorNuvem = idServidorNuvem;

        timer.schedule(new TimerTask() {
            public void run() {

                if (finalIdServidor != null) {

                    memoria.coletarDadosDinamicos(con,conWin,  finalIdServidor, finalIdServidorNuvem);
                    processador.coletarDadosDinamicos(con,conWin,  finalIdServidor, finalIdServidorNuvem);
                    disco.coletarDadosDinamicos(con,conWin, finalIdServidor, finalIdServidorNuvem);
                    processo.coletarDadosDeProcessos(con,conWin, finalIdServidor, finalIdServidorNuvem);
                    sistema.coletarDadosDeSistemaOperacional(con,conWin, finalIdServidor, finalIdServidorNuvem);
                    rede.coletarDadosDeRede(con,conWin, finalIdServidor, finalIdServidorNuvem);

                }
            }
        }, 50, 10000);
    }
}
