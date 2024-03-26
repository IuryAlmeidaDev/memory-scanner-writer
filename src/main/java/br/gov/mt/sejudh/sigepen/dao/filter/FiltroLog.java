package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;

public class FiltroLog extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Acao acao;
	
    private Date dataInicio;
	
	private Date dataFim;
	
	private String usuarioId;
	
	public Acao getAcao() {
		return acao;
	}

	public void setAcao(Acao acao) {
		this.acao = acao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}
	
	private String acaoMensagem(Acao acao) {
		String mensagem = null;

		switch(acao) {
			case ACESSAR:				
					mensagem = "Acessar: ";				
				
				break;

			case SALVAR:				
					mensagem = "Salvar: ";					

				break;
				
			case EXCLUIR:				
					mensagem = "Excluir: ";				
				
				break;	
				
			case LOGAR:				
					mensagem = "Logar: ";				
				
				break;
				
			case VISUALIZAR:				
					mensagem = "Visualizar: ";				
				
				break;
				
			case ALTERAR:				
					mensagem = "Alterar: ";
				
				break;
				
			case ADICIONAR:				
					mensagem = "Adicionar: ";
				
				break;
				
			case REMOVER:				
					mensagem = "Remover: ";
				
				break;
				
			case FINALIZAR:				
					mensagem = "Finalizar: ";			
				
				break;
				
			case AUTORIZAR:				
					mensagem = "Autorizar: ";				
				
				break;
				
			case CANCELAR:				
					mensagem = "Autorizar: ";
				
				break;
				
			case INICIAR:				
					mensagem = "Iniciar: ";
				
				break;
				
			case IMPRIMIR:			
					mensagem = "Imprimir: ";				
				
				break;
				
			case ERRO:			
				mensagem = "Erro: ";				
			
			break;
			
			default:
				mensagem = "ERRO: ";
				break;
		}
		
		
		return mensagem;
	}
	
	

	@Override
	public void configurarFiltros() {
		if (this.getUsuarioId() != null && !this.getUsuarioId().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("usuarioId", this.getUsuarioId(), MatchMode.EXACT));
		}		
		
		if (this.getAcao() != null) {
			if (this.getAcao().equals(Acao.ERRO)) {
				getCriteria().add(Restrictions.eq("nivel", "ERROR"));
			} else {
				getCriteria().add(Restrictions.ilike("mensagem", acaoMensagem(this.getAcao()), MatchMode.START));
			}
		}
		
		if(this.getDataInicio() != null){
			getCriteria().add(Restrictions.ge("dataLog",this.getDataInicio()));
		}
		
		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("dataLog", this.getDataFim()));
		}
	}
}
