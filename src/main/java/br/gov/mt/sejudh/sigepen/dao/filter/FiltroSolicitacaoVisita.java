package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroSolicitacaoVisita extends FiltroConsulta {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String recuperando;
    private Parente parente;
    private Recuperando recuperandoO;
    private SgpStatusAlternativo status;
    private UnidadePrisional unidadePrisional;
    private List<UnidadePrisional> unidades;
    private boolean listarTodasUnidades;
    private String nomeSocial;
    private Date dataNascimento;
    private String nomeSocial;

    @Override
    public void configurarFiltros() {
    	
		getCriteria()
		.createAlias("recuperando", "recuperando", JoinType.INNER_JOIN)
		.createAlias("recuperando.solicitacoes", "solicitacoes", JoinType.LEFT_OUTER_JOIN)
		.createAlias("solicitacoes.pessoaSolicitacao", "pessoaSolicitacao", JoinType.INNER_JOIN)
		.createAlias("solicitacoes.parentesco", "parentesco", JoinType.INNER_JOIN);
		
		ProjectionList proj = Projections.projectionList();
        proj.add((Projections.projectionList()
                .add(Projections.property("solicitacoes.id").as("id"))
                .add(Projections.property("solicitacoes.numeroDecisaoJudicial").as("numeroDecisaoJudicial"))
                .add(Projections.property("solicitacoes.possuiDecisaoJudicial").as("possuiDecisaoJudicial"))
                .add(Projections.property("solicitacoes.status").as("status"))
                .add(Projections.property("pessoaSolicitacao.nome").as("nomeSolicitante"))
                .add(Projections.property("nome").as("nomeRecuperando"))
                .add(Projections.property("nomeSocial").as("nomeSocial"))
                .add(Projections.property("parentesco.descricao").as("parentesco"))));
                
        getCriteria().setProjection(proj);
        
        if (StringUtils.isNotBlank(this.getNome()) || StringUtils.isNotBlank(this.getCpf())) {
            this.configurarFiltrosPessoa();
        }

        if (this.getStatus() != null) {
            getCriteria().add(Restrictions.eq("solicitacoes.status", this.getStatus()));
        }

        if (this.getParente() != null) {
            getCriteria().add(Restrictions.eq("parente", this.getParente()));
        }

        if (this.getRecuperandoO() != null) {
            getCriteria().add(Restrictions.eq("pessoa.nome", this.getRecuperandoO()));
        }
        
        if (this.getNomeSocial() != null) {
            getCriteria().add(Restrictions.eq("pessoa.nomeSocial", this.getNomeSocial()));
        }

        if(this.isListarTodasUnidades()) {
            if (this.getUnidadePrisional() == null) {
            	List<Long> lista = new ArrayList<Long>();
            	for(UnidadePrisional u : this.unidades) {
            		lista.add(u.getId());
            	}
                getCriteria().add(Restrictions.in("solicitacoes.unidadePrisional.id", lista));
            } else {
            	getCriteria().add(Restrictions.eq("solicitacoes.unidadePrisional", this.getUnidadePrisional()));
            }
        } else {
        	getCriteria().add(Restrictions.eq("solicitacoes.unidadePrisional", SecurityUtil.getUnidadeSelecionada()));
        }
        
        if (StringUtils.isNotBlank(this.getRecuperando())) {
            getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getRecuperando(), MatchMode.ANYWHERE));
        }

        if (this.getDataNascimento() != null) {
            getCriteria().add(Restrictions.le("pessoaSolicitacao.dataNascimento", this.getDataNascimento()));
        }

    }

    private void configurarFiltrosPessoa() {
        if (StringUtils.isNotBlank(this.getNome())) {
        	getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoaSolicitacao.nome", this.getNome(), MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(this.getCpf())) {
        	getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoaSolicitacao.cpf", this.getCpf(), MatchMode.ANYWHERE));
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Sigepen.formatarEspacos(nome);
    }

    public SgpStatusAlternativo getStatus() {
		return status;
	}

	public void setStatus(SgpStatusAlternativo status) {
		this.status = status;
	}

	public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRecuperando() {
        return recuperando;
    }

    public void setRecuperando(String recuperando) {
        this.recuperando = recuperando;
    }

    public Parente getParente() {
        return parente;
    }

    public void setParente(Parente parente) {
        this.parente = parente;
    }

    public Recuperando getRecuperandoO() {
        return recuperandoO;
    }

    public void setRecuperandoO(Recuperando recuperandoO) {
        this.recuperandoO = recuperandoO;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

	public List<UnidadePrisional> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<UnidadePrisional> unidades) {
		this.unidades = unidades;
	}

	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}


	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

}
