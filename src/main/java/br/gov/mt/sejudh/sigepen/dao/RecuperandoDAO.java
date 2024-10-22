package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnificarRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.EstruturaComplemento;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Remanejamento;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;

@RequestScoped
public class RecuperandoDAO {
	
	@Inject
	private EntityManager em;
	
	@Inject
	private SolicitacaoVisitaDAO solicitacaoVisitaDao;

	@Inject
	private EstruturaDAO estruturaDAO;

	public int buscarQuantidadeFiltrada(FiltroRecuperandoView filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(RecuperandoView.class, em);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		criteria.setProjection(Projections.rowCount());

		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public int buscarQuantidadeFiltrada(FiltroRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		criteria.setProjection(Projections.countDistinct("id"));

		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroRecuperando filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Pessoa.class, em, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(RecuperandoDTO.class));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<RecuperandoView> buscarRecuperandosPorFiltro(FiltroRecuperandoView filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(RecuperandoView.class, em, isBuscaRelatorio);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		List<RecuperandoView> list = criteria.list();
		return list;
	}

	public int buscarQuantidadeFiltrada(FiltroUnificarRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
		criteria.setProjection(Projections.countDistinct("id"));
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<RecuperandoDTO> buscarRecuperandosPorFiltro(FiltroUnificarRecuperando filtro, boolean isBuscaRelatorio) {
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
	
	public void excluirUnificacao(Recuperando recuperando) {
		em.remove(em.getReference(Recuperando.class, recuperando.getId()));
	}
	
	public Recuperando salvarUnificacao(Recuperando recuperando) {
		em.persist(recuperando);

		return recuperando;
	}
	
	public void excluir(Recuperando recuperando) {
		em.remove(em.merge(recuperando));
	}

	public void excluir(long idRecuperando) {
		Recuperando r = buscarRecuperandoPorId(idRecuperando);
		em.remove(em.merge(r));
	}

	public Recuperando buscarRecuperandoDocumentosPorId(long idRecuperando) {
		try {
            String jpql="select r from Recuperando r where r.id = :id";
            TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
            query.setParameter("id", idRecuperando);
            Recuperando r = query.getSingleResult();
            
            Hibernate.initialize(r.getPessoa().getDocumentos());
            
            return r;
		} catch (NoResultException e) {
			return null;
		}
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

    public Date buscarUltimaPassagemRecuperandoPessoa(long idPessoa) {
        try {
            String jpql="select max(p.dataSaida)" +
                    "from Recuperando r " +
                    "left join r.passagens p " +
                    "where r.pessoa.id = :id and r.status = :status ";
            TypedQuery<Date> query = em.createQuery(jpql, Date.class);
            query.setParameter("id", idPessoa);
            query.setParameter("status", StatusRecuperando.INATIVO);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
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
			Hibernate.initialize(r.getHistoricos());
			if(r.getParentes() != null) {
				for(Parente p : r.getParentes()) {
					Hibernate.initialize(p.getCredenciamentos());
					if(p.getCredenciamentos()!=null) {
						for(Credenciamento vr : p.getCredenciamentos()) {
							Hibernate.initialize(vr.getVisitante());
							if(vr.getVisitante()!=null) {
								Hibernate.initialize(vr.getVisitante().getPessoa());
							}
						}
					}
				}
			}
			Hibernate.initialize(r.getOrientacaoSexual());
			Hibernate.initialize(r.getIdentidadeDeGenero());
			Hibernate.initialize(r.getEscolaridade());
			Hibernate.initialize(r.getRaca());
			Hibernate.initialize(r.getEtnia());
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
			Hibernate.initialize(r.getProcessos());
			Hibernate.initialize(r.getPassagens());

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
		Foto foto = em.merge(f);
		em.remove(foto);
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
        FiltroSolicitacaoVisita filtro = new FiltroSolicitacaoVisita();
        filtro.setParente(parente);
        List<SolicitacaoVisita> lista = solicitacaoVisitaDao.buscarPorParente2(parente);
        if(lista!=null) {
            for(SolicitacaoVisita s : lista) {
            	s.setParente(null);
            	em.merge(s);
            }

    		em.remove(em.merge(parente));
        	
        }
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
	
	public void atualizarDataCadastroParente(Date data, long idRecuperando){
		String jpql="update Recuperando r set  r.dataCadastroParente = :data where r.id = :idRec";
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
	
	public PedidoTransito findByFotoPedidoTransito(long idFoto) {
		try {
			Query query = em.createQuery("select p from PedidoTransito p where p.foto.id = :idFoto");
			query.setParameter("idFoto", idFoto);
			PedidoTransito pedidoTransito = (PedidoTransito)query.getSingleResult();
			return pedidoTransito;
		} catch (NoResultException e) {
			return null;
		}		
	}
	
	public int updateFotoPedidoTransito(long id, long idFoto) {
		try {
			Query query = em.createQuery("update PedidoTransito p set p.foto.id = :idFoto where p.id = :id");
			query.setParameter("idFoto", idFoto);
	        query.setParameter("id", id);
			int pedidoTransito = query.executeUpdate();
			return pedidoTransito;
		} catch (NoResultException e) {
			return 0;
		}		
	}
	
	public Remanejamento findByFotoRemanejamento(long idFoto) {
		try {
			Query query = em.createQuery("select r from Remanejamento r where r.foto.id = :idFoto");
			query.setParameter("idFoto", idFoto);
			Remanejamento pedidoTransito = (Remanejamento)query.getSingleResult();
		return pedidoTransito;
		} catch (NoResultException e) {
			return null;
		}		
	}
	
	public int updateFotoRemanejamento(long id, long idFoto) {
		try {
			Query query = em.createQuery("update Remanejamento r set r.foto.id = :idFoto where r.id = :id");
			query.setParameter("idFoto", idFoto);
	        query.setParameter("id", id);
			int pedidoTransito = query.executeUpdate();
			return pedidoTransito;
		} catch (NoResultException e) {
			return 0;
		}		
	}
	
	public Passagem findByFotoPassagem(long idFoto) {
		try {
			Query query = em.createQuery("select p from Passagem p where p.foto.id = :idFoto");
			query.setParameter("idFoto", idFoto);
			Passagem passagem = (Passagem)query.getSingleResult();
			return passagem;
		} catch (NoResultException e) {
			return null;
		}	
	}
	
	public int updateFotoPassagem(long id, long idFoto) {
		try {
			Query query = em.createQuery("update Passagem p set p.foto.id = :idFoto where p.id = :id");
			query.setParameter("idFoto", idFoto);
	        query.setParameter("id", id);
			int pedidoTransito = query.executeUpdate();
			return pedidoTransito;
		} catch (NoResultException e) {
			return 0;
		}		
	}
	
	public void substituirFoto(Foto foto, Foto fotoAntiga) {
		PedidoTransito pedidoTransito = findByFotoPedidoTransito(fotoAntiga.getId());
		if(pedidoTransito != null){
			updateFotoPedidoTransito(pedidoTransito.getId(),foto.getId());
		}
		Remanejamento remanejamento = findByFotoRemanejamento(fotoAntiga.getId());
		if(remanejamento != null){
			updateFotoRemanejamento(remanejamento.getId(),foto.getId());
		}
		Passagem passagem = findByFotoPassagem(fotoAntiga.getId());
		if(passagem != null){
			updateFotoPassagem(passagem.getId(),foto.getId());
		}
	}

	
	public boolean fotoVinculada(Foto foto) {
		Query query = em.createQuery("select count(p) from PedidoTransito p where p.foto.id = :idFoto");
		query.setParameter("idFoto", foto.getId());
		long contador = (Long)query.getSingleResult();
		Query query2 = em.createQuery("select count(r) from Remanejamento r where r.foto.id = :idFoto");
		query2.setParameter("idFoto", foto.getId());
		Long contador2 = (Long)query2.getSingleResult();
		Query query3 = em.createQuery("select count(p) from Passagem p where p.foto.id = :idFoto");
		query3.setParameter("idFoto", foto.getId());
		Long contador3 = (Long)query3.getSingleResult();
		long count = contador + contador2 + contador3;
		return count>0;
	}
	
	public Estrutura lotacaoAtual(Recuperando recuperando) {
		try {
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
		    jpql.append("order by l.id desc ");
			
			TypedQuery<Estrutura> query = em.createQuery(jpql.toString(), Estrutura.class);
			query.setParameter("idRecuperando", recuperando.getId());



			List<Estrutura> list = query.getResultList();

			if (list.size() > 0) {
				list.forEach(estrutura -> estruturaDAO.buscarEstruturasCompleta(estrutura));
				return query.getResultList().get(0);
			}
			
			return null; 
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Estrutura PedidoTransitolotacaoAtual(Recuperando recuperando) {
		try {
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
		    jpql.append("order by l.id desc ");
			
			TypedQuery<Estrutura> query = em.createQuery(jpql.toString(), Estrutura.class);
			query.setParameter("idRecuperando", recuperando.getId());



			return query.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
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
	
	public StatusRecuperando buscarStatusRecuperandoPorIdVisitante(long idVisitante){
		try {
			String jpql="select r.status from Recuperando r, Visitante v where r.pessoa.id = v.pessoa.id and v.id = :id";
			Query query = em.createQuery(jpql);
			query.setParameter("id", idVisitante);
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

	public Recuperando buscarRecuperandoComPassagens(Long recuperandoId) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select r ");
		jpql.append("from Recuperando r ");
		jpql.append("join fetch r.pessoa pe ");
		jpql.append("join fetch r.passagens p ");
		jpql.append("join fetch p.lotacao l ");
		jpql.append("join fetch l.estrutura e ");
		jpql.append("join fetch e.unidadePrisional u ");
		jpql.append("left join fetch pe.pais pa ");
		jpql.append("left join fetch u.responsavel s ");
		jpql.append("left join fetch pe.naturalidade c ");
		jpql.append("left join fetch pe.nacionalidade n ");
		jpql.append("where r.id = :idRecuperando ");

		TypedQuery<Recuperando> query = em.createQuery(jpql.toString(), Recuperando.class);
		query.setParameter("idRecuperando", recuperandoId);

		return query.getSingleResult();
	}

	public Boolean verificaRecuperandoPossuiPassagemNaUnidadeLogada(Long recuperandoId, long unidadeId) {
		try {
			Query query = em.createQuery("select count(l) from Recuperando r, Passagem p, PassagemLotacao l, Estrutura e, UnidadePrisional u "
					+ "where r.id = p.recuperando.id "
					+ "and p.id = l.passagem.id "
					+ "and e.id = l.estrutura.id "
					+ "and e.unidadePrisional.id = u.id "
					+ "and r.id = :recuperandoId "
					+ "and u.id = :unidadeId "
					+ "and l.tipoTransito != :tipoTransito");
			
			query.setParameter("recuperandoId", recuperandoId);
			query.setParameter("unidadeId", unidadeId);
			query.setParameter("tipoTransito", TipoTransito.REMANEJAMENTO);
			
			long resultado = (Long) query.getSingleResult();
			return (resultado > 0);
		} catch (NoResultException e) {
			return false;
		}
	}

	public Long recuperandoJaExisteNoSistema(String nome, String nomeMae, Date dataNascimento, Long idRecuperando) {
		try {
			boolean possuiIdRecuperando = idRecuperando > 0;
			StringBuilder sqlQuery = new StringBuilder();
			
			sqlQuery.append("select r.id from Recuperando r, Pessoa p "
					+ "where r.id = p.recuperando.id "
					+ "and p.nome = :nome "
					+ "and p.nomeMae = :nomeMae "
					+ "and p.dataNascimento = :dataNascimento ");
			
			if (possuiIdRecuperando) {
				sqlQuery.append("and r.id != :id ");
			}
			
			Query query = em.createQuery(sqlQuery.toString());
			query.setParameter("nome", nome.toUpperCase());
			query.setParameter("nomeMae",nomeMae.toUpperCase());
			query.setParameter("dataNascimento", dataNascimento);
			
			if (possuiIdRecuperando) {
				query.setParameter("id", idRecuperando);
			}
			
			long resultado = (Long) query.getSingleResult();
			return resultado;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Optional<Recuperando> buscarRecuperandoPorIdPessoa(Long idPessoa)  {
		try {
			String jpql="select r from Recuperando r where r.pessoa.id = :id";
			TypedQuery<Recuperando> query = em.createQuery(jpql, Recuperando.class);
			query.setParameter("id", idPessoa);

			Recuperando result = query.getSingleResult();
			Hibernate.initialize(result.getFotoFrenteAtual());

			return Optional.of(result);
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	public Recuperando buscarPessoaRecuperando(Pessoa pessoa) {
    	StringBuilder jpql = new StringBuilder();
		
		jpql.append("select r ");
		jpql.append("from Recuperando r ");
		jpql.append("join fetch r.fotos f ");
		jpql.append("Where r.pessoa = :pessoa");

		TypedQuery<Recuperando> query = em.createQuery(jpql.toString(), Recuperando.class);
		query.setParameter("pessoa", pessoa);
		try {
			Recuperando r = query.getSingleResult();
			return r;
		} catch (Exception e) {
			Recuperando r = new Recuperando();
			Pessoa p = this.em.find(Pessoa.class, pessoa.getId());
			if(p.getBiometria() != null)
				Hibernate.initialize(p.getBiometria().getDedos());
			r.setPessoa(p);
			return r;
		}
		    	
    }

	public void resetarDataCadastroParente(long idRecuperando) {
		String jpql="update Recuperando r set  r.dataCadastroParente = null, r.alteracaoParentes = null where r.id = :idRec";
		Query query = em.createQuery(jpql);
		query.setParameter("idRec", idRecuperando);
		query.executeUpdate();
	}

	public List<Etnia> listarEtnias() {
		try {
			String jpql="select e from Etnia e";
			TypedQuery<Etnia> query = em.createQuery(jpql, Etnia.class);

			return query.getResultList();

		} catch (NoResultException e) {
			return new ArrayList<Etnia>();
		}
	}

	public void expirarGestante(Date dataExpirar) {
		String jpql="update Recuperando r set r.gestante = 0 where r.gestante = 1 and r.inicioGestacao is not null and r.inicioGestacao < :dataExpirar";
		Query query = em.createQuery(jpql);
		query.setParameter("dataExpirar", dataExpirar);
		query.executeUpdate();
	}

	public void expirarLactante(Date dataExpirar) {
		String jpql="update Recuperando r set r.lactante = 0 where r.lactante = 1 and r.inicioLactante is not null and r.inicioLactante < :dataExpirar";
		Query query = em.createQuery(jpql);
		query.setParameter("dataExpirar", dataExpirar);
		query.executeUpdate();
	}

	public RecuperandoView buscarRecuperandoSelecionado(Long idPessoa) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select r from RecuperandoView r left join r.estrutura e where r.pessoaId = :id");

		TypedQuery<RecuperandoView> query = em.createQuery(jpql.toString(), RecuperandoView.class);
		query.setParameter("id", idPessoa);
		
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Recuperando alterar(Recuperando recuperando) {
		boolean novaFoto = false;
		for(Foto f : recuperando.getFotos()) {
			if(f.getId() == 0) {
				novaFoto = true;
			}
			f.setIsAtual(false);
		}
		if(novaFoto) {
			for(Foto f : recuperando.getFotos()) {
				f.setIsAtual(false);
				if(f.getId() == 0) {
					f.setIsAtual(true);
				}
			}
		}
		return this.em.merge(recuperando);
	}

	public String buscarLotacaoAtual(long idRecuperando, boolean isTrazerUnidade) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select e from EstruturaComplemento e, RecuperandoView r where r.id = :id and e.id = r.estruturaId");

		TypedQuery<EstruturaComplemento> query = em.createQuery(jpql.toString(), EstruturaComplemento.class);
		query.setParameter("id", idRecuperando);
		
		EstruturaComplemento est = null;
		
		try {
		 est = query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
		if (isTrazerUnidade) {
			return est.getNomeCompletoUnidade();
		} else {
			return est.getNomeCompleto();
		}
	}

	public byte[] buscarFotoFrente(Long recuperandoId) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select f ");
		jpql.append("from Foto f ");
		jpql.append("where f.tipo = 1");
		jpql.append("and f.isAtual = 1");
		jpql.append("and f.recuperando.id = :idRecuperando ");

		TypedQuery<Foto> query = em.createQuery(jpql.toString(), Foto.class);
		query.setParameter("idRecuperando", recuperandoId);

		return query.getSingleResult().getImagem().getArquivo();
	}

	public Imagem buscarImagemFrente(Long id) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select i ");
		jpql.append("from Foto f, Imagem i ");
		jpql.append("where f.tipo = 1 ");
		jpql.append("and f.isAtual = 1 ");
		jpql.append("and f.imagem.id = i.id ");
		jpql.append("and f.recuperando.id = :idRecuperando ");

		TypedQuery<Imagem> query = em.createQuery(jpql.toString(), Imagem.class);
		query.setParameter("idRecuperando", id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
