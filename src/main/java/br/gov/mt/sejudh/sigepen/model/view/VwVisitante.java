package br.gov.mt.sejudh.sigepen.model.view;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Immutable
@Table(name = "SGP_V_VISITANTE", schema = Sigepen.SEJUDH_SCHEMA)
public class VwVisitante extends BaseModel<Long> {
	
	private static final long serialVersionUID = 6216716577668846056L;

	@Id
    @Column(name = "ID_VISITANTE")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA_VISITANTE")
    private Pessoa pessoa;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_HORA_CADASTRO")
    private Date dataHoraCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "STAT_VISITANTE")
    private StatusVisitante status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ULTIMA_APROVACAO")
    private Date ultimaAprovacao;

    @Convert(converter = EnumCustomConverter.class, disableConversion = true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    @Column(name = "ULTIMA_FASE_CREDENCIAL")
    private FaseCredenciamento ultimaFaseCredenciamento;

    @Convert(converter = EnumCustomConverter.class, disableConversion = true)
    @Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
    @Column(name = "ULTIMO_STATUS_CREDENCIAL")
    private StatusCredenciamento ultimoStatusCredenciamento;

    @Column(name = "TOTAL_CREDENCIAL")
    private Long credenciamentosAtivos;

    @OneToMany(mappedBy = "visitante", fetch = FetchType.LAZY)
    private List<Credenciamento> credenciamentos;

    @OneToMany(mappedBy = "visitante",fetch = FetchType.LAZY)
    private List<Impedimento> impedimentos;
    
    @Column(name = "NOME_PESSOA")
    private String nome;

    @Column(name = "ID_PESSOA")
    private Long idPessoa;

    @Column(name = "FASE_CREDENCIAL")
    private Integer faseCredenciamento;

    @Column(name = "STATUS_CREDENCIAL")
    private Integer statusCredenciamento;
    
    @Column(name = "CODG_FASE_ATUAL")
    private Integer faseAtual;
    
    @Column(name = "CODG_SITUACAO_ATUAL")
    private Integer situacaoAtual;
    
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_DESLIGAMENTO")
	private TipoDesligamento tipoDesligamento;
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_SAIDA")
	private Date dataSaida;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FIM_IMPEDIMENTO")
	private Date fimImpedimento;

	@Column(name = "ID_RECUPERANDO")
    private Long idRecuperando;
	
	//1 - Cor, 2 - Classe
	public String colorIconStatus(int tipo) {
		String cor = "#808080";
		String classe = "fa fa-exclamation-circle";
		
		if (this.status != null) {
			switch (this.status) {
				case A:
					cor = "#009900";
					classe = "fa fa-check-circle";
					break;
				case C:
					cor = "#FFEB00";
					break;
				case I:
					cor = "#FF8000";
					break;
				case M:
					cor = "#FF0000";
					break;
				case P:
					cor = "#808080";
					break;
			}
		}
		
		if (tipo == 1)
			return cor;
		else if (tipo == 2)
			return classe;
		else 
			return StringUtils.EMPTY;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    public void setDataHoraCadastro(Date dataHoraCadastro) {
        this.dataHoraCadastro = dataHoraCadastro;
    }

    public StatusVisitante getStatus() {
        return status;
    }

    public void setStatus(StatusVisitante status) {
        this.status = status;
    }

    public Date getUltimaAprovacao() {
        return ultimaAprovacao;
    }

    public void setUltimaAprovacao(Date ultimaAprovacao) {
        this.ultimaAprovacao = ultimaAprovacao;
    }

    public FaseCredenciamento getUltimaFaseCredenciamento() {
    	if(ultimaFaseCredenciamento == null)
    		return FaseCredenciamento.RECEBIMENTO;
        return ultimaFaseCredenciamento;
    }

    public void setUltimaFaseCredenciamento(FaseCredenciamento ultimaFaseCredenciamento) {
        this.ultimaFaseCredenciamento = ultimaFaseCredenciamento;
    }

    public Long getCredenciamentosAtivos() {
        return credenciamentosAtivos;
    }

    public void setCredenciamentosAtivos(Long credenciamentosAtivos) {
        this.credenciamentosAtivos = credenciamentosAtivos;
    }

    public StatusCredenciamento getUltimoStatusCredenciamento() {
        return ultimoStatusCredenciamento;
    }

    public void setUltimoStatusCredenciamento(StatusCredenciamento ultimoStatusCredenciamento) {
        this.ultimoStatusCredenciamento = ultimoStatusCredenciamento;
    }

    public List<Credenciamento> getCredenciamentos() {
        return credenciamentos;
    }

    public List<Impedimento> getImpedimentos() {
        return impedimentos;
    }
    
    public String getNome() {
		return nome;
	}
    
    public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Integer getFaseCredenciamento() {
		return faseCredenciamento;
	}

	public void setFaseCredenciamento(Integer faseCredenciamento) {
		this.faseCredenciamento = faseCredenciamento;
	}
	
	public Integer getStatusCredenciamento() {
		return statusCredenciamento;
	}

	public void setStatusCredenciamento(Integer statusCredenciamento) {
		this.statusCredenciamento = statusCredenciamento;
	}

    public Integer getFaseAtual() {
		return faseAtual;
	}

	public void setFaseAtual(Integer faseAtual) {
		this.faseAtual = faseAtual;
	}

	public Integer getSituacaoAtual() {
		return situacaoAtual;
	}

	public void setSituacaoAtual(Integer situacaoAtual) {
		this.situacaoAtual = situacaoAtual;
	}

	public FaseCredenciamento getFaseAtualCredenciamento() {
    	if(this.getFaseAtual() == null)
    		return FaseCredenciamento.RECEBIMENTO;
        return FaseCredenciamento.getValue(this.getFaseAtual());
    }

	public TipoDesligamento getTipoDesligamento() {
		return tipoDesligamento;
	}

	public void setTipoDesligamento(TipoDesligamento tipoDesligamento) {
		this.tipoDesligamento = tipoDesligamento;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Date getFimImpedimento() {
		return fimImpedimento;
	}

	public void setFimImpedimento(Date fimImpedimento) {
		this.fimImpedimento = fimImpedimento;
	}

    public Long getIdRecuperando() {
		return idRecuperando;
	}

	public void setIdRecuperando(Long idRecuperando) {
		this.idRecuperando = idRecuperando;
	}

	@Override
    public String toString() {
		return "Visitante [id=" + id + ", nome=" + pessoa.getNome() + "]";
    }
}
