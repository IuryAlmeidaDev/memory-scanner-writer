package br.gov.mt.sejudh.sigepen.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class ContratoDAO {

	@Inject
	private EntityManager entityManager;

	public Contrato salvar(Contrato contrato) {
		if (contrato.getId() == null || contrato.getId() <= 0) {
			entityManager.persist(contrato);
			return contrato;
		}

		return entityManager.merge(contrato);
	}

	public Contrato buscaPorId(Long id) {

		String jpql = "select c from Contrato c where c.id = :id";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("id", id);
		Contrato contrato = (Contrato) query.getSingleResult();
		Hibernate.initialize(contrato.getFornecedor());
		Hibernate.initialize(contrato.getUnidades());
		Hibernate.initialize(contrato.getHorariosRefeicao());
		Hibernate.initialize(contrato.getJustificativa());

		return contrato;
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> buscarAtivosUnidade(long id) {
		StringBuilder s = new StringBuilder("select c from Contrato c ");
		s.append("join fetch c.unidade u ");
		s.append("where u.id = :id");
		// TODO: Buscar apenas ativos.
		Query query = entityManager.createQuery(s.toString(), Contrato.class);
		query.setParameter("id", id);

		List<Contrato> contratos = query.getResultList();

		return contratos;
	}

	public int quantidadeFiltrados(FiltroContrato filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Contrato.class, entityManager);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<ContratoDTO> buscaPorFiltro(FiltroContrato filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Contrato.class, entityManager);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(ContratoDTO.class));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> listarAtivo() {
		String jpql = "select c from Contrato c where c.status = 1";
		Query query = entityManager.createQuery(jpql, Contrato.class);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> listarTodos() {
		String jpql = "select c from Contrato c";
		Query query = entityManager.createQuery(jpql, Contrato.class);

		return query.getResultList();
	}

	public Contrato buscarContratoPorUnidade(long idUnidade, Date data) {

		String jpql = "select c from Contrato c left join fetch c.fornecedor f left join fetch c.unidades u where u.id = :idUnidade and c.status = 1 and c.vigenciaContratoInicio <= :data and c.vigenciaContratoFim >= :data ";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("data", data);

		Contrato contrato = (Contrato) query.getSingleResult();
		Hibernate.initialize(contrato.getFornecedor());
		Hibernate.initialize(contrato.getUnidades());

		return contrato;
	}

	public Contrato buscarContratoPorNumero(String numeroContrato) {
		try {
			String jpql = "select c from Contrato c left join fetch c.fornecedor f left join fetch c.unidades u where c.status = 1 and c.numContrato = :numeroContrato ";
			Query query = entityManager.createQuery(jpql, Contrato.class);
			query.setParameter("numeroContrato", numeroContrato);

			Contrato contrato = (Contrato) query.getSingleResult();
			Hibernate.initialize(contrato.getFornecedor());
			Hibernate.initialize(contrato.getUnidades());

			return contrato;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> buscarContratoPoFornecedor(long idFornecedor) {

		StringBuilder s = new StringBuilder("select c from Contrato c ");
		s.append("join fetch c.fornecedor f ");
		s.append("where c.fornecedor.id = :idFornecedor");

		Query query = entityManager.createQuery(s.toString(), Contrato.class);
		query.setParameter("idFornecedor", idFornecedor);

		List<Contrato> contratos = query.getResultList();
		return contratos;
	}

	public void inativarContratosPorFornecedor(long idFornecedor) {
		List<Contrato> contratos = buscarContratoPoFornecedor(idFornecedor);
		contratos.forEach(contrato -> {
			contrato.setStatus(Status.INATIVO);
			contrato.setVigenciaContratoFim(new Date());
			salvar(contrato);
		});
	}

	public void ativarContratosPorFornecedor(long idFornecedor) {
		List<Contrato> contratos = buscarContratoPoFornecedor(idFornecedor);
		contratos.forEach(contrato -> {
			contrato.setStatus(Status.ATIVO);
			salvar(contrato);
		});
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> validarContratoUnidade(Contrato contrato) {
		StringBuilder s = new StringBuilder("select c from Contrato c ");
		s.append("join fetch c.unidades u ");
		s.append("where (c.status = 1 AND c.vigenciaContratoFim >= SYSDATE) and c.id != :id and u.id in(");
		for (UnidadePrisional unidade : contrato.getUnidades()) {
			s.append(String.valueOf(unidade.getId()) + ",");
		}
		s.append("0)");
		Query query = entityManager.createQuery(s.toString(), Contrato.class);
		query.setParameter("id", contrato.getId());

		List<Contrato> contratos = query.getResultList();
		return contratos;
	}

	public String horarioRefeiracaoPorUnidade(Long idUnidade, Long tipoRefeiracao) {
		StringBuilder jpql = new StringBuilder(
				"SELECT DISTINCT hr.horario  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID AND cup.UNIDADE_PRISIONAL_ID = :idUnidade ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".HORARIO_REFEICAO hr ON (hr.CONTRATO_ID = cup.CONTRATO_ID OR hr.UNIDADE_PRISIONAL_ID = cup.UNIDADE_PRISIONAL_ID) AND hr.TIPO_REFEICAO_ID = :tipoRefeiracao ");
		jpql.append("WHERE c.STATUS = 1 AND hr.UNIDADE_PRISIONAL_ID = :idUnidade");
		Query query = entityManager.createNativeQuery(jpql.toString());
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("tipoRefeiracao", tipoRefeiracao);
		try {
			String horarioRefeicao = (String) query.getSingleResult();
			return horarioRefeicao;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HorarioRefeicao> horarioRefeiracaoPorUnidade(Long idUnidade) {
		StringBuilder jpql = new StringBuilder(
				"SELECT DISTINCT hr.HORARIO,hr.TIPO_REFEICAO_ID  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID AND cup.UNIDADE_PRISIONAL_ID = :idUnidade ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".HORARIO_REFEICAO hr ON hr.CONTRATO_ID = cup.CONTRATO_ID OR hr.UNIDADE_PRISIONAL_ID = cup.UNIDADE_PRISIONAL_ID ");
		jpql.append("WHERE c.STATUS = 1 AND hr.UNIDADE_PRISIONAL_ID = :idUnidade");
		Query query = entityManager.createNativeQuery(jpql.toString());
		query.setParameter("idUnidade", idUnidade);
		try {
			return (List<HorarioRefeicao>) query.getResultList().stream()
					.map(row -> new HorarioRefeicao((Object[]) row)).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HorarioRefeicao> horarioRefeiracaoPorContratoUnidade(Long idUnidade) {
		StringBuilder jpql = new StringBuilder(
				"SELECT DISTINCT hr.horario  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID AND cup.UNIDADE_PRISIONAL_ID = :idUnidade ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".HORARIO_REFEICAO hr ON hr.CONTRATO_ID = cup.CONTRATO_ID OR hr.UNIDADE_PRISIONAL_ID = cup.UNIDADE_PRISIONAL_ID ");
		jpql.append("WHERE c.STATUS = 1 AND hr.CONTRATO_ID IS NOT NULL");
		Query query = entityManager.createNativeQuery(jpql.toString());
		query.setParameter("idUnidade", idUnidade);
		try {
			return (List<HorarioRefeicao>) query.getResultList().stream()
					.map(row -> new HorarioRefeicao((Object[]) row)).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	public String horarioRefeiracaoPorContratoUnidade(Long idUnidade, Long tipoRefeiracao) {
		StringBuilder jpql = new StringBuilder(
				"SELECT DISTINCT hr.horario  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID AND cup.UNIDADE_PRISIONAL_ID = :idUnidade ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".HORARIO_REFEICAO hr ON (hr.CONTRATO_ID = cup.CONTRATO_ID OR hr.UNIDADE_PRISIONAL_ID = cup.UNIDADE_PRISIONAL_ID) AND hr.TIPO_REFEICAO_ID = :tipoRefeiracao ");
		jpql.append("WHERE c.STATUS = 1 AND hr.CONTRATO_ID IS NOT NULL");
		Query query = entityManager.createNativeQuery(jpql.toString());
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("tipoRefeiracao", tipoRefeiracao);
		String horarioRefeicao = (String) query.getSingleResult();
		return horarioRefeicao;
	}

	@SuppressWarnings("unchecked")
	public Contrato buscarContratoVigente(long idUnidade, String data) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String jpql = "select c from Contrato c left join fetch c.fornecedor f left join fetch c.unidades u where c.status = 1 and u.id = :idUnidade and c.vigenciaContratoInicio <= :data and c.vigenciaContratoFim >= :data";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("data", dateFormat.parse(data));
		Contrato contrato = new Contrato();
		try {
			contrato = (Contrato) query.getSingleResult();
			Hibernate.initialize(contrato.getFornecedor());
			Hibernate.initialize(contrato.getUnidades());
		} catch (NoResultException e) {
			return null;
		}

		return contrato;
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> buscarContratoVigente(long idUnidade, String dataInicio, String dataFim)
			throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String jpql = "select c from Contrato c left join fetch c.fornecedor f left join fetch c.unidades u where u.id = :idUnidade and c.vigenciaContratoInicio <= :dataFim and c.vigenciaContratoFim >= :dataInicio";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("dataInicio", dateFormat.parse(dataInicio));
		query.setParameter("dataFim", dateFormat.parse(dataFim));
		List<Contrato> contratos = new ArrayList<Contrato>();
		try {
			contratos = (List<Contrato>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}

		return contratos;
	}
}
