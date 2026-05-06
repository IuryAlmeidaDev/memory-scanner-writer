package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.*;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class EntradaSaidaDAO {

	@Inject
	private EntityManager em;

	@Inject
	private EstruturaDAO estruturaDAO;

	@Inject
	private VisitanteDAO visitanteDAO;

	@Inject
	private VisitanteResponsavelDAO visitanteResponsavelDAO;

	public int buscarQuantidadeFiltrada(FiltroEntradaSaida filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(EntradaSaida.class, em);
		criteria.setProjection(Projections.countDistinct("id"));
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaSaida> buscarEntradasPorFiltro(FiltroEntradaSaida filtro, boolean isRelatorio) {

		Criteria criteria = filtro.criarCriteriaParaBusca(EntradaSaida.class, em, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<EntradaSaida> entradas = criteria.list();
		for(EntradaSaida e : entradas) {
			Hibernate.initialize(e.getVisitante().getIdentidadeDeGenero());
			Hibernate.initialize(e.getVisitante());
			Hibernate.initialize(e.getRecuperandos());
			Hibernate.initialize(e.getVisitante().getFotos());
			Hibernate.initialize(e.getVisitante().getPessoa().getNacionalidade());
			Hibernate.initialize(e.getVisitante().getPessoa().getNaturalidade());
			Hibernate.initialize(e.getVisitante().getPessoa().getPais());
			Hibernate.initialize(e.getVisitante().getPessoa().getBiometria());
			Hibernate.initialize(e.getVisitante().getResponsavel());
			if(e.getVisitante().getResponsavel() != null) {
				Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa());
				Hibernate.initialize(e.getVisitante().getResponsavel().getFotos());
				Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getNaturalidade());
				Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getNacionalidade());
				Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getPais());

			}
			Hibernate.initialize(e.getVisitante().getCredenciamentos());
			for(Credenciamento vr : e.getVisitante().getCredenciamentos()) {
				Hibernate.initialize(vr.getFasesDoCredenciamento());
				if(vr.getRecuperando() != null) {
					Hibernate.initialize(vr.getRecuperando().getFaltas());
					Hibernate.initialize(vr.getRecuperando().getPassagens());
					for(Passagem p : vr.getRecuperando().getPassagens()) {
						Hibernate.initialize(p.getLotacao());
					}
				}
			}
		}
		return entradas;
	}

	public List<Estrutura> buscarEstruturaDisponivel(long idUnidade, Date hora) throws ValidacaoException {
		List<Estrutura> estruturas = new ArrayList<Estrutura>();
		Calendar c = Calendar.getInstance();
		c.setTime(hora);
		int d = c.get(Calendar.DAY_OF_WEEK);
		boolean dom = d == 1;
		boolean seg = d == 2;
		boolean ter = d == 3;
		boolean qua = d == 4;
		boolean qui = d == 5;
		boolean sex = d == 6;
		boolean sab = d == 7;

		UnidadePrisional unidade = this.em.find(UnidadePrisional.class, idUnidade);
		for(ParametroVisita p : unidade.getParametrosVisita()) {
			if ((p.getStatus() == SgpStatus.A) && (!bloqueada(p, hora)) && ((p.entrada() == 0 && p.saida() == 0) || (p.saida() > 0 && p.entrada() <= this.getHora(hora) && p.saida() >= this.getHora(hora)))) {
				if ((dom && p.isDomingo()) ||
						(seg && p.isSegunda()) ||
						(ter && p.isTerca()) ||
						(qua && p.isQuarta()) ||
						(qui && p.isQuinta()) ||
						(sex && p.isSexta()) ||
						(sab && p.isSabado())){
					estruturas = this.buscarEstruturas(p, estruturas);
				}
			}
		}
		return estruturas;
	}

	private boolean bloqueada(ParametroVisita parametro, Date hora) {
		for(ParametroVisitaBloqueio b : parametro.getBloqueios()) {
			if(b.getDataInicio().before(hora) && b.getDataFim().after(hora)) {
				return true;
			}
		}
		return false;
	}

	private List<Estrutura> buscarEstruturas(ParametroVisita p, List<Estrutura> estruturas) throws ValidacaoException {
		for(ParametroVisitaEstrutura e : p.getEstruturas()) {
			if(e.getEstrutura().getCela())
				incluirEstrutura(e.getEstrutura(), estruturas);
			else {
				this.buscaTodasAsCelasDaEstruturaId(e.getEstrutura().getId())
						.stream()
						.forEach(e2 -> {
							incluirEstrutura(e2, estruturas);
						});
			}
		}
		return estruturas;
	}

	@SuppressWarnings({ "unchecked" })
	public List<Estrutura> buscaTodasAsCelasDaEstruturaId(Long id) {
		Session session = this.em.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT id,                                                           ");
		sql.append("       capacidade,                                                    ");
		sql.append("       cela,                                                          ");
		sql.append("       nome,                                                          ");
		sql.append("       situacao,                                                      ");
		sql.append("       estruturapai_id,                                               ");
		sql.append("       unidadeprisional_id,                                           ");
		sql.append("       versao                                                         ");
		sql.append("  FROM (SELECT e.*, SYS_CONNECT_BY_PATH(e.id, '/') AS BREADCRUMB_ALIAS");
		sql.append("          FROM sigepen_owner.estrutura e                              ");
		sql.append("         START WITH e.estruturapai_id IS NULL                         ");
		sql.append("        CONNECT BY PRIOR e.id = e.estruturapai_id)                    ");
		sql.append(" WHERE cela = 1 AND BREADCRUMB_ALIAS LIKE '%'||chr(47)||" + id + "||chr(47)||'%' ");

		return session.createSQLQuery(sql.toString()).addEntity(Estrutura.class).list();
	}

	public void buscarCelas(Estrutura estrutura, List<Estrutura> retorno){
		List<Estrutura> filhos = estruturaDAO.buscaEstruturaFilho(estrutura);
		for (Estrutura e : filhos) {
			if(e.getCela()) {
				retorno.add(e);
			}else {
				buscarCelas(e, retorno);
			}
		}
		return;
	}

	private void incluirEstrutura(Estrutura est, List<Estrutura> estruturas)  {
		boolean jaCad = false;
		for(Estrutura pe : estruturas) {
			if(est.getId() == pe.getId())
				jaCad = true;
		}
		if(!jaCad) {
			estruturas.add(est);
		}
	}
	private int getHora(Date hora) {
		String x = "" + hora.getHours();
		if(hora.getMinutes() < 10) {
			x += "0" + hora.getMinutes();
		} else {
			x += hora.getMinutes();
		}
		return Integer.parseInt(x);
	}

	public List<ParametroVisita> buscarParametrosVisita(long idUnidade, Date hora) throws ValidacaoException {
		List<ParametroVisita> parametros = new ArrayList<ParametroVisita>();
		Calendar c = Calendar.getInstance();
		c.setTime(hora);
		int d = c.get(Calendar.DAY_OF_WEEK);
		boolean dom = d == 1;
		boolean seg = d == 2;
		boolean ter = d == 3;
		boolean qua = d == 4;
		boolean qui = d == 5;
		boolean sex = d == 6;
		boolean sab = d == 7;

		UnidadePrisional unidade = this.em.find(UnidadePrisional.class, idUnidade);
		for(ParametroVisita p : unidade.getParametrosVisita()) {
			List<Estrutura> estruturas = new ArrayList<Estrutura>();
			if (
					(p.getStatus().equals(SgpStatus.A)) &&
							(!bloqueada(p, hora)) &&
							(
									(p.entrada() == 0 && p.saida() == 0) || (p.saida() > 0 && p.entrada() <= this.getHora(hora) && p.saida() >= this.getHora(hora))

							)

			) {
				if ((dom && p.isDomingo()) ||
						(seg && p.isSegunda()) ||
						(ter && p.isTerca()) ||
						(qua && p.isQuarta()) ||
						(qui && p.isQuinta()) ||
						(sex && p.isSexta()) ||
						(sab && p.isSabado())){
					p.setCelas(this.buscarEstruturas(p, estruturas));
					parametros.add(p);
				}

			}
		}
		return parametros;
	}

	public Visitante buscarVisitantePorPessoa(FiltroEntradaSaida filtro) {
		Query query = this.em.createQuery("select v from Visitante v join fetch v.pessoa p where p.id = :id");
		query.setParameter("id", filtro.getPessoasIds().get(0));
		List<Visitante> lista = query.getResultList();
		Visitante visitante = new Visitante();
		if(lista != null && lista.size() > 0) {
			visitante = lista.get(0);
			Hibernate.initialize(visitante.getIdentidadeDeGenero());
			Hibernate.initialize(visitante.getFotos());
			Hibernate.initialize(visitante.getPessoa().getNacionalidade());
			Hibernate.initialize(visitante.getPessoa().getNaturalidade());
			Hibernate.initialize(visitante.getPessoa().getPais());
			Hibernate.initialize(visitante.getPessoa().getBiometria());
			if(visitante.getPessoa().getBiometria() != null) {
				Hibernate.initialize(visitante.getPessoa().getBiometria().getDedos());
			}

			List<VisitanteResponsavel> listaResponsaveis =
					visitanteResponsavelDAO.buscarListaVisitantesReponsaveis(visitante);

			List<Visitante> visitantesResponsaveis = new ArrayList<>();

			if(!listaResponsaveis.isEmpty()) {
				for(VisitanteResponsavel vr : listaResponsaveis) {
					Visitante vi = new Visitante();
					vi = visitanteDAO.buscarPorId(vr.getVisitanteResponsavel().getId());
					visitantesResponsaveis.add(vi);
				}

				if(!visitantesResponsaveis.isEmpty()) {
					visitante.setVisitantesReponsaveis(visitantesResponsaveis);
				}
			}


			Hibernate.initialize(visitante.getResponsavel());
			if(visitante.getResponsavel() != null) {
				Hibernate.initialize(visitante.getResponsavel().getPessoa());
				Hibernate.initialize(visitante.getResponsavel().getFotos());
				Hibernate.initialize(visitante.getResponsavel().getIdentidadeDeGenero());
				Hibernate.initialize(visitante.getResponsavel().getPessoa().getNaturalidade());
				Hibernate.initialize(visitante.getResponsavel().getPessoa().getNacionalidade());
				Hibernate.initialize(visitante.getResponsavel().getPessoa().getPais());
				Hibernate.initialize(visitante.getResponsavel().getPessoa().getBiometria());
				if(visitante.getResponsavel().getPessoa().getBiometria() != null) {
					Hibernate.initialize(visitante.getResponsavel().getPessoa().getBiometria().getDedos());
				}
			}
			Hibernate.initialize(visitante.getCredenciamentos());
			for(Credenciamento vr : visitante.getCredenciamentos()) {
				Hibernate.initialize(vr.getRecuperando());
				Hibernate.initialize(vr.getFasesDoCredenciamento());
				if(vr.getRecuperando() != null) {
					Hibernate.initialize(vr.getRecuperando().getFaltas());
					Hibernate.initialize(vr.getRecuperando().getPassagens());
					for(Passagem p : vr.getRecuperando().getPassagens()) {
						Hibernate.initialize(p.getLotacao());
						for(PassagemLotacao l : p.getLotacao()) {
							Hibernate.initialize(l.getEstrutura());
							Hibernate.initialize(l.getEstrutura().getUnidadePrisional());
						}
					}
				}
			}
		}
		return visitante;
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		if(entrada.getId() == null)
			this.em.persist(entrada);
		else
			entrada = this.em.merge(entrada);
		return entrada;
	}

	public void bloquearRecuperandoParaControleVisita(Long recuperandoId) {
		this.em.find(Recuperando.class, recuperandoId, LockModeType.PESSIMISTIC_WRITE);
	}

	public UnidadePrisional buscarUnidadePrisionalParaControleVisita(Long unidadeId) {
		return this.em.find(UnidadePrisional.class, unidadeId);
	}

	public Long contarVisitantesAdultosPorRecuperandoNoDia(Long unidadeId, Long recuperandoId, Date inicioDia, Date fimDia, Date dataMaioridade) {
		return contarVisitantesAdultosPorRecuperandoNoDia(unidadeId, recuperandoId, inicioDia, fimDia, dataMaioridade, null);
	}

	public Long contarVisitantesAdultosPorRecuperandoNoDia(Long unidadeId, Long recuperandoId, Date inicioDia, Date fimDia, Date dataMaioridade, Long entradaIgnoradaId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COUNT(DISTINCT E.ID) ");
		sql.append(" FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA E ");
		sql.append(" JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA_RECUP ESR ON ESR.ID_ENTRADA_SAIDA = E.ID ");
		sql.append(" JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAMENTO C ON C.ID_CREDENCIAMENTO = ESR.ID_CREDENCIAMENTO ");
		sql.append(" JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_VISITANTE V ON V.ID_VISITANTE = E.ID_VISITANTE ");
		sql.append(" JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA P ON P.ID = V.ID_PESSOA_VISITANTE ");
		sql.append(" WHERE E.ID_UNIDADE_PRISIONAL = :unidadeId ");
		sql.append(" AND C.ID_RECUPERANDO = :recuperandoId ");
		sql.append(" AND E.DATA_ENTRADA >= :inicioDia ");
		sql.append(" AND E.DATA_ENTRADA <= :fimDia ");
		sql.append(" AND E.SITUACAO IN (:situacaoEmAndamento, :situacaoFinalizada) ");
		sql.append(" AND P.DATA_NASCIMENTO <= :dataMaioridade ");
		if (entradaIgnoradaId != null) {
			sql.append(" AND E.ID <> :entradaIgnoradaId ");
		}

		Query query = this.em.createNativeQuery(sql.toString());
		query.setParameter("unidadeId", unidadeId);
		query.setParameter("recuperandoId", recuperandoId);
		query.setParameter("inicioDia", inicioDia);
		query.setParameter("fimDia", fimDia);
		query.setParameter("situacaoEmAndamento", SituacaoVisita.EM_ANDAMENTO.getId());
		query.setParameter("situacaoFinalizada", SituacaoVisita.FINALIZADA.getId());
		query.setParameter("dataMaioridade", dataMaioridade);
		if (entradaIgnoradaId != null) {
			query.setParameter("entradaIgnoradaId", entradaIgnoradaId);
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	public Long buscarQuantidadeEmAndamento(FiltroEntradaSaida f) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String data = sdf.format(f.getData());
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT COUNT(*) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA E ");
			jpql.append(" WHERE E.ID_UNIDADE_PRISIONAL = " + f.getUnidade());
			jpql.append(" AND E.DATA_SAIDA IS NULL ");
			jpql.append(" AND E.DATA_ENTRADA >= TO_DATE('" + data + " 00:00:00','DD/MM/YYYY HH24:MI:SS')");
			jpql.append(" AND E.DATA_ENTRADA <= TO_DATE('" + data + " 23:59:59','DD/MM/YYYY HH24:MI:SS')");
			Query query = this.em.createNativeQuery(jpql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0L;
		}
	}

	public Long buscarQuantidadeFinalizada(FiltroEntradaSaida f) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String data = sdf.format(f.getData());
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT COUNT(*) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA E ");
			jpql.append(" WHERE E.ID_UNIDADE_PRISIONAL = " + f.getUnidade());
			jpql.append(" AND E.DATA_ENTRADA IS NOT NULL ");
			jpql.append(" AND E.DATA_SAIDA >= TO_DATE('" + data + " 00:00:00','DD/MM/YYYY HH24:MI:SS')");
			jpql.append(" AND E.DATA_SAIDA <= TO_DATE('" + data + " 23:59:59','DD/MM/YYYY HH24:MI:SS')");
			Query query = this.em.createNativeQuery(jpql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0L;
		}
	}

	public Long buscarEntradaResponsavel(FiltroEntradaSaida f) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String data = sdf.format(f.getData());
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT COUNT(*) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA E ");
			jpql.append(" WHERE E.ID_UNIDADE_PRISIONAL = " + f.getUnidade());
			jpql.append(" AND E.DATA_SAIDA IS NULL ");
			jpql.append(" AND E.DATA_ENTRADA >= TO_DATE('" + data + " 00:00:00','DD/MM/YYYY HH24:MI:SS')");
			jpql.append(" AND E.DATA_ENTRADA <= TO_DATE('" + data + " 23:59:59','DD/MM/YYYY HH24:MI:SS')");
			jpql.append(" AND E.ID_VISITANTE = " + f.getResponsavel());
			Query query = this.em.createNativeQuery(jpql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0L;
		}
	}

	public void excluir(EntradaSaida entrada) {
		this.em.remove(this.em.merge(entrada));
	}

	public EntradaSaida buscarPorId(Long id) {
		EntradaSaida e = this.em.find(EntradaSaida.class, id);
		return e;
	}

	public Visitante buscarBiometriaVisitante(List<Long> ids) {
		Session session = this.em.unwrap(Session.class);
		Criteria c = session.createCriteria(Visitante.class);
		c.createAlias("pessoa", "pessoa", JoinType.INNER_JOIN);
		c.add(Restrictions.in("pessoa.id", ids));
		c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Visitante> visitantes = c.list();
		if(visitantes != null && visitantes.size() > 0) {
			Visitante v = visitantes.get(0);
			Hibernate.initialize(v.getPessoa());
			Hibernate.initialize(v.getIdentidadeDeGenero());
			Hibernate.initialize(v.getCredenciamentos());
			for(Credenciamento vr : v.getCredenciamentos()) {
				Hibernate.initialize(vr.getFasesDoCredenciamento());
				if(vr.getRecuperando() != null) {
					Hibernate.initialize(vr.getRecuperando().getFaltas());
					Hibernate.initialize(vr.getRecuperando().getPassagens());
					for(Passagem p : vr.getRecuperando().getPassagens()) {
						Hibernate.initialize(p.getLotacao());
						for(PassagemLotacao l : p.getLotacao()) {
							Hibernate.initialize(l.getEstrutura());
							Hibernate.initialize(l.getEstrutura().getUnidadePrisional());
						}
					}
				}
			}

			if(v.getPessoa() != null) {
				Hibernate.initialize(v.getPessoa().getBiometria());
				if(v.getPessoa().getBiometria() != null) {
					Hibernate.initialize(v.getPessoa().getBiometria().getDedos());
				}
			}
			return v;
		}
		return null;
	}

	public Visitante buscarPessoaVisitante(List<Long> ids) {

		Session session = this.em.unwrap(Session.class);
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT v FROM Visitante v ");
		hql.append("LEFT JOIN FETCH v.identidadeDeGenero ");
		hql.append("LEFT JOIN FETCH v.pessoa p ");
		hql.append("LEFT JOIN FETCH p.nacionalidade ");
		hql.append("LEFT JOIN FETCH p.pais ");
		hql.append("LEFT JOIN FETCH p.naturalidade n ");
		hql.append("LEFT JOIN FETCH n.estado ");

		hql.append("LEFT JOIN FETCH v.pessoa.biometria ");
		hql.append("WHERE p.id = :pessoaId ");
		Visitante visitante = (Visitante) session.createQuery(hql.toString())
				.setParameter("pessoaId", ids.get(0))
				.uniqueResult();
		Hibernate.initialize(visitante.getFotos());

		return visitante;

	}

	public int buscarQuantidadeFiltrada(FiltroEntradaVisitante filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Visitante.class, em);
		criteria.setProjection(Projections.countDistinct("id"));
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Visitante> buscarEntradasPorFiltro(FiltroEntradaVisitante filtro, boolean isRelatorio) {

		Criteria criteria = filtro.criarCriteriaParaBusca(Visitante.class, em, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		List<Visitante> visitantes = criteria.list();
		for(Visitante v : visitantes) {
			Hibernate.initialize(v.getEntradas());
			for(EntradaSaida e : v.getEntradas()) {
				Hibernate.initialize(e.getUnidadePrisional());
				Hibernate.initialize(e.getVisitante().getIdentidadeDeGenero());
				Hibernate.initialize(e.getVisitante());
				Hibernate.initialize(e.getRecuperandos());
				Hibernate.initialize(e.getVisitante().getFotos());
				Hibernate.initialize(e.getVisitante().getPessoa().getNacionalidade());
				Hibernate.initialize(e.getVisitante().getPessoa().getNaturalidade());
				Hibernate.initialize(e.getVisitante().getPessoa().getPais());
				Hibernate.initialize(e.getVisitante().getPessoa().getBiometria());
				if(e.getVisitante().getPessoa().getBiometria() != null) {
					Hibernate.initialize(e.getVisitante().getPessoa().getBiometria().getDedos());
				}


				List<VisitanteResponsavel> listaResponsaveis =
						visitanteResponsavelDAO.buscarListaVisitantesReponsaveis(v);

				List<Visitante> visitantesResponsaveis = new ArrayList<>();

				if(!listaResponsaveis.isEmpty()) {
					for(VisitanteResponsavel vr : listaResponsaveis) {
						Visitante vi = new Visitante();
						vi = visitanteDAO.buscarPorId(vr.getVisitanteResponsavel().getId());
						visitantesResponsaveis.add(vi);
					}

					if(!visitantesResponsaveis.isEmpty()) {
						v.setVisitantesReponsaveis(visitantesResponsaveis);
					}
				}

				Hibernate.initialize(e.getVisitante().getResponsavel());
				if(e.getVisitante().getResponsavel() != null) {
					Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa());
					Hibernate.initialize(e.getVisitante().getResponsavel().getFotos());
					Hibernate.initialize(e.getVisitante().getResponsavel().getIdentidadeDeGenero());
					Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getNacionalidade());
					Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getNaturalidade());
					Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getPais());
					Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getBiometria());
					if(e.getVisitante().getResponsavel().getPessoa().getBiometria() != null) {
						Hibernate.initialize(e.getVisitante().getResponsavel().getPessoa().getBiometria().getDedos());
					}

				}

				Hibernate.initialize(e.getVisitante().getCredenciamentos());
				for(Credenciamento vr : e.getVisitante().getCredenciamentos()) {
					Hibernate.initialize(vr.getFasesDoCredenciamento());
					if(vr.getRecuperando() != null) {
						Hibernate.initialize(vr.getRecuperando().getFaltas());
						Hibernate.initialize(vr.getRecuperando().getPassagens());
						for(Passagem p : vr.getRecuperando().getPassagens()) {
							Hibernate.initialize(p.getLotacao());
							for(PassagemLotacao l : p.getLotacao()) {
								Hibernate.initialize(l.getEstrutura());
								Hibernate.initialize(l.getEstrutura().getUnidadePrisional());
							}
						}
					}
				}

			}
		}
		return visitantes;
	}

	public String validarEntrada(FiltroEntradaSaida f, Long unidade) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		FiltroEntradaSaida fAtual = new FiltroEntradaSaida();
		fAtual.setPessoasIds(f.getPessoasIds());
		fAtual.setData(new Date());
		fAtual.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		List<EntradaSaida> lista = this.buscarEntradasPorFiltro(f, true);
		for(EntradaSaida e : lista) {
			if(SituacaoVisita.EM_ANDAMENTO.equals(e.getSituacao()) && e.getUnidadePrisional().getId() != unidade && e.getDataEntrada().after(di) && e.getDataEntrada().before(df))
				return e.getUnidadePrisional().getNome() + ";" + e.getVisitante().getPessoa().getNome();
		}
		return null;
	}

	public Visitante responsavelDeuEntrada(Long id) {
		try {
			String jpql = "SELECT ea " +
					"FROM EntradaSaida ea " +
					"WHERE ea.visitante.id IN " +
					"(SELECT vr.visitanteResponsavel.id FROM VisitanteResponsavel vr " +
					"WHERE vr.visitante.id = :idVisitante " +
					"AND vr.status = 'A') " +
					"AND ea.dataSaida IS NULL";

			TypedQuery<EntradaSaida> query = em.createQuery(jpql, EntradaSaida.class);
			query.setParameter("idVisitante", id);
			query.setMaxResults(1);
			EntradaSaida entradaSaida = query.getSingleResult();
			return entradaSaida.getVisitante();
		}catch (Exception e){
			return null;
		}
	}

	public Recuperando buscarRecuperandoPorVisitanteCredenciado(Long idVisitante) {

		String jpql = "SELECT r " +
				"FROM Visitante v " +
				"JOIN v.credenciamentos c " +
				"JOIN c.recuperando r " +
				"JOIN v.entradas ses " +
				"WHERE v.id = :idVisitante " +
				"ORDER BY ses.id DESC";


		try{
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);

			query.setParameter("idVisitante", idVisitante);
			query.setMaxResults(1);

			Recuperando recuperando = query.getSingleResult();

			Hibernate.initialize(recuperando.getPessoa());

			return recuperando;
		} catch (Exception e) {
			return null;
		}

	}
	
public EntradaSaida buscarUltimaEntradaSaidaAtivaPorVisitante(Long idVisitante) {
	    
	    String jpql = "SELECT es FROM EntradaSaida es " +
	                  "JOIN FETCH es.recuperandos esr " + 
	                  "JOIN FETCH esr.credenciamento c " +
	                  "JOIN FETCH c.recuperando r " + 
                      "JOIN FETCH r.pessoa p " + 
	                  "WHERE es.visitante.id = :idVisitante " +
	                  "AND es.situacao = :situacaoVisita " +
	                  "ORDER BY es.dataEntrada DESC";

	    try {
	        TypedQuery<EntradaSaida> query = em.createQuery(jpql, EntradaSaida.class);
	        query.setParameter("idVisitante", idVisitante);
	        query.setParameter("situacaoVisita", SituacaoVisita.EM_ANDAMENTO); 
	        query.setMaxResults(1);
	        
	        return query.getSingleResult();
	    } catch (Exception e) {
            e.printStackTrace(); 
	        return null;
	    }
	}
}
