package Slack;

import Conexao.Conexao;
import com.slack.api.methods.SlackApiException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import com.slack.api.Slack;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
public class PublishingMessage {

    //    Conexão
    static Conexao conexao = new Conexao();
    static JdbcTemplate con = conexao.getConexaoDoBanco();
    JdbcTemplate conWin = conexao.getConexaoDBWIN();

//    Sistema Registro

    public static void publishMessageSistemaRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Sistema
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'REDE';";

        // Parâmetros da consulta

        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Integer resultado = con.queryForObject(sql, Integer.class);

            if (resultado >= 50) {
                texto = "Erro: utilização do Sistema acima de 50%.";
            } else {
                texto = "OK: utilização do Sistema dentro do normal.";
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = "teste";
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }



    //    Cpu Registro

    public static void publishMessageCpuRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Cpu
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'CPU';";

        // Parâmetros da consulta

        String texto = "";

//        try {
//            // Executando a consulta e armazenando o resultado em uma variável
//            Integer resultado = con.queryForObject(sql, Integer.class);
//
//            if (resultado >= 50) {
//                texto = "Erro: utilização do Cpu acima de 50%.";
//            } else {
//                texto = "OK: utilização do Cpu dentro do normal.";
//            }
//        } catch (Exception e) {
//            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
//            texto = "Erro ao consultar o banco de dados.";
//        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
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

        // Query SQL para selecionar a utilização do Memoria
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'MEMORIA';";

        // Parâmetros da consulta

        String texto = "";

//        try {
//            // Executando a consulta e armazenando o resultado em uma variável
//            Integer resultado = con.queryForObject(sql, Integer.class);
//
//            if (resultado >= 50) {
//                texto = "Erro: utilização do Memoria acima de 50%.";
//            } else {
//                texto = "OK: utilização do Memoria dentro do normal.";
//            }
//        } catch (Exception e) {
//            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
//            texto = "Erro ao consultar o banco de dados.";
//        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
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
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Disco
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'REDE';";


        // Parâmetros da consulta

        String texto = "";

//        try {
//            // Executando a consulta e armazenando o resultado em uma variável
//            Integer resultado = con.queryForObject(sql, Integer.class);
//
//            if (resultado >= 50) {
//                texto = "Erro: utilização do Disco acima de 50%.";
//            } else {
//                texto = "OK: utilização do Disco dentro do normal.";
//            }
//        } catch (Exception e) {
//            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
//            texto = "Erro ao consultar o banco de dados.";
//        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }



    //    Processo Registro

    public static void publishMessageProcessoRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");


        // Query SQL para selecionar a utilização do Processo
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'REDE';";

        // Parâmetros da consulta

        String texto = "";

//        try {
//            // Executando a consulta e armazenando o resultado em uma variável
//            Integer resultado = con.queryForObject(sql, Integer.class);
//
//            if (resultado >= 50) {
//                texto = "Erro: utilização do Processo acima de 50%.";
//            } else {
//                texto = "OK: utilização do Processo dentro do normal.";
//            }
//        } catch (Exception e) {
//            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
//            texto = "Erro ao consultar o banco de dados.";
//        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }

    //    Rede Registro

    public static void publishMessageRedeRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Rede
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'REDE';";

        // Parâmetros da consulta

        String texto = "";

//        try {
//            // Executando a consulta e armazenando o resultado em uma variável
//            Integer resultado = con.queryForObject(sql, Integer.class);
//
//            if (resultado >= 50) {
//                texto = "Erro: utilização do Rede acima de 50%.";
//            } else {
//                texto = "OK: utilização do Rede dentro do normal.";
//            }
//        } catch (Exception e) {
//            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
//            texto = "Erro ao consultar o banco de dados.";
//        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
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


