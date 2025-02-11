package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "LOG", schema = Sigepen.SEJUDH_AUDIT_SCHEMA)
public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATALOG")
	private Date dataLog;
	
	@Column(name = "NIVEL")
	private String nivel;
	
	@Column(name = "MESSAGE")
	private String mensagem;	

	@Column(name ="USUARIO_ID")
    private  String usuarioId;
    
    @Column(name = "IP_ORIGEM")
	private String ipOrigem;
    
    @Lob
    @Column(name = "THROWABLE")
    private String textoExcecao;

    @Column(name = "HASH_IMPRESSAO", length = 100)
    private String hashImpressao;
    
	public String getHashImpressao() {
		return hashImpressao;
	}

	public void setHashImpressao(String hashImpressao) {
		this.hashImpressao = hashImpressao;
	}

	public String getTextoExcecao() {
		return textoExcecao;
	}

	public void setTextoExcecao(String textoExcecao) {
		this.textoExcecao = textoExcecao;
	}

	public long getId() {
		return id;
	}

	public Date getDataLog() {
		return dataLog;
	}
	
	public String getNivel() {
		return nivel;
	}
	
	public String getMensagem() {
		return mensagem;
	}
	
	public String getUsuarioId() {
		return usuarioId;
	}	

	public String getIpOrigem() {
		return ipOrigem;
	}		
	
}