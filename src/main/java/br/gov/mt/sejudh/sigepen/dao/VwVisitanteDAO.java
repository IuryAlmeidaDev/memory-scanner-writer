package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import br.gov.mt.sejudh.sigepen.model.Pessoa;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.primefaces.model.SortOrder;

@RequestScoped
public class VwVisitanteDAO extends BaseDAO<VwVisitante, Long> {


    private static final long serialVersionUID = -8941606440978113368L;

    public int quantidadeVwVisitante(FiltroVwVisitante filtro) {
        List<VwVisitante> object = buscarVwVisitante(filtro, true, 0 , 0, null , null);
        return Math.toIntExact(object.get(0).getCredenciamentosAtivos());
    }
    
    @SuppressWarnings("unchecked")
	public List<VwVisitante> buscarPorFiltro(FiltroVwVisitante filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, em, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(VwVisitante.class));
		List<VwVisitante> lista = criteria.list();
		for(VwVisitante x : lista) {
			if(x.getId() == null)
				x.setId(0L);
		}
		return lista;
	}


    public List<VwVisitante> buscarPorFiltroVW(FiltroVwVisitante filtro, boolean isBuscaRelatorio, int first, int pageSize, String sortField, SortOrder sortOrder) {
        
        List<VwVisitante> lista = buscarVwVisitante(filtro, false, first, pageSize,  sortField,  sortOrder);

        return lista;
    }

    private List<VwVisitante> buscarVwVisitante(FiltroVwVisitante filtro, boolean count, int first, int pageSize, String sortField, SortOrder SQLOrderBy) {
        Session session = em.unwrap(Session.class);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbFiltros = new StringBuilder();
        StringBuilder sbFiltrosPessoa = new StringBuilder();
        StringBuilder sbFiltrosCredenciamento = new StringBuilder();
        
        boolean isFiltrouPessoaDeveBuscarRecuperando = false;
        
        if(filtro.getPessoasIds() != null && !filtro.getPessoasIds().isEmpty()) {
        	sbFiltrosPessoa.append("  AND P.ID IN (");
        	for(Long ids: filtro.getPessoasIds()) {
        		sbFiltrosPessoa.append(ids.toString() + ","); 
        	}
        	sbFiltrosPessoa.append(" 0 )");
        }

        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
        	sbFiltrosPessoa.append("  AND P.NOME LIKE '%' || :nome || '%'");
        }

        if (filtro.getNomeMae() != null && !filtro.getNomeMae().isEmpty()) {
        	sbFiltrosPessoa.append("  AND P.NOME_MAE LIKE '%' || :mae || '%'");
        }

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
        	sbFiltros.append("  AND TBDOCUMENTO.CPF LIKE :cpf");
        	isFiltrouPessoaDeveBuscarRecuperando = true;
        }

        if (filtro.getStatus() != null) {
        	sbFiltros.append("  AND V.STAT_VISITANTE = :status_visitante");
        }
        addFiltroCredenciamento(sbFiltrosCredenciamento, filtro);
        addFiltroRecuperando(sbFiltros, filtro);
        
        sb.append(" WITH CTE AS ");
        sb.append("  (SELECT CRE.ID_VISITANTE visitanteId, ");
        sb.append("          CRE.ID_CREDENCIAMENTO credenciamentoId, ");
        sb.append("          CRE.ID_RECUPERANDO idRecuperando, ");
        sb.append("          CRE.ID_TIPO_VISITA tipoVisitaId, ");
        sb.append("          CRE.CODG_FASE_CREDENCIAL faseCredenciamento, ");
        sb.append("          CRE.CODG_SITUACAO_CREDENCIAL statusCredenciamento, ");
        sb.append("          row_number() OVER(PARTITION BY CRE.ID_VISITANTE ORDER BY CRE.ID_CREDENCIAMENTO DESC) AS RN  ");
        sb.append("     FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE), ");
        sb.append(" DADOS_PESSOAS AS ");
        sb.append("  (SELECT P.ID AS ID, P.NOME AS NOME, P.nome_mae as NOME_MAE ");
        sb.append("     FROM SIGEPEN_OWNER.PESSOA P WHERE 1 = 1 " + sbFiltrosPessoa +  " ) ");
        
        sb.append(" SELECT 	 ");
        if (count) {
            sb.append(" count(p.id) credenciamentosAtivos ");

        } else {
          sb.append(" v.id_visitante           as id, ");	
          sb.append(" v.id_visitante           as idVisitante, ");
          sb.append(" p.nome                   as nome, ");
          sb.append(" p.id                     as idPessoa, ");
          sb.append(" v.data_hora_cadastro     as dataHoraCadastro, ");
          sb.append(" v.stat_visitante         as statusVisitante, ");
          sb.append(" CTE.credenciamentoId     as credenciamentoId, ");
          sb.append(" NULL                     as idRecuperando, ");
          sb.append(" CTE.tipoVisitaId         as tipoVisitaId, ");
          sb.append(" CTE.faseCredenciamento   as faseCredenciamento, ");
          sb.append(" CTE.statusCredenciamento as statusCredenciamento, ");
          sb.append(" null					   as tipoDesligamentoRecuperando, ");
          sb.append(" null					   as dataSaida ");

         
        }
        sb.append("   FROM DADOS_PESSOAS P ");
        sb.append("  INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE v ");
        sb.append("     ON P.ID = V.ID_PESSOA_VISITANTE ");
        sb.append("   LEFT JOIN CTE ");
        sb.append("     ON CTE.visitanteId = V.ID_VISITANTE ");
        sb.append("    AND CTE.RN = 1 ");
        sb.append("   LEFT JOIN (SELECT D.PESSOA_ID PESSOAID, replace(D.NUMERO,' ','') CPF ");
        sb.append("                FROM SIGEPEN_OWNER.DOCUMENTO D ");
        sb.append("               INNER JOIN SIGEPEN_OWNER.TIPO_DOCUMENTO TD ");
        sb.append("                  ON TD.ID = D.TIPO_DOCUMENTO_ID ");
        sb.append("               WHERE TD.DESCRICAO = 'CPF' GROUP BY D.PESSOA_ID , replace(D.NUMERO,' ','') ) TBDOCUMENTO ");
        sb.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
        sb.append("     WHERE 1 = 1 ");

        sb.append(sbFiltros);
        sb.append(sbFiltrosCredenciamento);
 
        if(sbFiltrosPessoa.length() > 0 || isFiltrouPessoaDeveBuscarRecuperando) {
        	
        
        sb.append(" ");
        sb.append(" UNION ALL ");
        sb.append(" ");
        sb.append(" SELECT ");
        if (count) {
            sb.append(" count(p.id) credenciamentosAtivos ");

        } else {
          sb.append(" 0      as id, ");	
          sb.append(" 0      as idVisitante, ");
          sb.append(" p.nome as nome, ");
          sb.append(" p.id   as idPessoa, ");
          sb.append(" null   as dataHoraCadastro, ");
          sb.append(" null   as statusVisitante, ");
          sb.append(" null   as credenciamentoId, ");
          sb.append(" TBRECUPERANDO.recuperando_id   as idRecuperando, ");
          sb.append(" null   as tipoVisitaId, ");
          sb.append(" null   as faseCredenciamento, ");
          sb.append(" null   as statusCredenciamento, ");
          sb.append(" TBRECUPERANDO.tipo_desligamento as tipoDesligamentoRecuperando, ");
          sb.append(" TBRECUPERANDO.DATA_SAIDA as dataSaida ");

        }
        sb.append("   FROM DADOS_PESSOAS P ");
        sb.append("  INNER JOIN (SELECT RE.Id as recuperando_id, RE.PESSOA_ID, PASS.TIPO_DESLIGAMENTO, PASS.DATA_SAIDA ");
        sb.append("                FROM SIGEPEN_OWNER.RECUPERANDO RE ");
        sb.append("                LEFT JOIN (SELECT RECUPERANDO_ID, MAX(ID) MAX_ID ");
        sb.append("                            FROM SIGEPEN_OWNER.PASSAGEM ");
        sb.append("                           GROUP BY RECUPERANDO_ID) P ");
        sb.append("                  ON P.RECUPERANDO_ID = RE.ID ");
        sb.append("                LEFT JOIN SIGEPEN_OWNER.PASSAGEM PASS ");
        sb.append("                  ON PASS.ID = P.MAX_ID  ) TBRECUPERANDO ");
        sb.append("     ON TBRECUPERANDO.PESSOA_ID = P.ID ");
        sb.append("   LEFT JOIN (SELECT D.PESSOA_ID PESSOAID, replace(D.NUMERO,' ','') CPF ");
        sb.append("                FROM SIGEPEN_OWNER.DOCUMENTO D ");
        sb.append("               INNER JOIN SIGEPEN_OWNER.TIPO_DOCUMENTO TD ");
        sb.append("                  ON TD.ID = D.TIPO_DOCUMENTO_ID ");
        sb.append("               WHERE TD.DESCRICAO = 'CPF' GROUP BY D.PESSOA_ID , replace(D.NUMERO,' ','') ) TBDOCUMENTO ");
        sb.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
        sb.append(" INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE V ON P.ID = V.ID_PESSOA_VISITANTE ");
        sb.append(" WHERE NOT EXISTS (SELECT 1 FROM SIGEPEN_OWNER.SGP_VISITANTE VT WHERE VT.ID_PESSOA_VISITANTE = P.ID) ");

        
        sb.append(sbFiltros);
        
        }
        
        StringBuilder sqlFinal = new StringBuilder();

        if(!count) {
        	if (SQLOrderBy.name().equals("DESCENDING")) {
            	sqlFinal.append("SELECT * FROM ( " + sb.toString() + " ) order by " + sortField + " desc " );

        	} else {
            	sqlFinal.append("SELECT * FROM ( " + sb.toString() + " ) order by " + sortField + " asc " );

        	}
        } else {
        	sqlFinal.append("SELECT SUM(credenciamentosAtivos) AS credenciamentosAtivos FROM ( " + sb + " ) " );
        }
        
        SQLQuery query = session.createSQLQuery(sqlFinal.toString());
        if(!count) {
        	query.setFirstResult(first);
            query.setMaxResults(pageSize);
        }
        
        
        if (count) {
            query.addScalar("credenciamentosAtivos", LongType.INSTANCE);
        } else {
            query.addScalar("id", LongType.INSTANCE);
            query.addScalar("idPessoa", LongType.INSTANCE);
            query.addScalar("idVisitante", LongType.INSTANCE);
            query.addScalar("nome");
            query.addScalar("dataHoraCadastro", DateType.INSTANCE);
            query.addScalar("statusVisitante");
            query.addScalar("idRecuperando", LongType.INSTANCE);
            query.addScalar("credenciamentoId", LongType.INSTANCE);
            query.addScalar("tipoVisitaId", LongType.INSTANCE);
            query.addScalar("faseCredenciamento", IntegerType.INSTANCE);
            query.addScalar("statusCredenciamento", IntegerType.INSTANCE);
            query.addScalar("tipoDesligamentoRecuperando");
            query.addScalar("dataSaida", DateType.INSTANCE);
        }

        addParametros(query,filtro);

        query.setResultTransformer(Transformers.aliasToBean(VwVisitante.class));
        List<VwVisitante> visitantes = query.list();
        
        return visitantes;
    }

    private void addParametros(SQLQuery query, FiltroVwVisitante filtro) {
        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            query.setParameter("nome", filtro.getNome());
        }

        if (filtro.getNomeMae() != null && !filtro.getNomeMae().isEmpty()) {
            query.setParameter("mae", filtro.getNomeMae());
        }

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            query.setParameter("cpf", filtro.getCpf());
        }

        if (filtro.getStatus() != null) {
            query.setParameter("status_visitante", filtro.getStatus().name());
        }

        if (filtro.getSituacaoCredenciamento() != null) {
            query.setParameter("CODG_SITUACAO_CREDENCIAL", filtro.getSituacaoCredenciamento().getId());
        }

        if (filtro.getFaseCredenciamento() != null) {
            query.setParameter("CODG_FASE_CREDENCIAL", filtro.getFaseCredenciamento().getId());
        }

        if(filtro.getUnidadePrisional() != null){
            query.setParameter("unidade", filtro.getUnidadePrisional().getId());
        }
    }

    private void addFiltroRecuperando(StringBuilder sb, FiltroVwVisitante filtro) {
        if (filtro.getRecuperando() != null && !filtro.getRecuperando().isEmpty()) {

            sb.append("  AND EXISTS");
            sb.append("  (SELECT 1");
            sb.append("  FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE");
            sb.append("  INNER JOIN SIGEPEN_OWNER.RECUPERANDO R");
            sb.append("  ON R.ID = CRE.ID_RECUPERANDO");
            sb.append("  INNER JOIN SIGEPEN_OWNER.PESSOA P");
            sb.append("  ON P.ID = R.PESSOA_ID");
            sb.append("  INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE V");
            sb.append("  ON V.ID_VISITANTE = CRE.ID_VISITANTE");
            sb.append("  WHERE  P.NOME LIKE '%' || :nome || '%'");
            sb.append(" ) ");
        }
    }
    
    private void addFiltroCredenciamento(StringBuilder sb, FiltroVwVisitante filtro) {
        if (filtro.getUnidadePrisional() != null || filtro.getSituacaoCredenciamento() != null || filtro.getFaseCredenciamento() != null) {
            sb.append("  AND EXISTS (SELECT 1 ");
            sb.append("  FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE ");
            
            sb.append("  INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE V ON V.ID_VISITANTE = CRE.ID_VISITANTE ");

            boolean firstCondition = true;

            if (filtro.getFaseCredenciamento() != null) {
                if (firstCondition) {
                    sb.append("  WHERE CRE.CODG_FASE_CREDENCIAL = :CODG_FASE_CREDENCIAL ");
                    firstCondition = false;
                } 
            }

            if (filtro.getSituacaoCredenciamento() != null) {
                if (firstCondition) {
                    sb.append("  WHERE CRE.CODG_SITUACAO_CREDENCIAL = :CODG_SITUACAO_CREDENCIAL ");
                    firstCondition = false;
                } else {
                    sb.append("  AND CRE.CODG_SITUACAO_CREDENCIAL = :CODG_SITUACAO_CREDENCIAL ");
                }
            }

            if (filtro.getUnidadePrisional() != null) {
                if (firstCondition) {
                    sb.append("  WHERE CRE.ID_UNIDADE_PRISIONAL = :unidade ");
                    firstCondition = false;
                } else {
                    sb.append("  AND CRE.ID_UNIDADE_PRISIONAL = :unidade ");
                }
            }
            
            sb.append(" ) ");
        }
    }



}
