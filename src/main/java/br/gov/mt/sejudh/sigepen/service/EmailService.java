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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.gov.mt.sejudh.sigepen.model.Usuario;

public class EmailService {

	private static final Logger log = LogManager.getLogger(EmailService.class);

	private static final String SMTP_HOST = System.getenv("SMTP_HOST");//"srv-smtp.seguranca.local";
	private static final String SMTP_PORT = System.getenv("SMTP_PORT");//"587";
	private static final String USERNAME_EMAIL = System.getenv("USERNAME_EMAIL");//"sistemas@sesp.mt.gov.br";
	private static final String PASSWORD_EMAIL = System.getenv("PASSWORD_EMAIL");//"O@bM9TUn";
	private static final String TEMPLATE_PATH_USUARIO_RESET_SENHA = "/WEB-INF/pages/_templates/EmailResetSenha.xhtml";
	private static final String TEMPLATE_PATH_SOLICITACAO_RESET_SENHA = "/WEB-INF/pages/_templates/SolicitacaoResetSenha.xhtml";
	private static final String EMAIL_TI_SIGEPEN = "sigepen@sesp.mt.gov.br";

	private static void sendEmail(String destinatario, String assunto, String body) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.port", SMTP_PORT);

		//
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME_EMAIL, PASSWORD_EMAIL);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(USERNAME_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
		message.setSubject(assunto);
		message.setContent(body, "text/html; charset=utf-8");

		Transport.send(message);

		log.info("E-mail enviado | destinatario={} | assunto={}", destinatario, assunto);
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
				content = loadTemplate(TEMPLATE_PATH_USUARIO_RESET_SENHA, placeholders);
			}catch (Exception e) {
				log.error("Erro ao carregar template de reset de senha | template={}", TEMPLATE_PATH_USUARIO_RESET_SENHA, e);
			}

			sendEmail(usuario.getEmail(), subject, content);

			log.info("E-mail de reset de senha enviado | usuario={}", usuario.getLogin());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail de reset de senha | usuario={}", usuario.getLogin(), e);
		}
	}

	public static void sendEmailTISegepen(Usuario usuario) {
		try {
			String subject = "Reset de senha!";
			Map<String, String> placeholders = new HashMap<String, String>();

			String nomeUnidade = "";

			if(!usuario.getAcessos().isEmpty()){
				nomeUnidade = usuario.getAcessos().get(0).getUnidade().getNome();
			}

			placeholders.put("{{nome}}", usuario.getServidor().getNome());
			placeholders.put("{{unidade}}", nomeUnidade);

			String content = "";
			try {
				content = loadTemplate(TEMPLATE_PATH_SOLICITACAO_RESET_SENHA, placeholders);
			}catch (Exception e) {
				log.error("Erro ao carregar template de solicitacao de reset | template={}", TEMPLATE_PATH_SOLICITACAO_RESET_SENHA, e);
			}

			sendEmail(EMAIL_TI_SIGEPEN, subject, content);

			log.info("E-mail de solicitacao de reset enviado para TI Sigepen | solicitante={}", usuario.getLogin());
		} catch (Exception e) {
			log.error("Erro ao enviar e-mail para TI Sigepen | solicitante={}", usuario.getLogin(), e);
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
