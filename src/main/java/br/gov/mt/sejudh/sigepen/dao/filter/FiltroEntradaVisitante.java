package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroEntradaVisitante extends FiltroConsulta {

	private static final long serialVersionUID = 3929832042968725424L;
	private String nome;
	private String nomeRecuperando;
	private Date data;
	private List<Long> pessoasIds;
	private StatusCredenciamentosVisita statusCredenciamento;
	private Long unidade;
	private Long responsavel;
	private Boolean somenteEntradas;
	private Long idVisitante;

	private Long idEstrutura;
	private Set<Long> idsEstruturas = new HashSet<>();

	@Override
	public void configurarFiltros() {

		getCriteria().createAlias("pessoa", "pessoa", JoinType.LEFT_OUTER_JOIN).createAlias("entradas", "entradas",
				JoinType.LEFT_OUTER_JOIN);

		if (StringUtils.isNotBlank(this.getNome())
				|| StringUtils.isNotBlank(this.getNomeRecuperando())
				|| (this.getPessoasIds() != null && this.getPessoasIds().size() > 0)
				|| (this.getIdVisitante() != null)) {
			if (StringUtils.isNotBlank(this.getNome())) {
				getCriteria()
						.add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getNome(), MatchMode.ANYWHERE));
			}

			if (this.getPessoasIds() != null && this.getPessoasIds().size() > 0) {
				getCriteria().add(Restrictions.in("pessoa.id", this.getPessoasIds()));
			}

			if(this.getIdVisitante() != null) {
				getCriteria().add(Restrictions.eq("id", this.getIdVisitante()));
			}

			if (StringUtils.isNotBlank(this.getNomeRecuperando()) || !this.getIdsEstruturas().isEmpty()) {
				getCriteria().createAlias("credenciamentos", "c", JoinType.INNER_JOIN)
						.createAlias("c.recuperando", "r", JoinType.INNER_JOIN);
			}

			if (StringUtils.isNotBlank(this.getNomeRecuperando())) {
				getCriteria().createAlias("r.pessoa", "p"); // Pessoa do Recuperando
				getCriteria()
						.add(RestrictionsSigepen.ignoraAcentoLike("p.nome", this.getNomeRecuperando(), MatchMode.ANYWHERE));
			}

			if(!this.getIdsEstruturas().isEmpty()) {

				getCriteria()
						.createAlias("r.passagens", "passagem", JoinType.INNER_JOIN)
						.createAlias("passagem.lotacao", "lotacao", JoinType.INNER_JOIN)
						.createAlias("lotacao.estrutura", "estrutura", JoinType.INNER_JOIN)
				;

				getCriteria().add(Restrictions.isNull("passagem.dataSaida"));
				getCriteria().add(Restrictions.isNull("lotacao.dataSaida"));
				getCriteria().add(Restrictions.in("estrutura.id", this.getIdsEstruturas()));

			}

		} else {
			if (this.getData() != null) {
				Date di = Sigepen.getDataConsultaMaiorIgual(this.getData());
				Date df = Sigepen.getDataConsultaMenorIgual(this.getData());
				getCriteria().add(Restrictions.between("entradas.dataEntrada", di, df));
			}

			if (this.getUnidade() != null && this.getUnidade() > 0) {
				getCriteria().add(Restrictions.eq("entradas.unidadePrisional.id", this.getUnidade()));
			}

			if (this.getSomenteEntradas() != null && this.getSomenteEntradas()) {
				getCriteria().add(Restrictions.eq("entradas.situacao", SituacaoVisita.EM_ANDAMENTO));
			}
		}

	}

	public FiltroEntradaVisitante() {
		this.pessoasIds = new ArrayList<Long>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}



	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public List<Long> getPessoasIds() {
		return pessoasIds;
	}

	public void setPessoasIds(List<Long> pessoasIds) {
		this.pessoasIds = pessoasIds;
	}

	public StatusCredenciamentosVisita getStatusCredenciamento() {
		return statusCredenciamento;
	}

	public void setStatusCredenciamento(StatusCredenciamentosVisita statusCredenciamento) {
		this.statusCredenciamento = statusCredenciamento;
	}

	public Long getUnidade() {
		return unidade;
	}

	public void setUnidade(Long unidade) {
		this.unidade = unidade;
	}

	public Long getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Long responsavel) {
		this.responsavel = responsavel;
	}

	public Boolean getSomenteEntradas() {
		return somenteEntradas;
	}

	public void setSomenteEntradas(Boolean somenteEntradas) {
		this.somenteEntradas = somenteEntradas;
	}

	public Long getIdVisitante() {
		return idVisitante;
	}

	public void setIdVisitante(Long idVisitante) {
		this.idVisitante = idVisitante;
	}

	@Override
	public void configurarOrdenacao() {
		super.configurarOrdenacao();
		
		if (this.getOrdenacao() != null) {
			getCriteria().addOrder((this.getOrdenacao().isAscending() ? Order.asc("entradas.id") : Order.desc("entradas.id")));
			
			if (this.getPropriedadeOrdenacao().equals("entradas.id")) {
				this.getOrdenacao().nulls(NullPrecedence.LAST);
			}
		}
	}

    public String getNomeRecuperando() {
        return nomeRecuperando;
    }

    public void setNomeRecuperando(String nomeRecuperando) {
        this.nomeRecuperando = nomeRecuperando;
    }

    public Long getIdEstrutura() {
        return idEstrutura;
    }

    public void setIdEstrutura(Long idEstrutura) {
        this.idEstrutura = idEstrutura;
    }

    public Set<Long> getIdsEstruturas() {
        return idsEstruturas;
    }

    public void setIdsEstruturas(Set<Long> idsEstruturas) {
        this.idsEstruturas = idsEstruturas;
    }
}
