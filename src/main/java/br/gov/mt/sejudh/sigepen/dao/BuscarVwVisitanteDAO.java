package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.VencimentoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.primefaces.model.SortOrder;

import javax.enterprise.context.RequestScoped;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequestScoped
public class BuscarVwVisitanteDAO extends BaseDAO<VwVisitante, Long> {

    public List<VwVisitante> buscarVwVisitante(FiltroVwVisitante filtro, boolean contar, int primeiro, int tamanhoPagina, String campoOrdenacao, SortOrder ordemSQL) {
        Session sessao = em.unwrap(Session.class);
        StringBuilder consultaSQL = new StringBuilder();

        StringBuilder subselectCTE = criarSubselectCTE();
        StringBuilder subselectDadosPessoas = criarSubselectDadosPessoas(filtro);
        StringBuilder subselectVisitantes = criarSubselectVisitantes(contar, filtro);
        StringBuilder subselectRecuperando = criarSubselectRecuperando(contar, filtro);

        consultaSQL.append(subselectCTE);
        consultaSQL.append(subselectDadosPessoas);
        consultaSQL.append(subselectVisitantes);

        if (subselectRecuperando != null && filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            consultaSQL.append(" UNION ALL ");
            consultaSQL.append(subselectRecuperando);
        }

        StringBuilder sqlFinal = new StringBuilder();
        if (!contar) {
            sqlFinal.append("SELECT * FROM ( ");
            sqlFinal.append("  SELECT subq.*, ROW_NUMBER() OVER (PARTITION BY subq.id ORDER BY subq.id DESC) as rn_final ");
            sqlFinal.append("  FROM ( ").append(consultaSQL.toString()).append(" ) subq ");
            sqlFinal.append(") WHERE rn_final = 1 ");
            
            if (ordemSQL.name().equals("DESCENDING")) {
                sqlFinal.append("ORDER BY ").append(campoOrdenacao).append(" DESC ");
                if (!campoOrdenacao.equals("id")) {
                    sqlFinal.append(", id DESC ");
                }
            } else {
                sqlFinal.append("ORDER BY ").append(campoOrdenacao).append(" ASC ");
                if (!campoOrdenacao.equals("id")) {
                    sqlFinal.append(", id DESC ");
                }
            }
        } else {
            sqlFinal.append("SELECT SUM(credenciamentosAtivos) AS credenciamentosAtivos FROM ( ").append(consultaSQL).append(" ) ");
        }

        SQLQuery query = sessao.createSQLQuery(sqlFinal.toString());
        if (!contar) {
            query.setFirstResult(primeiro);
            query.setMaxResults(tamanhoPagina);
        }

        adicionarParametros(query, filtro, false);
        adicionarScalars(query, contar);

        query.setResultTransformer(Transformers.aliasToBean(VwVisitante.class));
        List<VwVisitante> visitantes = query.list();

        return visitantes;
    }

    
    private StringBuilder criarSubselectCTE() {
        return new StringBuilder(" WITH CTE AS ")
            .append("  (SELECT CRE.ID_VISITANTE visitanteId, ")
            .append("          CRE.ID_CREDENCIAMENTO credenciamentoId, ")
            .append("          CRE.ID_RECUPERANDO idRecuperando, ")
            .append("          CRE.ID_TIPO_VISITA tipoVisitaId, ")
            .append("          CRE.CODG_FASE_CREDENCIAL faseCredenciamento, ")
            .append("          CRE.CODG_SITUACAO_CREDENCIAL statusCredenciamento, ")
            .append("          CRE.DATA_VALIDADE dataValidade, ")
            .append("          row_number() OVER ( ")
            .append("             PARTITION BY CRE.ID_VISITANTE, CRE.ID_RECUPERANDO ")
            .append("             ORDER BY CRE.ID_CREDENCIAMENTO DESC ")
            .append("          ) AS RN ")
            .append("     FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE), ");
    }


    private StringBuilder criarSubselectDadosPessoas(FiltroVwVisitante filtro) {
        StringBuilder filtrosPessoa = getFiltrosPessoa(filtro);
        return new StringBuilder(" DADOS_PESSOAS AS ")
                .append("  (SELECT P.ID AS ID, P.NOME AS NOME, P.nome_mae as NOME_MAE ")
                .append("     FROM SIGEPEN_OWNER.PESSOA P WHERE 1 = 1 ").append(filtrosPessoa).append(" ) ");
    }

