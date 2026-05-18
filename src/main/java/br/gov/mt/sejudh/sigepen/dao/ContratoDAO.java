package br.gov.mt.sejudh.sigepen.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import br.gov.mt.sejudh.sigepen.model.dto.UnidadeContratoConflitoDTO;
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

	/*public int quantidadeFiltrados(FiltroContrato filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Contrato.class, entityManager);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}*/

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

	@SuppressWarnings("unchecked")
	public List<Contrato> listarParaFiltro(boolean listarTodasUnidades, Long unidadeId) {
		if (!listarTodasUnidades && unidadeId == null) {
			return Collections.emptyList();
		}

		StringBuilder jpql = new StringBuilder("select distinct c from Contrato c left join fetch c.fornecedor f ");

		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("join c.unidades u where u.id = :unidadeId ");
		}

		jpql.append("order by c.numContrato");

		Query query = entityManager.createQuery(jpql.toString(), Contrato.class);

		if (!listarTodasUnidades && unidadeId != null) {
			query.setParameter("unidadeId", unidadeId);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> listarAtivosParaFiltro(boolean listarTodasUnidades, Long unidadeId) {
		if (!listarTodasUnidades && unidadeId == null) {
			return Collections.emptyList();
		}

		StringBuilder jpql = new StringBuilder("select distinct c from Contrato c left join fetch c.fornecedor f ");

		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("join c.unidades u ");
		}

		jpql.append("where c.status = 1 ");

		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("and u.id = :unidadeId ");
		}

		jpql.append("order by c.numContrato");

		Query query = entityManager.createQuery(jpql.toString(), Contrato.class);

		if (!listarTodasUnidades && unidadeId != null) {
			query.setParameter("unidadeId", unidadeId);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> listarFornecedoresParaFiltro(boolean listarTodasUnidades, Long unidadeId) {
		return listarCampoTextoParaFiltro("f.nomeFantasia", listarTodasUnidades, unidadeId, null, 0);
	}

	@SuppressWarnings("unchecked")
	public List<String> listarFornecedoresParaFiltro(boolean listarTodasUnidades, Long unidadeId,
			String consulta, int limite) {
		return listarCampoTextoParaFiltro("f.nomeFantasia", listarTodasUnidades, unidadeId, consulta, limite);
	}

	@SuppressWarnings("unchecked")
	public List<String> listarNumerosParaFiltro(boolean listarTodasUnidades, Long unidadeId) {
		return listarCampoTextoParaFiltro("c.numContrato", listarTodasUnidades, unidadeId, null, 0);
	}

	@SuppressWarnings("unchecked")
	public List<String> listarNumerosParaFiltro(boolean listarTodasUnidades, Long unidadeId,
			String consulta, int limite) {
		return listarCampoTextoParaFiltro("c.numContrato", listarTodasUnidades, unidadeId, consulta, limite);
	}

	public List<UnidadePrisional> listarUnidadesPorNumeroContrato(String numeroContrato) {
		if (numeroContrato == null || numeroContrato.trim().isEmpty()) {
			return Collections.emptyList();
		}

		String jpql = "select distinct u from Contrato c join c.unidades u "
				+ "where c.numContrato = :numeroContrato "
				+ "order by u.nome";
		Query query = entityManager.createQuery(jpql, UnidadePrisional.class);
		query.setParameter("numeroContrato", numeroContrato.trim());
		return query.getResultList();
	}

	private List<String> listarCampoTextoParaFiltro(String campo, boolean listarTodasUnidades, Long unidadeId,
			String consulta, int limite) {
		if (!listarTodasUnidades && unidadeId == null) {
			return Collections.emptyList();
		}

		String consultaNormalizada = consulta != null ? consulta.trim().toLowerCase() : null;
		if (consulta != null && consultaNormalizada.isEmpty()) {
			return Collections.emptyList();
		}

		StringBuilder jpql = new StringBuilder("select distinct ");
		jpql.append(campo);
		jpql.append(" from Contrato c left join c.fornecedor f ");
		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("join c.unidades u ");
		}
		jpql.append("where ");
		jpql.append(campo);
		jpql.append(" is not null ");
		if (consultaNormalizada != null) {
			jpql.append("and lower(");
			jpql.append(campo);
			jpql.append(") like :consulta ");
		}
		if (!listarTodasUnidades && unidadeId != null) {
			jpql.append("and u.id = :unidadeId ");
		}
		jpql.append("order by ");
		jpql.append(campo);

		Query query = entityManager.createQuery(jpql.toString(), String.class);
		if (consultaNormalizada != null) {
			query.setParameter("consulta", "%" + consultaNormalizada + "%");
		}
		if (!listarTodasUnidades && unidadeId != null) {
			query.setParameter("unidadeId", unidadeId);
		}
		if (limite > 0) {
			query.setMaxResults(limite);
		}

		return query.getResultList();
	}

	public List<Contrato> buscarContratoVigenteEAtivoPorUnidade(long idUnidade, Date data) {
		String jpql = "select c from Contrato c left join fetch c.fornecedor f left join fetch c.unidades u where u.id = :idUnidade and c.status = 1 and c.vigenciaContratoInicio <= :data and c.vigenciaContratoFim >= :data ";
		Query query = entityManager.createQuery(jpql, Contrato.class);
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("data", data);

		return query.getResultList();
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

        return (List<Contrato>) query.getResultList();
	}

	public void inativarContratosPorFornecedor(long idFornecedor) {
		List<Contrato> contratos = buscarContratoPoFornecedor(idFornecedor);
		contratos.forEach(contrato -> {
			contrato.setStatus(Status.INATIVO);
			contrato.setVigenciaContratoFim(new Date());
			salvar(contrato);
		});
	}

	@SuppressWarnings("unchecked")
	public List<Contrato> validarContratoUnidade(Contrato contrato) {
		StringBuilder s = new StringBuilder("select c from Contrato c ");
		s.append("join fetch c.unidades u ");
		s.append("where (c.status = 1 AND c.vigenciaContratoFim >= SYSDATE) and c.id != :id and u.id in(");
		for (UnidadePrisional unidade : contrato.getUnidades()) {
			s.append(unidade.getId()).append(",");
		}
		s.append("0)");
		Query query = entityManager.createQuery(s.toString(), Contrato.class);
		query.setParameter("id", contrato.getId());

		return (List<Contrato>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<UnidadeContratoConflitoDTO> listarConflitosUnidadeContratoAtivo(Contrato contrato) {
		if (contrato == null || contrato.getUnidades() == null || contrato.getUnidades().isEmpty()) {
			return new ArrayList<UnidadeContratoConflitoDTO>();
		}

		List<Long> unidadeIds = contrato.getUnidades().stream().map(UnidadePrisional::getId).collect(Collectors.toList());

		String jpql = "select distinct new br.gov.mt.sejudh.sigepen.model.dto.UnidadeContratoConflitoDTO(u.id, u.nome, c.numContrato) "
				+ "from Contrato c join c.unidades u "
				+ "where c.status = 1 and c.vigenciaContratoFim >= SYSDATE and c.id != :id and u.id in :unidadeIds";

		Query query = entityManager.createQuery(jpql, UnidadeContratoConflitoDTO.class);
		query.setParameter("id", contrato.getId());
		query.setParameter("unidadeIds", unidadeIds);

		return (List<UnidadeContratoConflitoDTO>) query.getResultList();
	}

	public String horarioRefeiracaoPorUnidade(Long idUnidade, Long tipoRefeiracao) {
		StringBuilder jpql = new StringBuilder(
				"SELECT DISTINCT hr.horario_inicio || ' - ' || hr.horario_fim  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
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
				"SELECT DISTINCT hr.HORARIO_INICIO, hr.HORARIO_FIM, hr.TIPO_REFEICAO_ID  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
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
				"SELECT DISTINCT hr.HORARIO_INICIO, hr.HORARIO_FIM, hr.TIPO_REFEICAO_ID  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
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
				"SELECT DISTINCT hr.horario_inicio || ' - ' || hr.horario_fim  FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID AND cup.UNIDADE_PRISIONAL_ID = :idUnidade ");
		jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".HORARIO_REFEICAO hr ON (hr.CONTRATO_ID = cup.CONTRATO_ID OR hr.UNIDADE_PRISIONAL_ID = cup.UNIDADE_PRISIONAL_ID) AND hr.TIPO_REFEICAO_ID = :tipoRefeiracao ");
		jpql.append("WHERE c.STATUS = 1 AND hr.CONTRATO_ID IS NOT NULL");
		Query query = entityManager.createNativeQuery(jpql.toString());
		query.setParameter("idUnidade", idUnidade);
		query.setParameter("tipoRefeiracao", tipoRefeiracao);
		
		try {
			String horarioRefeicao = (String) query.getSingleResult();
			return horarioRefeicao;
		}catch(Exception e) {
			return null;
		}
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
		List<Contrato> contratos = new ArrayList<>();
		try {
			contratos = (List<Contrato>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}

		return contratos;
	}
	
	public int contarFiltrados(FiltroContrato filtro) {
	    return filtro.contarRegistros(entityManager);
	}
}
