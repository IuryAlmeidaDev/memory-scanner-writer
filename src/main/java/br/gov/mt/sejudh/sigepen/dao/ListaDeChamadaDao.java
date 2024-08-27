package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.relatorio.EstruturaRelatorio;
import br.gov.mt.sejudh.sigepen.model.relatorio.RecuperandoRelatorio;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@RequestScoped
public class ListaDeChamadaDao {
    @Inject
    protected EntityManager em;

    public List<EstruturaRelatorio> listarEstrutura(long id) {
        Session session = em.unwrap(Session.class);

        String jpql = "SELECT e1.id as idEstrutura, " +
                "    e1.nome as nomeEstrutura, " +
                "    e1.cela, " +
                "    (SELECT count(r.id) " +
                "    FROM sigepen_owner.estrutura e, " +
                "        sigepen_owner.passagem_lotacao plo, " +
                "        sigepen_owner.passagem p, " +
                "        sigepen_owner.recuperando r " +
                "    WHERE plo.estrutura_id(+) = e.id " +
                "      AND plo.passagem_id = p.id(+) " +
                "      AND plo.data_saida IS NULL " +
                "      AND p.data_saida IS NULL " +
                "      AND p.recuperando_id = r.id(+) " +
                "    START WITH e.id = e1.id " +
                "    CONNECT BY PRIOR e.id = e.estruturapai_id) quantidade  " +
                "FROM sigepen_owner.estrutura e1  " +
                "START WITH e1.id = :id  " +
                "CONNECT BY PRIOR e1.id = e1.estruturapai_id";

        SQLQuery query = session.createSQLQuery(jpql);
        addScalarEstrutura(query);
        query.setParameter("id", id);
        query.setResultTransformer(Transformers.aliasToBean(EstruturaRelatorio.class));
        return query.list();
    }


    public List<RecuperandoRelatorio> listarRecuperando(Long idEstrutura) {
        Session session = em.unwrap(Session.class);


        String jpql = "SELECT DISTINCT(SIGEPEN_OWNER.BUSCAESTRUTURANOMECOMPLETO(estrutura_id)) nomeCela, " +
                "    recup.id as matricula, " +
                "    recup.nome as nomeRecuperando, " +
                "    recup.nome_social as nomeSocial, " +
                "    recup.nome_mae as nomeMae, " +
                "    recup.data_nascimento as dataNascimento, " +
                "    recup.em_movimento  as emMovimento " +
                "FROM (SELECT r.id, " +
                "    p.nome, " +
                "    p.nome_social, " +
                "    p.nome_mae, " +
                "    p.data_nascimento, " +
                "    es.id    estrutura_id, " +
                "    (CASE " +
                "         WHEN pt.status IN ('EM_ANDAMENTO', 'SUSPENSO') THEN " +
                "             'SIM' " +
                "         ELSE " +
                "             'NAO' " +
                "        END) em_movimento " +
                "FROM sigepen_owner.estrutura es, " +
                "    sigepen_owner.recuperando r, " +
                "    sigepen_owner.pessoa p, " +
                "    sigepen_owner.passagem pa, " +
                "    sigepen_owner.passagem_lotacao pal, " +
                "    sigepen_owner.pedido_transito pt " +
                "WHERE r.pessoa_id = p.id(+) " +
                "  AND pa.id(+) = pal.passagem_id " +
                "  AND pal.estrutura_id(+) = es.id " +
                "  AND pa.recuperando_id = r.id(+) " +
                "  AND pal.data_saida IS NULL " +
                "  AND pt.recuperando_id(+) = r.id " +
                "  AND pt.data_conclusao(+) IS NULL " +
                "  AND pt.status(+) IN ('SUSPENSO', 'EM_ANDAMENTO') " +
                "  AND pa.recuperando_id IS NOT NULL " +
                "  AND es.id = :id " +
                "ORDER BY es.estruturapai_id, es.id, p.nome) recup " +
                "ORDER BY nomeRecuperando";

        SQLQuery query = session.createSQLQuery(jpql);
        addScalarRecuperando(query);
        query.setParameter("id", idEstrutura);
        query.setResultTransformer(Transformers.aliasToBean(RecuperandoRelatorio.class));
        return query.list();
    }

