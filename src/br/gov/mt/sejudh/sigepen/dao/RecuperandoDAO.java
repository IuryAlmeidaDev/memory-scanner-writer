package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;

@RequestScoped
public class RecuperandoDAO {
	
	@Inject
	private EntityManager em;

	public int buscarQuantidadeFiltrada(FiltroRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroRecuperando filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, em, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(RecuperandoDTO.class));
		return criteria.list();
	}
	
	public Recuperando salvar(Recuperando recuperando) {
		if (recuperando.getId() > 0) {			
			return em.merge(recuperando);
		} else {
			em.persist(recuperando);
		}
		
		return recuperando;
	}

	public void excluir(long idRecuperando) {
		Recuperando r = buscarRecuperandoPorId(idRecuperando);
		em.remove(em.merge(r));
	}

	public Recuperando buscarRecuperandoSimplesPorId(long idRecuperando) {
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.Recuperando(r.id,r.pessoa.nome) from Recuperando r where r.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idRecuperando);
			
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Recuperando buscarRecuperandoSimplesPorIdPessoa(long idPessoa) throws ValidacaoException {
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.Recuperando(r.id,r.pessoa.nome) from Recuperando r where r.pessoa.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idPessoa);
			
			return query.getSingleResult();
		} catch (NoResultException e) {
			throw new ValidacaoException("Pessoa " + idPessoa + " não encontrada.");
		}
	}
	
	public Recuperando buscarRecuperandoPorId(long idRecuperando) {
		try {
			String jpql="select r from Recuperando r where r.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idRecuperando);			
			Recuperando r = query.getSingleResult();
			Hibernate.initialize(r.getFotos());
			Hibernate.initialize(r.getParticularidades());
			Hibernate.initialize(r.getAlcunhas());
			Hibernate.initialize(r.getOutrosNomes());
			Hibernate.initialize(r.getDocumentos());
			Hibernate.initialize(r.getParentes());
			Hibernate.initialize(r.getOrientacaoSexual());
			Hibernate.initialize(r.getIdentidadeDeGenero());
			Hibernate.initialize(r.getEscolaridade());
			Hibernate.initialize(r.getRaca());
			Hibernate.initialize(r.getEstadoCivil());
			Hibernate.initialize(r.getTipoSanguineo());
			Hibernate.initialize(r.getReligiao());
			Hibernate.initialize(r.getPessoa());
			Hibernate.initialize(r.getPessoa().getBiometria());
			Hibernate.initialize(r.getPessoa().getNaturalidade());
			Hibernate.initialize(r.getPessoa().getNacionalidade());
			Hibernate.initialize(r.getPessoa().getPais());
			Hibernate.initialize(r.getPessoa().getDocumentos());
			Hibernate.initialize(r.getPessoa().getEnderecos());
			Hibernate.initialize(r.getPessoa().getContatos());
			Hibernate.initialize(r.getPessoa().getAutorizacaoCadastro());
			Hibernate.initialize(r.getProcessos());
			Hibernate.initialize(r.getPassagens());
			Hibernate.initialize(r.getSituacaoCadastro());
			Hibernate.initialize(r.getSituacaoCadastroCompleto());
			return r;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Foto salvarFoto(Foto f) {
		if (f.getId() <= 0) {
			em.persist(f);
		} else {
			f = em.merge(f);
		}
		
		return f;
	}
	
	public Particularidade salvarParticularidade(Particularidade p) {
		em.persist(p);
		return p;
	}
	
	public void excluirFoto(Foto f) {
		em.remove(em.merge(f));
	}
	
	public void excluirParticularidade(Particularidade p) {
		em.remove(em.merge(p));
	}
	
	/*
	 * Parentes
	 */
	public void salvarParente(Parente parente) {
		if(parente.getId()>0){
			parente = em.merge(parente);
		}else{
			em.persist(parente);	
		}
	}

	public void excluirParente(Parente parente) {
		em.remove(em.merge(parente));
	}
	
	/*
	 * Alcunha
	 */
	
	/*
	 * Outros Nomes
	 */
	public void salvarOutroNome(OutroNome outroNome) {
		if(outroNome.getId()>0){
			outroNome = em.merge(outroNome);
		}else{
			em.persist(outroNome);	
		}
	}

	public void excluirOutroNome(OutroNome outroNome) {
		em.remove(em.merge(outroNome));
	}
	
	/*
	 * Alcunha
	 */
	public void salvarAlcunha(Alcunha alcunha) {
		if(alcunha.getId()>0){
			alcunha = em.merge(alcunha);
		}else{
			em.persist(alcunha);	
		}
	}

	public void excluirAlcunha(Alcunha alcunha) {
		em.remove(em.merge(alcunha));
	}
	
	public void atualizarAlcunhaPrincipal(boolean p, long idRecuperando){
		String jpql="update Alcunha a set  a.principal = :principal where a.recuperando.id = :idRec";
		Query query = em.createQuery(jpql);
		query.setParameter("principal", p);
		query.setParameter("idRec", idRecuperando);
		query.executeUpdate();
	}
	
	public void atualizarAlteracaoParentes(Date data, long idRecuperando){
		String jpql="update Recuperando r set  r.alteracaoParentes = :data where r.id = :idRec";
		Query query = em.createQuery(jpql);
		query.setParameter("data", data);
		query.setParameter("idRec", idRecuperando);
		query.executeUpdate();
	}

	public List<Foto> listarFotos(long idRecuperando) {
		TypedQuery<Foto> query = em.createQuery("select f from Foto f where f.recuperando.id = :idRecuperando", Foto.class);
		query.setParameter("idRecuperando", idRecuperando);
		return query.getResultList();
	}
	
	public boolean fotoVinculada(Foto foto) {
		Query query = em.createQuery("select count(p) from PedidoTransito p where p.foto.id = :idFoto");
		query.setParameter("idFoto", foto.getId());
		long contador = (Long)query.getSingleResult();
		Query query2 = em.createQuery("select count(r) from Remanejamento r where r.foto.id = :idFoto");
		query2.setParameter("idFoto", foto.getId());
		Long contador2 = (Long)query2.getSingleResult();
		long count = contador + contador2;
		return count>0;
	}
	
	public Estrutura lotacaoAtual(Recuperando recuperando) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("select e ");
		jpql.append("from Recuperando r, Passagem p, PassagemLotacao l, Estrutura e left join fetch e.unidadePrisional, UnidadePrisional u ");
		jpql.append("where r.id = :idRecuperando ");
		jpql.append("and p.recuperando.id = r.id ");
		jpql.append("and p.dataSaida is null ");
		jpql.append("and l.passagem.id = p.id ");
		jpql.append("and l.dataSaida is null ");
		jpql.append("and l.estrutura.id = e.id ");
		jpql.append("and e.unidadePrisional.id = u.id ");
		
		TypedQuery<Estrutura> query = em.createQuery(jpql.toString(), Estrutura.class);
		query.setParameter("idRecuperando", recuperando.getId());
		return query.getSingleResult();
	}
	
	public StatusRecuperando statusAtualRecuperando(long id){
		try {
			String jpql="select r.status from Recuperando r where r.id = :id";
			 Query query = em.createQuery(jpql);		
			query.setParameter("id", id);
			StatusRecuperando status = (StatusRecuperando)query.getSingleResult();
			return status;
		} catch (NoResultException e) {
			return null;
		}		
	}
	
	public void atualizarRegimeCondicaoAtualRecuperando(Recuperando recuperando, Regime regime, Condicao condicao) {
		String jpql="UPDATE Recuperando r SET r.regimeAtual = :regimeAtual, r.condicaoAtual = :condicaoAtual WHERE r.id = :idRec";
		Query query = em.createQuery(jpql);
		query.setParameter("regimeAtual", regime);
		query.setParameter("condicaoAtual", condicao);
		query.setParameter("idRec", recuperando.getId());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<String> buscarAlcunhas(Long id) {
		String jpql="select a.nome from Alcunha a where a.recuperando.id = :id";
		Query query = em.createQuery(jpql);
		query.setParameter("id", id);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OutroNome> buscarOutrosNomes(Long id) {
		String jpql="select o from OutroNome o where o.recuperando.id = :id";
		Query query = em.createQuery(jpql);
		query.setParameter("id", id);
		
		return query.getResultList();
	}

	public boolean recuperandoPossuiBiometriaAtualizada(long id) {
		try {
			Query query = em.createQuery("select count(b) from Recuperando r, Pessoa p, Biometria b "
					+ "where r.id = :id "
					+ "and r.pessoa.id = p.id "
					+ "and p.biometria.id = b.id "
					+ "and b.template is not null ");
			query.setParameter("id", id);
			long resultado = (Long) query.getSingleResult();
			return (resultado > 0);
		} catch (NoResultException e) {
			return false;
		}
	}
}
