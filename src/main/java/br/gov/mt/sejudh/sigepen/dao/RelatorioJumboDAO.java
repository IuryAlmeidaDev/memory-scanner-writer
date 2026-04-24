package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class RelatorioJumboDAO {

    @Inject
    private EntityManager em;

    public List<RelatorioJumboDTO> buscar(FiltroRelatorioJumbo filtro) {
        return executar(filtro, true);
    }

    public List<RelatorioJumboDTO> buscarRaw(FiltroRelatorioJumbo filtro) {
        return executar(filtro, false);
    }

    private List<Object[]> executarQuery(FiltroRelatorioJumbo filtro) {
        Query query = em.createNativeQuery(montarSql());
        query.setParameter("unidadesSelecionadas", filtro.getUnidadesSelecionadas());

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = query.getResultList();
        return resultados;
    }

    private String montarSql() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct ");
        sql.append("   ltr.matricula, ");
        sql.append("   ltr.nome, ");
        sql.append("   ltr.nome_mae, ");
        sql.append("   ltr.unidade, ");
        sql.append("   ltr.estrutura ");
        sql.append(" from ").append(Sigepen.SEJUDH_SCHEMA).append(".lista_todos_recuperandos ltr ");
        sql.append(" where ltr.status = 'Ativo' ");
        sql.append("   and ltr.id_unidade in (:unidadesSelecionadas) ");
        sql.append(" order by ltr.unidade, ltr.estrutura, ltr.nome asc ");
        return sql.toString();
    }

    private List<RelatorioJumboDTO> executar(FiltroRelatorioJumbo filtro, boolean formatarTitleCase) {
        List<Object[]> resultados = executarQuery(filtro);

        List<RelatorioJumboDTO> retorno = new ArrayList<>();
        resultados.forEach(registro -> {
            if (registro[0] == null) {
                return;
            }
            Long matricula = ((BigDecimal) registro[0]).longValue();
            String nome = (String) registro[1];
            String nomeMae = (String) registro[2];
            String unidade = (String) registro[3];
            String estrutura = (String) registro[4];

            retorno.add(formatarTitleCase
                    ? new RelatorioJumboDTO(matricula, nome, nomeMae, unidade, estrutura)
                    : semFormatacao(matricula, nome, nomeMae, unidade, estrutura));
        });

        return retorno;
    }

    private RelatorioJumboDTO semFormatacao(Long matricula, String nome, String nomeMae,
                                            String unidade, String estrutura) {
        RelatorioJumboDTO dto = new RelatorioJumboDTO();
        dto.setMatricula(matricula);
        dto.setNomeRecuperando(nome);
        dto.setNomeMae(nomeMae);
        dto.setUnidadePrisional(unidade);
        dto.setLocalizacao(estrutura);
        return dto;
    }
}
