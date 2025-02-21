package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.primefaces.model.SortOrder;

import javax.enterprise.context.RequestScoped;
import java.util.List;

@RequestScoped
public class BuscarVwVisitanteDAO extends BaseDAO<VwVisitante, Long> {

    public List<VwVisitante> buscarVwVisitante(FiltroVwVisitante filtro, boolean contar, int primeiro, int tamanhoPagina, String campoOrdenacao, SortOrder ordemSQL) {
        Session sessao = em.unwrap(Session.class);
        StringBuilder consultaSQL = new StringBuilder();

        String subselectCTE = criarSubselectCTE();
        String subselectDadosPessoas = criarSubselectDadosPessoas(filtro);
        String subselectVisitantes = criarSubselectVisitantes(contar, filtro);
        String subselectRecuperando = criarSubselectRecuperando(contar, filtro);

        consultaSQL.append(subselectCTE);
        consultaSQL.append(subselectDadosPessoas);
        consultaSQL.append(subselectVisitantes);

        if (subselectRecuperando != null) {
            consultaSQL.append(" UNION ALL ");
            consultaSQL.append(subselectRecuperando);
        }

        StringBuilder sqlFinal = new StringBuilder();
        if (!contar) {
            if (ordemSQL.name().equals("DESCENDING")) {
                sqlFinal.append("SELECT * FROM ( ").append(consultaSQL.toString()).append(" ) order by ").append(campoOrdenacao).append(" desc ");
            } else {
                sqlFinal.append("SELECT * FROM ( ").append(consultaSQL.toString()).append(" ) order by ").append(campoOrdenacao).append(" asc ");
            }
        } else {
            sqlFinal.append("SELECT SUM(credenciamentosAtivos) AS credenciamentosAtivos FROM ( ").append(consultaSQL).append(" ) ");
        }

        SQLQuery query = sessao.createSQLQuery(sqlFinal.toString());
        if (!contar) {
            query.setFirstResult(primeiro);
            query.setMaxResults(tamanhoPagina);
        }

        adicionarParametros(query, filtro);
        adicionarScalars(query, contar);

        query.setResultTransformer(Transformers.aliasToBean(VwVisitante.class));
        List<VwVisitante> visitantes = query.list();

        return visitantes;
    }

    private String criarSubselectCTE() {
        return " WITH CTE AS " +
               "  (SELECT CRE.ID_VISITANTE visitanteId, " +
               "          CRE.ID_CREDENCIAMENTO credenciamentoId, " +
               "          CRE.ID_RECUPERANDO idRecuperando, " +
               "          CRE.ID_TIPO_VISITA tipoVisitaId, " +
               "          CRE.CODG_FASE_CREDENCIAL faseCredenciamento, " +
               "          CRE.CODG_SITUACAO_CREDENCIAL statusCredenciamento, " +
               "          row_number() OVER(PARTITION BY CRE.ID_VISITANTE ORDER BY CRE.ID_CREDENCIAMENTO DESC) AS RN  " +
               "     FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE), ";
    }

    private String criarSubselectDadosPessoas(FiltroVwVisitante filtro) {
        StringBuilder filtrosPessoa = new StringBuilder();

        if (filtro.getPessoasIds() != null && !filtro.getPessoasIds().isEmpty()) {
            filtrosPessoa.append("  AND P.ID IN (");
            for (Long id : filtro.getPessoasIds()) {
                filtrosPessoa.append(id.toString()).append(",");
            }
            filtrosPessoa.append(" 0 )");
        }

        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            filtrosPessoa.append("  AND P.NOME LIKE '%' || :nome || '%'");
        }

        if (filtro.getNomeMae() != null && !filtro.getNomeMae().isEmpty()) {
            filtrosPessoa.append("  AND P.NOME_MAE LIKE '%' || :nomeMae || '%'");
        }

