package br.gov.mt.sejudh.sigepen.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailService{

	
	
	private static final String SMTP_HOST = "srv-smtp.seguranca.local";
    private static final String SMTP_PORT = "465";
    private static final String USERNAME = "sistemas@sesp.mt.gov.br";
    private static final String PASSWORD = "O@bM9TUn"; 

    public static void sendEmail(String destinatario, String assunto, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST); 
        props.put("mail.smtp.port", SMTP_PORT);

        // 
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(assunto);
        message.setContent(body, "text/html; charset=utf-8");

        Transport.send(message);

        System.out.println("E-mail enviado com sucesso para: " + to);
    }
    
    public static void main() {
        try {
            String destinatario = "exemplo@exemplo.com";
            String assunto = "Teste de envio de e-mail";
            String mensagem = "<h1>Olá!</h1><p>Este é um teste de envio de e-mail com Jakarta Mail.</p>";

            sendEmail(destinatario, assunto, mensagem);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