    private StringBuilder getFiltrosPessoa(FiltroVwVisitante filtro) {
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

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            filtrosPessoa.append("  AND EXISTS (SELECT 1 FROM SIGEPEN_OWNER.DOCUMENTO D WHERE D.PESSOA_ID = P.ID AND D.NUMERO = :cpf)");
        }
        return filtrosPessoa;
    }

    private StringBuilder criarSubselectVisitantes(boolean contar, FiltroVwVisitante filtro) {
        StringBuilder subselect = new StringBuilder();

        subselect.append(" SELECT ");
        if (contar) {
            subselect.append(" count(DISTINCT p.id) credenciamentosAtivos ");
        } else {
            subselect.append(criarCamposVisitantes());
        }
        subselect.append("   FROM DADOS_PESSOAS P ");
        subselect.append("  INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE v ");
        subselect.append("     ON P.ID = V.ID_PESSOA_VISITANTE ");
        subselect.append("   LEFT JOIN CTE ");
        subselect.append("     ON CTE.visitanteId = V.ID_VISITANTE ");
        subselect.append("    AND CTE.RN = 1 ");
        subselect.append("   LEFT JOIN SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE ");
        subselect.append("     ON CRE.ID_CREDENCIAMENTO = CTE.credenciamentoId "); 
        subselect.append("   LEFT JOIN ").append(criarSubselectDocumento());
        subselect.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
        subselect.append("     WHERE 1 = 1 ");

        adicionarCondicoes(subselect, filtro, false);
        
        if (!contar) {
            subselect.append(" GROUP BY v.id_visitante, p.nome, p.id, v.data_hora_cadastro, ")
                     .append(" v.stat_visitante, CTE.credenciamentoId, CTE.tipoVisitaId, ")
                     .append(" CTE.faseCredenciamento, CTE.statusCredenciamento ");
        }

        return subselect;
    }

    private void adicionarCondicoes(StringBuilder subselect, FiltroVwVisitante filtro, boolean recuperando) {
        if (!recuperando) {
            if (filtro.getSituacaoCredenciamento() != null) {
                subselect.append("       AND CRE.CODG_SITUACAO_CREDENCIAL = :CODG_SITUACAO_CREDENCIAL ");
            }
            if (filtro.getFaseCredenciamento() != null) {
                subselect.append("       AND CRE.CODG_FASE_CREDENCIAL = :CODG_FASE_CREDENCIAL ");
            }
            

            if (filtro.getStatus() != null) {
                subselect.append("  AND V.STAT_VISITANTE = :statusVisitante");
            }
       
            if (filtro.getVencimentoCredenciamento() != null) {
                VencimentoCredenciamento venc = filtro.getVencimentoCredenciamento();
                if (venc == VencimentoCredenciamento.VENCIDOS) {
                    subselect.append(" AND CTE.dataValidade < :dataValidadeFim ");
                } else {
                    subselect.append(" AND CTE.dataValidade BETWEEN :dataValidadeInicio AND :dataValidadeFim ");
                }
            }


            if (filtro.getRecuperando() != null && !filtro.getRecuperando().isEmpty()) {
                subselect.append("  AND EXISTS (SELECT 1 FROM SIGEPEN_OWNER.RECUPERANDO R ")
                        .append("              INNER JOIN SIGEPEN_OWNER.PESSOA PR ON PR.ID = R.PESSOA_ID ")
                        .append("              WHERE R.ID = CRE.ID_RECUPERANDO ")
                        .append("              AND PR.NOME LIKE '%' || :recuperando || '%') ");
            }
        }
        
        if (filtro.getUnidadePrisional() != null) {
            subselect.append("       AND CRE.ID_UNIDADE_PRISIONAL = :unidade ");
        }
    }

    private StringBuilder criarCamposVisitantes() {
        return new StringBuilder(" v.id_visitante           as id, ")
                .append(" v.id_visitante           as idVisitante, ")
                .append(" p.nome                   as nome, ")
                .append(" p.id                     as idPessoa, ")
                .append(" v.data_hora_cadastro     as dataHoraCadastro, ")
                .append(" v.stat_visitante         as statusVisitante, ")
                .append(" CTE.credenciamentoId     as credenciamentoId, ")
                .append(" NULL                     as idRecuperando, ")
                .append(" CTE.tipoVisitaId         as tipoVisitaId, ")
                .append(" CTE.faseCredenciamento   as faseCredenciamento, ")
                .append(" CTE.statusCredenciamento as statusCredenciamento, ")
                .append(" null                     as tipoDesligamentoRecuperando, ")
                .append(" null                     as dataSaida ");
    }

    private StringBuilder criarSubselectDocumento() {
        return new StringBuilder("(SELECT D.PESSOA_ID PESSOAID, replace(D.NUMERO,' ','') CPF ")
                .append("   FROM SIGEPEN_OWNER.DOCUMENTO D ")
                .append("  INNER JOIN SIGEPEN_OWNER.TIPO_DOCUMENTO TD ")
                .append("     ON TD.ID = D.TIPO_DOCUMENTO_ID ")
                .append("  WHERE TD.DESCRICAO = 'CPF' GROUP BY D.PESSOA_ID , replace(D.NUMERO,' ','') ) TBDOCUMENTO");
    }

    private StringBuilder criarSubselectRecuperando(boolean contar, FiltroVwVisitante filtro) {
        StringBuilder subselect = new StringBuilder();

        subselect.append(" SELECT ");
        if (contar) {
            subselect.append(" count(DISTINCT p.id) credenciamentosAtivos ");
        } else {
            subselect.append(criarCamposRecuperando());
        }
        subselect.append("   FROM DADOS_PESSOAS P ");
        subselect.append("  INNER JOIN ").append(criarSubselectRecuperandoPessoa(filtro));
        subselect.append("     ON TBRECUPERANDO.PESSOA_ID = P.ID ");
        subselect.append("   LEFT JOIN ").append(criarSubselectDocumento());
        subselect.append("     ON TBDOCUMENTO.PESSOAID = P.ID ");
        subselect.append(" WHERE NOT EXISTS (SELECT 1 FROM SIGEPEN_OWNER.SGP_VISITANTE VT WHERE VT.ID_PESSOA_VISITANTE = P.ID) ");

        return subselect;
    }

    private StringBuilder criarCamposRecuperando() {
        StringBuilder campos = new StringBuilder();
        campos.append(" 0      as id, ")
                .append(" 0      as idVisitante, ")
                .append(" p.nome as nome, ")
                .append(" p.id   as idPessoa, ")
                .append(" null   as dataHoraCadastro, ")
                .append(" null   as statusVisitante, ")
                .append(" null   as credenciamentoId, ")
                .append(" TBRECUPERANDO.recuperando_id   as idRecuperando, ")
                .append(" null   as tipoVisitaId, ")
                .append(" null   as faseCredenciamento, ")
                .append(" null   as statusCredenciamento, ")
                .append(" TBRECUPERANDO.tipo_desligamento as tipoDesligamentoRecuperando, ")
                .append(" TBRECUPERANDO.DATA_SAIDA as dataSaida ");
        return campos;
    }

    private StringBuilder criarSubselectRecuperandoPessoa(FiltroVwVisitante filtro) {
        StringBuilder subselect = new StringBuilder();
        subselect.append("(SELECT DISTINCT RE.Id as recuperando_id, RE.PESSOA_ID, PASS.TIPO_DESLIGAMENTO, PASS.DATA_SAIDA ")
                .append("   FROM SIGEPEN_OWNER.RECUPERANDO RE ")
                .append("   LEFT JOIN SIGEPEN_OWNER.SGP_CREDENCIAMENTO CRE ")
                .append("     ON CRE.ID_RECUPERANDO = RE.ID ")
                .append("   INNER JOIN SIGEPEN_OWNER.PESSOA P ")
                .append("     ON P.ID = RE.PESSOA_ID ")
                .append("   LEFT JOIN (SELECT RECUPERANDO_ID, MAX(ID) MAX_ID ")
                .append("               FROM SIGEPEN_OWNER.PASSAGEM ")
                .append("              GROUP BY RECUPERANDO_ID) P ")
                .append("     ON P.RECUPERANDO_ID = RE.ID ")
                .append("   LEFT JOIN SIGEPEN_OWNER.PASSAGEM PASS ")
                .append("     ON PASS.ID = P.MAX_ID ")
                .append("  WHERE 1 = 1 ");

        adicionarCondicoes(subselect, filtro, true);
        
        subselect.append(") TBRECUPERANDO");
        return subselect;
    }

    private void adicionarParametros(SQLQuery query, FiltroVwVisitante filtro, boolean isRecuperando) {
        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            query.setParameter("nome", filtro.getNome());
        }

        if (filtro.getRecuperando() != null && !filtro.getRecuperando().isEmpty()) {
            query.setParameter("recuperando", filtro.getRecuperando());
        }

        if (filtro.getNomeMae() != null && !filtro.getNomeMae().isEmpty()) {
            query.setParameter("nomeMae", filtro.getNomeMae());
        }

        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            query.setParameter("cpf", filtro.getCpf());
        }

        if (filtro.getStatus() != null && !isRecuperando) {
            query.setParameter("statusVisitante", filtro.getStatus().name());
        }

        if (filtro.getSituacaoCredenciamento() != null) {
            query.setParameter("CODG_SITUACAO_CREDENCIAL", filtro.getSituacaoCredenciamento().getId());
        }

        if (filtro.getFaseCredenciamento() != null) {
            query.setParameter("CODG_FASE_CREDENCIAL", filtro.getFaseCredenciamento().getId());
        }

        if (filtro.getVencimentoCredenciamento() != null && !isRecuperando) {
            VencimentoCredenciamento venc = filtro.getVencimentoCredenciamento();
            LocalDate hoje = LocalDate.now();
            if (venc == VencimentoCredenciamento.VENCIDOS) {
                Date dataValidadeFim = Date.from(hoje.atStartOfDay(ZoneId.systemDefault()).toInstant());
                query.setParameter("dataValidadeFim", dataValidadeFim);
            } else {
                Date dataValidadeInicio = Date.from(hoje.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date dataValidadeFim = Date.from(hoje.plusDays(venc.getDias()).atStartOfDay(ZoneId.systemDefault()).toInstant());
                query.setParameter("dataValidadeInicio", dataValidadeInicio);
                query.setParameter("dataValidadeFim", dataValidadeFim);
            }
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
