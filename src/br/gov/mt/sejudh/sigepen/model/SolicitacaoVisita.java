package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_SOLICITACAO_VISITA", schema = Sigepen.SEJUDH_SCHEMA)
public class SolicitacaoVisita extends BaseModel<Long> implements Serializable {

	private static final long serialVersionUID = -3349712972027465619L;

	@Id
    @SequenceGenerator(name = "SIGEPEN_S_SOLICITACAO_VISITA", sequenceName = "SIGEPEN_S_SOLICITACAO_VISITA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGEPEN_S_SOLICITACAO_VISITA")
    @Column(name = "ID_SOLICITACAO_VISITA")
    private Long id;

    @Column(name = "NUMR_DECISAO_JUDICIAL")
    private String numeroDecisaoJudicial;

    @Enumerated(EnumType.STRING)
    @Column(name = "FLAG_POSSUI_DECISAO_JUDICIAL")
    private SimNao possuiDecisaoJudicial;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_SOLICITACAO_VISITA")
    private SgpStatusAlternativo status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RECUPERANDO")
    private Recuperando recuperando;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARENTE")
    private Parente parente;

    @NotAudited
    @ManyToOne
    @JoinColumn(name = "ID_PARENTESCO")
    private Parentesco parentesco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDADE_PRISIONAL")
    private UnidadePrisional unidadePrisional;

    @Transient
    private String cpf;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA_SOLICITACAO_VISITA")
    private PessoaSolicitacaoVisita pessoaSolicitacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDecisaoJudicial() {
        return numeroDecisaoJudicial;
    }

    public void setNumeroDecisaoJudicial(String numeroDecisaoJudicial) {
        this.numeroDecisaoJudicial = numeroDecisaoJudicial;
    }

    public SimNao getPossuiDecisaoJudicial() {
        return possuiDecisaoJudicial;
    }

    public void setPossuiDecisaoJudicial(SimNao possuiDecisaoJudicial) {
        this.possuiDecisaoJudicial = possuiDecisaoJudicial;
    }

    public Recuperando getRecuperando() {
        return recuperando;
    }

    public void setRecuperando(Recuperando recuperando) {
        this.recuperando = recuperando;
    }

    public Pessoa getPessoa() {
    	Pessoa p = new Pessoa();
    	if(this.pessoaSolicitacao != null) {
        	p.setNome(this.pessoaSolicitacao.getNome());
        	p.setNomeMae(this.pessoaSolicitacao.getNomeMae());
        	p.setNomePai(this.pessoaSolicitacao.getNomePai());
        	p.setNomeSocial(this.pessoaSolicitacao.getNomeSocial());
        	p.setDataNascimento(this.pessoaSolicitacao.getDataNascimento());
    	}
        return p;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public SgpStatusAlternativo getStatus() {
        return status;
    }

    public void setStatus(SgpStatusAlternativo status) {
        this.status = status;
    }

    public Parente getParente() {
        return parente;
    }

    public void setParente(Parente parente) {
        this.parente = parente;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

	public PessoaSolicitacaoVisita getPessoaSolicitacao() {
		return pessoaSolicitacao;
	}

	public void setPessoaSolicitacao(PessoaSolicitacaoVisita pessoaSolicitacao) {
		this.pessoaSolicitacao = pessoaSolicitacao;
	}

	public String getCpf() {
		return this.pessoaSolicitacao.getCpf();
	}
    
	@Override
	public String toString() {
		return "Solicitação de Visita [id=" + id + ", pessoa="
				+ getPessoaSolicitacao().getNome() + ", status=" + status.getDescricao() + "]";
	}
}
