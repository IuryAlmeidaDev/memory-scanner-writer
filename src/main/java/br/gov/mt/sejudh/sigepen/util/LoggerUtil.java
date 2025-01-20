package br.gov.mt.sejudh.sigepen.util;

import java.io.Serializable;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.LogSeverity;

@RequestScoped
public class LoggerUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	@Produces
	public Logger criarLogger(InjectionPoint ip) {
		return LogManager.getLogger(ip.getMember().getDeclaringClass().getName());
	}

	public void finaliza(@Disposes Logger logger) {
		ThreadContext.clearAll();
	}
	
	public static void logWS(Throwable e, Logger log) {
		log.error("Erro WebService", e);
	}
	
	public static void logErro(Throwable e, Logger log) {
		log(LogSeverity.ERRO, null, e.toString(), e, log);
	}
	
	public static void logErro(Acao acao, Object objeto, Throwable e, Logger log) {
		log(LogSeverity.ERRO, acao, objeto, e, log);
	}
	
	public static void logInfo(Acao acao, Object objeto, Logger log) {
		log(LogSeverity.INFO, acao, objeto, null, log);
	}
	
	private static String configuraMensagem(Acao acao, LogSeverity severidade, Object objeto) {
		String mensagem = null;
		
		if (acao == null) {
			return "Erro: ";
		}
		
		switch(acao) {
			case ACESSAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao acessar: ";
				} else {
					mensagem = "Acessar: ";
				}
				
				break;

			case SALVAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao salvar: ";
				} else {
					mensagem = "Salvar: ";
				}

				break;
				
			case EXCLUIR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao excluir: ";
				} else {
					mensagem = "Excluir: ";
				}
				
				break;	
				
			case LOGAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao logar: ";
				} else {
					mensagem = "Logar: ";
				}
				
				break;
				
			case VISUALIZAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Visualizar: ";
				} else {
					mensagem = "Visualizar: ";
				}
				
				break;
				
			case ALTERAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Alterar: ";
				} else {
					mensagem = "Alterar: ";
				}
				
				break;
				
			case ADICIONAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Adicionar: ";
				} else {
					mensagem = "Adicionar: ";
				}
				
				break;
				
			case REMOVER:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Remover: ";
				} else {
					mensagem = "Remover: ";
				}
				
				break;
				
			case FINALIZAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Finalizar: ";
				} else {
					mensagem = "Finalizar: ";
				}
				
				break;
				
			case AUTORIZAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Autorizar: ";
				} else {
					mensagem = "Autorizar: ";
				}
				
				break;
				
			case CANCELAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Cancelar: ";
				} else {
					mensagem = "Cancelar: ";
				}
				
				break;
				
			case INICIAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Iniciar: ";
				} else {
					mensagem = "Iniciar: ";
				}
				
				break;
				
			case IMPRIMIR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Imprimir: ";
				} else {
					mensagem = "Imprimir: ";
				}
				
				break;
				
			case ASSOCIAR:
				if (severidade == LogSeverity.ERRO) {
					mensagem = "Erro ao Associar: ";
				} else {
					mensagem = "Associar: ";
				}
				
				break;
				
			case ERRO:				
					mensagem = "Erro: ";		
				
				break;
		}
		
		mensagem += objeto.toString();
		
		return mensagem;
	}
	
	private static void log(LogSeverity severidade, Acao acao, Object objeto, Throwable e, Logger log) {
		if (e != null) {
			if (e.getMessage() != null) {
				if (e.getMessage().toLowerCase().contains("unable to get managed connection")) {
					System.out.println("Entrou");
					return;
				}
			}
		}
		
		ThreadContext.put("loginUser", String.valueOf(SecurityUtil.getUsuario().getId()));
		ThreadContext.put("ipOrigem", getIp());

		if (acao.equals(Acao.IMPRIMIR)) {
			ThreadContext.put("hashImpressao", getHashImpressao());

		}

		String mensagem = configuraMensagem(acao, severidade, objeto);
		
		switch(severidade) {
			case ERRO:
				log.error(mensagem, e);
				break;
			
			case INFO:
				log.info(mensagem);
				break;
				
			default:
				log.error(mensagem + " " + e.getMessage(), e);
				break;
		}
	}
	
	public static  String getHashImpressao() {
		//set session em: FileDownloadHandlerBean.prepararArquivo
		String hashImpressao = "HashNaoGerador";
		FacesUtil facesUtil = new FacesUtil();
		if(facesUtil != null) {
			hashImpressao = (String) facesUtil.getObjetoSessao("hashImpressao");
		}
		
		return hashImpressao;
	}
	
	public static String getIp() {	
		return Optional.ofNullable(FacesUtil.getRequest().getHeader("X-Real-IP-Port"))
                .filter(ipPort -> !ipPort.trim().isEmpty())
                .orElseGet(() -> FacesUtil.getRequest().getRemoteAddr());
    }
}