        return " DADOS_PESSOAS AS " +
               "  (SELECT P.ID AS ID, P.NOME AS NOME, P.nome_mae as NOME_MAE " +
               "     FROM SIGEPEN_OWNER.PESSOA P WHERE 1 = 1 " + filtrosPessoa + " ) ";
    }

    private String criarSubselectVisitantes(boolean contar, FiltroVwVisitante filtro) {
        StringBuilder subselect = new StringBuilder();

        subselect.append(" SELECT ");
        if (contar) {
            subselect.append(" count(p.id) credenciamentosAtivos ");
        } else {
            subselect.append(criarCamposVisitantes());
        }
        subselect.append("   FROM DADOS_PESSOAS P ");
        subselect.append("  INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE v ");
        subselect.append("     ON P.ID = V.ID_PESSOA_VISITANTE ");
        subselect.append("   LEFT JOIN SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE ");
        subselect.append("     ON CRE.ID_VISITANTE = V.ID_VISITANTE ");
        subselect.append("   LEFT JOIN CTE ");
        subselect.append("     ON CTE.visitanteId = V.ID_VISITANTE ");
        subselect.append("    AND CTE.RN = 1 ");
        subselect.append("   LEFT JOIN ").append(criarSubselectDocumento());
        subselect.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
        subselect.append("     WHERE 1 = 1 ");

        adicionarCondicoes(subselect, filtro);

        return subselect.toString();
    }

    private void adicionarCondicoes(StringBuilder subselect, FiltroVwVisitante filtro) {
        if (filtro.getSituacaoCredenciamento() != null) {
            subselect.append("       AND CRE.CODG_SITUACAO_CREDENCIAL = :CODG_SITUACAO_CREDENCIAL ");
        }
        if (filtro.getFaseCredenciamento() != null) {
            subselect.append("       AND CRE.CODG_FASE_CREDENCIAL = :CODG_FASE_CREDENCIAL ");
        }
        if (filtro.getUnidadePrisional() != null) {
            subselect.append("       AND CRE.ID_UNIDADE_PRISIONAL = :unidade ");
        }
    }

    private String criarCamposVisitantes() {
        return " v.id_visitante           as id, " +
               " v.id_visitante           as idVisitante, " +
               " p.nome                   as nome, " +
               " p.id                     as idPessoa, " +
               " v.data_hora_cadastro     as dataHoraCadastro, " +
               " v.stat_visitante         as statusVisitante, " +
               " CTE.credenciamentoId     as credenciamentoId, " +
               " NULL                     as idRecuperando, " +
               " CTE.tipoVisitaId         as tipoVisitaId, " +
               " CTE.faseCredenciamento   as faseCredenciamento, " +
               " CTE.statusCredenciamento as statusCredenciamento, " +
               " null                     as tipoDesligamentoRecuperando, " +
               " null                     as dataSaida ";
    }

    private String criarSubselectDocumento() {
        return "(SELECT D.PESSOA_ID PESSOAID, replace(D.NUMERO,' ','') CPF " +
               "   FROM SIGEPEN_OWNER.DOCUMENTO D " +
               "  INNER JOIN SIGEPEN_OWNER.TIPO_DOCUMENTO TD " +
               "     ON TD.ID = D.TIPO_DOCUMENTO_ID " +
               "  WHERE TD.DESCRICAO = 'CPF' GROUP BY D.PESSOA_ID , replace(D.NUMERO,' ','') ) TBDOCUMENTO";
    }

    private String criarSubselectRecuperando(boolean contar, FiltroVwVisitante filtro) {
        StringBuilder filtrosRecuperando = new StringBuilder();

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            filtrosRecuperando.append("  AND TBDOCUMENTO.CPF LIKE :cpf");
        }

        if (filtro.getStatus() != null) {
            filtrosRecuperando.append("  AND V.STAT_VISITANTE = :statusVisitante");
        }

        if (filtrosRecuperando.length() > 0) {
            StringBuilder subselect = new StringBuilder();

            subselect.append(" SELECT ");
            if (contar) {
                subselect.append(" count(p.id) credenciamentosAtivos ");
            } else {
                subselect.append(criarCamposRecuperando());
            }
            subselect.append("   FROM DADOS_PESSOAS P ");
            subselect.append("  INNER JOIN ").append(criarSubselectRecuperandoPessoa());
            subselect.append("     ON TBRECUPERANDO.PESSOA_ID = P.ID ");
            subselect.append("   LEFT JOIN ").append(criarSubselectDocumento());
            subselect.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
            subselect.append(" INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE V ON P.ID = V.ID_PESSOA_VISITANTE ");
            subselect.append(" WHERE NOT EXISTS (SELECT 1 FROM SIGEPEN_OWNER.SGP_VISITANTE VT WHERE VT.ID_PESSOA_VISITANTE = P.ID) ");
            subselect.append(filtrosRecuperando);

            return subselect.toString();
        }

        return null;
    }

    private String criarCamposRecuperando() {
        return " 0      as id, " +
               " 0      as idVisitante, " +
               " p.nome as nome, " +
               " p.id   as idPessoa, " +
               " null   as dataHoraCadastro, " +
               " null   as statusVisitante, " +
               " null   as credenciamentoId, " +
               " TBRECUPERANDO.recuperando_id   as idRecuperando, " +
               " null   as tipoVisitaId, " +
               " null   as faseCredenciamento, " +
               " null   as statusCredenciamento, " +
               " TBRECUPERANDO.tipo_desligamento as tipoDesligamentoRecuperando, " +
               " TBRECUPERANDO.DATA_SAIDA as dataSaida ";
    }

    private String criarSubselectRecuperandoPessoa() {
        return "(SELECT RE.Id as recuperando_id, RE.PESSOA_ID, PASS.TIPO_DESLIGAMENTO, PASS.DATA_SAIDA " +
               "   FROM SIGEPEN_OWNER.RECUPERANDO RE " +
               "   LEFT JOIN (SELECT RECUPERANDO_ID, MAX(ID) MAX_ID " +
               "               FROM SIGEPEN_OWNER.PASSAGEM " +
               "              GROUP BY RECUPERANDO_ID) P " +
               "     ON P.RECUPERANDO_ID = RE.ID " +
               "   LEFT JOIN SIGEPEN_OWNER.PASSAGEM PASS " +
               "     ON PASS.ID = P.MAX_ID  ) TBRECUPERANDO";
    }

    private void adicionarParametros(SQLQuery query, FiltroVwVisitante filtro) {
        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            query.setParameter("nome", filtro.getNome());
        }

        if (filtro.getNomeMae() != null && !filtro.getNomeMae().isEmpty()) {
            query.setParameter("nomeMae", filtro.getNomeMae());
        }

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            query.setParameter("cpf", filtro.getCpf());
        }

        if (filtro.getStatus() != null) {
            query.setParameter("statusVisitante", filtro.getStatus().name());
        }

        if (filtro.getSituacaoCredenciamento() != null) {
            query.setParameter("CODG_SITUACAO_CREDENCIAL", filtro.getSituacaoCredenciamento().getId());
        }

        if (filtro.getFaseCredenciamento() != null) {
            query.setParameter("CODG_FASE_CREDENCIAL", filtro.getFaseCredenciamento().getId());
        }

        if (filtro.getUnidadePrisional() != null) {
            query.setParameter("unidade", filtro.getUnidadePrisional().getId());
        }
    }

    private void adicionarScalars(SQLQuery query, boolean contar) {
        if (contar) {
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
    }
}