    private void addScalarRecuperando(SQLQuery query) {
        query.addScalar("nomeCela", StringType.INSTANCE)
                .addScalar("matricula", LongType.INSTANCE)
                .addScalar("nomeRecuperando", StringType.INSTANCE)
                .addScalar("nomeSocial", StringType.INSTANCE)
                .addScalar("nomeMae", StringType.INSTANCE)
                .addScalar("dataNascimento", DateType.INSTANCE)
                .addScalar("emMovimento", StringType.INSTANCE);
    }

    private void addScalarEstrutura(SQLQuery query) {
        query.addScalar("idEstrutura", LongType.INSTANCE)
                .addScalar("nomeEstrutura", StringType.INSTANCE)
                .addScalar("cela", BooleanType.INSTANCE)
                .addScalar("quantidade", IntegerType.INSTANCE);
    }

    public List<RecuperandoRelatorio> listarRecuperandoComFoto(Long idEstrutura) {
        Session session = em.unwrap(Session.class);

        String jpql =
      " SELECT (SIGEPEN_OWNER.BUSCAESTRUTURANOMECOMPLETO(estrutura_id)) nomeCela, " +
                "    recup.id matricula, " +
                "    recup.nome nomeRecuperando, " +
                "    recup.nome_social nomeSocial, " +
                "    recup.nome_mae nomeMae, " +
                "    recup.data_nascimento dataNascimento, " +
                "    recup.em_movimento emMovimento, " +
                "    recup.foto " +
                " FROM (SELECT r.id, " +
                "    p.nome, " +
                "    p.nome_social, " +
                "    p.nome_mae, " +
                "    p.data_nascimento, " +
                "    es.id     estrutura_id, " +
                "    (CASE " +
                "         WHEN pt.status IN ('EM_ANDAMENTO', 'SUSPENSO') THEN " +
                "             'SIM' " +
                "         ELSE " +
                "             'NAO' " +
                "        END)  em_movimento, " +
                "    i.arquivo foto " +
                " FROM sigepen_owner.estrutura es, " +
                "    sigepen_owner.recuperando r, " +
                "    sigepen_owner.pessoa p, " +
                "    sigepen_owner.passagem pa, " +
                "    sigepen_owner.passagem_lotacao pal, " +
                "    sigepen_owner.pedido_transito pt, " +
                "    sigepen_owner.foto f, " +
                "    sigepen_owner.imagem i " +
                " WHERE r.pessoa_id = p.id(+) " +
                "  AND pa.id(+) = pal.passagem_id " +
                "  AND pal.estrutura_id(+) = es.id " +
                "  AND pa.recuperando_id = r.id(+) " +
                "  AND pal.data_saida IS NULL " +
                "  AND pt.recuperando_id(+) = r.id " +
                "  AND pt.data_conclusao(+) IS NULL " +
                "  AND pt.status(+) IN ('SUSPENSO', 'EM_ANDAMENTO') " +
                "  AND f.recuperando_id(+) = r.id " +
                "  AND f.imagem_id = i.id(+) " +
                "  AND pa.recuperando_id IS NOT NULL " +
                "  AND f.is_atual = 1 " +
                "  AND f.tipo = 1 " +
                " START WITH es.id = :id " +
                " CONNECT BY PRIOR es.id = es.estruturapai_id " +
                " ORDER BY es.estruturapai_id, es.id, p.nome) recup " +
                "ORDER BY nomeRecuperando";



        SQLQuery query = session.createSQLQuery(jpql);
        addScalarRecuperando(query);
        query.setParameter("id", idEstrutura);
        query.addScalar("foto", StandardBasicTypes.BINARY);
        query.setResultTransformer(Transformers.aliasToBean(RecuperandoRelatorio.class));

        return query.list();
    }
}
