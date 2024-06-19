package Slack;

import Conexao.Conexao;
import com.github.britooo.looca.api.core.Looca;
import com.slack.api.methods.SlackApiException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.slack.api.Slack;
import org.springframework.jdbc.core.JdbcTemplate;

public class PublishingMessage {

    //    Conexão
    static Conexao conexao = new Conexao();
    static JdbcTemplate con = conexao.getConexaoDoBanco();
    static JdbcTemplate conWin = conexao.getConexaoDBWIN();

    static org.slf4j.Logger logger = LoggerFactory.getLogger("my-awesome-slack-app");
    static java.util.Date lastDiscoMessageDate = null;

    //    Cpu Registro

    public static Double converterGB(Long numero) {
        Double numeroConvertido = Math.round(numero / (1024.0 * 1024.0 * 1024.0) * 100.0) / 100.0;
        return numeroConvertido;
    }

    public PublishingMessage() {
    }

    public static void publishMessageCpuRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");
        String hostName = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            System.out.println("HOSTNAME: " + hostName);

            // Código de log omitido...
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }
//        TOKEN
        String token = "SELECT token FROM tokenSlack where idToken = 1;";
        // Query SQL para selecionar a utilização do Cpu
        String sql = """
                SELECT ISNULL(AVG(uso), 0) AS MediaUso
                FROM registro
                WHERE fk_componente = 5
                AND data_registro >= DATEADD(MINUTE, -5, GETDATE())
                """;

        // Parâmetros da consulta
        String pMin = """
                SELECT parametro_min FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'CPU' and id_configuracao = 1);
                """;

        String pMax = """
                SELECT parametro_max FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'CPU' and id_configuracao = 1);
                """;

        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Double resultado = conWin.queryForObject(sql, Double.class);
            resultado = resultado * 2;
            BigDecimal bd = new BigDecimal(resultado).setScale(2, RoundingMode.HALF_UP);
            resultado = bd.doubleValue();
            Double parametroMin = conWin.queryForObject(pMin, Double.class);
            Double parametroMax = conWin.queryForObject(pMax, Double.class);

            if (resultado > parametroMax) {
                texto = hostName + " - Alerta: Utilização da CPU acima de " + parametroMax + "% \nA média foi de: " + resultado + "%";
            } else if (resultado < parametroMin) {
                texto = hostName + " - Alerta: Utilização da CPU abaixo de " + parametroMin + "% \nA média foi de: " + resultado + "%";
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            String slack = conWin.queryForObject(token, String.class);
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token(slack)
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }


    //    Memoria Registro

    public static void publishMessageMemoriaRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");
        Looca looca = new Looca();
        String hostName = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            System.out.println("HOSTNAME: " + hostName);

            // Código de log omitido...
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }

//        TOKEN
        String token = "SELECT token FROM tokenSlack where idToken = 1;";

        // Query SQL para selecionar a utilização do Memoria
        String sql = """
                SELECT AVG(uso) AS MediaUso
                FROM Registro
                WHERE fk_componente = 4
                AND data_registro >= DATEADD(MINUTE, -5, GETDATE());
                """;

        // Parâmetros da consulta
        String pMin = """
                SELECT parametro_min FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'MEMORIA' and id_configuracao = 2);
                """;

        String pMax = """
                SELECT parametro_max FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'MEMORIA' and id_configuracao = 2);
                """;


        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Double resultado = conWin.queryForObject(sql, Double.class);
            resultado = (resultado / converterGB(looca.getMemoria().getTotal()) * 100);
            BigDecimal bd = new BigDecimal(resultado).setScale(2, RoundingMode.HALF_UP);
            resultado = bd.doubleValue();
            Double parametroMin = conWin.queryForObject(pMin, Double.class);
            Double parametroMax = conWin.queryForObject(pMax, Double.class);
            String slack = conWin.queryForObject(token, String.class);

            if (resultado > parametroMax) {
                texto = hostName + " - Alerta: Utilização da Memória RAM acima de " + parametroMax + "% \nA média foi de: " + resultado + "%";
            } else if (resultado < parametroMin) {
                texto = hostName + " - Alerta: Utilização da Memória RAM abaixo de " + parametroMin + "% \nA média foi de: " + resultado + "%";
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            String slack = conWin.queryForObject(token, String.class);
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token(slack)
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }


    //    Disco Registro

    public static void publishMessageDiscoRegistro(String id) {
        var client = Slack.getInstance().methods();
        Looca looca = new Looca();
        String hostName = "";

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            System.out.println("HOSTNAME: " + hostName);

            // Código de log omitido...
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }
        // TOKEN
        String token = "SELECT token FROM tokenSlack where idToken = 1;";

        // Parâmetros da consulta
        String pMin = """
                SELECT parametro_min FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'DISCO' and id_configuracao = 3);
                """;

        String pMax = """
                SELECT parametro_max FROM ConfiguracaoAlerta
                WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'DISCO' and id_configuracao = 3);
                """;


        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Double resultado = converterGB(looca.getGrupoDeDiscos().getTamanhoTotal()) - converterGB(looca.getGrupoDeDiscos().getVolumes().get(0).getDisponivel());
            resultado = (resultado / converterGB(looca.getGrupoDeDiscos().getTamanhoTotal())) * 100;
            BigDecimal bd = new BigDecimal(resultado).setScale(2, RoundingMode.HALF_UP);
            resultado = bd.doubleValue();
            Double parametroMin = conWin.queryForObject(pMin, Double.class);
            Double parametroMax = conWin.queryForObject(pMax, Double.class);
            String slack = conWin.queryForObject(token, String.class);

            if (resultado > parametroMax) {
                texto = hostName + " - Alerta: Utilização do Disco acima de " + parametroMax + "% \nA média foi de: " + resultado + "%";
            } else if (resultado < parametroMin) {
                texto = hostName + " - Alerta: Utilização do Disco abaixo de " + parametroMin + "% \nA média foi de: " + resultado + "%";
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            String slack = conWin.queryForObject(token, String.class);
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token(slack)
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }
}
