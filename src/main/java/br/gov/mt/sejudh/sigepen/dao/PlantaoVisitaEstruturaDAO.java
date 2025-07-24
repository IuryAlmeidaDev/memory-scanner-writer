package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.PlantaoVisitaEstrutura;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestScoped
public class PlantaoVisitaEstruturaDAO extends BaseDAO<PlantaoVisitaEstrutura, Long>{

	private static final long serialVersionUID = -318366946850533179L;
	
	
	@Inject
	private EntityManager entityManager;
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<PlantaoVisitaEstrutura> buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(Integer tipoEntrada, Long idUnidade, Date date) {
		String sql = "SELECT " +
				"COUNT(CASE WHEN p.SEXO = 'M' THEN 1 END) AS HOMEM, " +
				"COUNT(CASE WHEN p.SEXO = 'F' THEN 1 END) AS MULHER, " +
				"COUNT(CASE WHEN e.ID_VISITANTE_RESPONSAVEL IS NOT NULL THEN 1 END) AS MENOR, " +
				"COUNT(r.MATRICULA) AS RECUPERANDO, " +
				"r.ESTRUTURA, R.ESTRUTURA_ID " +
				"FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_ENTRADA_SAIDA e " +
				"JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_VISITANTE v ON v.ID_VISITANTE = e.ID_VISITANTE " +
				"JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA p ON p.ID = v.ID_PESSOA_VISITANTE " +
				"JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAMENTO c ON c.ID_VISITANTE = e.ID_VISITANTE " +
				"JOIN " + Sigepen.SEJUDH_SCHEMA + ".LISTA_TODOS_RECUPERANDOS r ON r.MATRICULA = c.ID_RECUPERANDO AND c.ID_UNIDADE_PRISIONAL = :idUnidade " +
				"JOIN " + Sigepen.SEJUDH_SCHEMA + ".SGP_TIPO_VISITA tp ON tp.CODG_TIPO_ENTRADA = c.ID_TIPO_VISITA " +
				"WHERE tp.CODG_TIPO_ENTRADA = :tipoEntrada " +
				"AND e.ID_UNIDADE_PRISIONAL = :idUnidade " +
				"AND c.CODG_SITUACAO_CREDENCIAL = :situacaoCredencial " +
				"AND e.DATA_ENTRADA >= :dataInicio " +
				"AND (e.DATA_SAIDA <= TRUNC(SYSDATE) OR e.DATA_SAIDA IS NULL) " +
		"GROUP BY r.ESTRUTURA,R.ESTRUTURA_ID " +
				"ORDER BY r.ESTRUTURA DESC";

		List<PlantaoVisitaEstrutura> lista = new ArrayList<PlantaoVisitaEstrutura>();
		try{
			List<Object[]> resultados = entityManager.createNativeQuery(sql)
					.setParameter("tipoEntrada", tipoEntrada)
					.setParameter("idUnidade", idUnidade)
					.setParameter("situacaoCredencial", StatusCredenciamento.APROVADO.getId())
					.setParameter("dataInicio", date)
					.getResultList();

			for (Object[] row : resultados) {
				PlantaoVisitaEstrutura dto = new PlantaoVisitaEstrutura(
						((Number) row[0]).longValue(),
						((Number) row[1]).longValue(),
						((Number) row[2]).longValue(),
						((Number) row[3]).longValue(),
						(String) row[4],
						((Number) row[5]).longValue()
				);
				lista.add(dto);
			}
		} catch (Exception e) {
			return new ArrayList<PlantaoVisitaEstrutura>();
		}
		return lista;
	}

}
