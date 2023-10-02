package com.myapp;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class SSHMySQLConnect {

    private static Properties config = new Properties();

    static {
        try (InputStream in = SSHMySQLConnect.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static void main(String[] args) {
        com.jcraft.jsch.Session sshSession = null;
        Connection connection = null;
        try {
            System.out.println("load properties");
            JSch jsch = new JSch();
            sshSession = jsch.getSession(
                    config.getProperty("ssh.user"), 
                    config.getProperty("ssh.host"), 
                    Integer.parseInt(config.getProperty("ssh.port"))
            );
            sshSession.setPassword(config.getProperty("ssh.password"));
            sshSession.setConfig("StrictHostKeyChecking", "no");
            System.out.println("starting ssh session to host:"+sshSession.getHost());
            sshSession.connect();
            sshSession.setPortForwardingL(
                    Integer.parseInt(config.getProperty("db.localPort")), 
                    config.getProperty("db.host"), 
                    Integer.parseInt(config.getProperty("db.remotePort"))
            );
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:" + config.getProperty("db.localPort") + "/" + config.getProperty("db.name"), 
                    config.getProperty("db.user"), 
                    config.getProperty("db.password")
            );

            String result = executeSQLQuery(connection, "SELECT count(*) as cnt FROM theuser");
            System.out.println("result:"+result);
            sendEmail("recipientEmail@example.com", "SQL Results", result);  // Ändere den Empfänger nach Bedarf
            System.out.println("all done");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                if (sshSession != null) sshSession.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String executeSQLQuery(Connection connection, String query) throws Exception {
        StringBuilder resultBuilder = new StringBuilder();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        while (rs.next()) {
            resultBuilder.append(rs.getString("cnt")).append("\n");  // Ersetze "columnName"
        }
        return resultBuilder.toString();
    }

    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        String from = config.getProperty("email.from");
        final String username = config.getProperty("email.username");
        final String password = config.getProperty("email.password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", config.getProperty("email.smtp.host"));
        props.put("mail.smtp.port", config.getProperty("email.smtp.port"));

        javax.mail.Session session = javax.mail.Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}
