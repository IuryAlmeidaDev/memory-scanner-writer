package br.gov.mt.sejudh.sigepen.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import br.gov.mt.sejudh.sigepen.model.Usuario;

public class EmailService {

	private static final String SMTP_HOST = "srv-smtp.seguranca.local";
	private static final String SMTP_PORT = "587";
	private static final String USERNAME = "sistemas@sesp.mt.gov.br";
	private static final String PASSWORD = "O@bM9TUn";
	private static final String TEMPLATE_PATH = "/WEB-INF/pages/_templates/EmailResetSenha.xhtml";

	private static void sendEmail(String destinatario, String assunto, String body) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "false");
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

		System.out.println("E-mail enviado com sucesso para: " + destinatario);
	}

	public static void sendEmailResetSenha(Usuario usuario) {
		try {
			String subject = "Reset de senha!";
			Map<String, String> placeholders = new HashMap<String, String>();
			
			placeholders.put("{{nome}}", usuario.getServidor().getNome());
			placeholders.put("{{login}}", usuario.getLogin());
			placeholders.put("{{senha}}", usuario.getLogin());

			String content = "";
			try {				
				content = loadTemplate(TEMPLATE_PATH, placeholders);
			}catch (Exception e) {
				e.printStackTrace();
			}

			sendEmail(usuario.getEmail(), subject, content);

			System.out.println("E-mail enviado com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String loadTemplate(String templatePath, Map<String, String> placeholders) throws Exception {
		
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		templatePath = ctx.getRealPath(templatePath); 
		
	        String content = new String(Files.readAllBytes(Paths.get(templatePath)), "UTF-8");
	        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
	            content = content.replace(entry.getKey(), entry.getValue());
	        }
	        return content;
	    }

}
