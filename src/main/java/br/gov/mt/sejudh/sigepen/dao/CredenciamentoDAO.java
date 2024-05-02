package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;
import br.gov.mt.sejudh.sigepen.model.CredencialCertidao;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.dto.CredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.VisitanteDTO;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@RequestScoped
public class CredenciamentoDAO extends BaseDAO<Credenciamento, Long> {


	private static final long serialVersionUID = -5039677954279568518L;
	
	@Inject
	private ParametroUnidadeDAO parametroUnidadeDAO;
	
	@Inject VisitanteService visitanteService;
	
	@Override
    public Credenciamento buscarPorId(Long id) {
        return this.buscarPorId(id, false);
    }

    @Override
    public Credenciamento buscarPorId(Long id, Boolean buscarAuditoria) {
    	Credenciamento credenciamento = super.buscarPorId(id, buscarAuditoria);

        if (credenciamento != null) {
            Hibernate.initialize(credenciamento.getFasesDoCredenciamento());
            Optional.ofNullable(credenciamento.getRecuperando()).ifPresent(
                    recuperando -> Hibernate.initialize(recuperando.getFotos())
            );
        }

        return credenciamento;
    }

    public Optional<List<Credenciamento>> listaVisitantes(Recuperando recuperando) {
        try {
            String jpql = "SELECT vr FROM Credenciamento vr WHERE vr.recuperando = :recuperando";
            Query query = this.em.createQuery(jpql, Credenciamento.class);
            query.setParameter("recuperando", recuperando);

            return Optional.of(query.getResultList());
        }catch(NoResultException e){
            return Optional.empty();
        }
    }
    
	public Credenciamento buscarCredenciamentos(Credenciamento credenciamento) {
		Credenciamento vr = this.em.find(Credenciamento.class, credenciamento.getId());

    	Hibernate.initialize(vr.getVisitante());
    	Hibernate.initialize(vr.getVisitante().getTipoVisita());
    	Hibernate.initialize(vr.getVisitante().getTipoVisita().getTipoVisitaDocumento());
    	Hibernate.initialize(vr.getVisitante().getTipoVisita().getTipoVisitaCertidao());
    	Hibernate.initialize(vr.getParente());
    	
    	for(FaseDoCredenciamento vc : vr.getFasesDoCredenciamento()){
    		Hibernate.initialize(vc.getUsuarioAprovacao());

        	for(CredencialDocumento cd : vc.getCredencialDocumentos()){
        		Hibernate.initialize(cd.getVisitanteDocumento());
        	}
        	
        	for(CredencialCertidao cd : vc.getCredencialCertidaos()){
        		Hibernate.initialize(cd.getVisitanteCertidao());
        	}
        	
    	}
    	return vr;
    }

	public void setDocumentosValidados(long id, long idCredenciamento, Visitante visitante) {
		Credenciamento vr = super.buscarPorId(id, false);
		Hibernate.initialize(vr.getFasesDoCredenciamento());
		Hibernate.initialize(vr.getUltimoCredenciamento());
		
		if(vr.getUltimoCredenciamento() != null) {
			Hibernate.initialize(vr.getUltimoCredenciamento().getCredencialDocumentos());
			Hibernate.initialize(vr.getUltimoCredenciamento().getCredencialCertidaos());
		}
		
		if(idCredenciamento == 0) {
	        for(VisitanteDocumento x : visitante.getVisitanteDocumentos()) {
	        	if(vr.getUltimoCredenciamento() != null) {
	            	for(CredencialDocumento y : vr.getUltimoCredenciamento().getCredencialDocumentos()) {
	            		if(y.getVisitanteDocumento().getId().equals(x.getId())) {
	            			x.setValidado(y.getValidado());
	            		}
	            	}
	        	} else {
	        		x.setValidado(SimNao.N);
	        	}
	        }
			
	        for(VisitanteCertidao x : visitante.getCertidoes()) {
	        	if(vr.getUltimoCredenciamento() != null) {
	            	for(CredencialCertidao y : vr.getUltimoCredenciamento().getCredencialCertidaos()) {
	            		if(y.getVisitanteCertidao().getId().equals(x.getId())) {
	            			x.setValidado(y.getValidado());
	            		}
	            	}
	        	} else {
	        		x.setValidado(SimNao.N);
	        	}
	        }
		} else {
	        for(VisitanteDocumento x : visitante.getVisitanteDocumentos()) {
	        	for(FaseDoCredenciamento vc : vr.getFasesDoCredenciamento()) {
	        		if(vc.getId() == idCredenciamento) {
		            	for(CredencialDocumento y : vc.getCredencialDocumentos()) {
		            		if(y.getVisitanteDocumento().getId().equals(x.getId())) {
		            			x.setValidado(y.getValidado());
		            		}
		            	}
	        			
	        		}
	        	}
	        }
			
	        for(VisitanteCertidao x : visitante.getCertidoes()) {
	        	for(FaseDoCredenciamento vc : vr.getFasesDoCredenciamento()) {
	        		if(vc.getId() == idCredenciamento) {
		            	for(CredencialCertidao y : vc.getCredencialCertidaos()) {
		            		if(y.getVisitanteCertidao().getId().equals(x.getId())) {
		            			x.setValidado(y.getValidado());
		            		}
		            	}
	        			
	        		}
	        	}
	        }
		}
	}

	public void atualizarStatusCredenciamento(FaseDoCredenciamento fase) {
		this.atualizarStatusCredenciamento(fase.getCredenciamento());
	}

	private Integer buscarFase(FaseCredenciamento fase) {
		
        List<ParametroUnidade> parametros = parametroUnidadeDAO.buscaPorCampo("unidadePrisional", SecurityUtil.getUnidadeSelecionada());

        switch (fase) {
            case RECEBIMENTO:
                return this.atribuirFaseRecebimento(fase, parametros).getId();
            case CHECAGEM:
                return this.atribuirFaseChecagem(fase, parametros).getId();
            case CONFERENCIA:
            	return this.atribuirFaseConferencia(fase, parametros).getId();
            case ANALISE:
            	return this.atribuirFaseAnalise(fase).getId();
            case APROVACAO:
            	return FaseCredenciamento.APROVACAO.getId();
            default:
                return FaseCredenciamento.RECEBIMENTO.getId();
        }
	}
	
    private FaseCredenciamento atribuirFaseRecebimento(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.CHECAGEM_DOCUMENTO && param.getValidar() == SimNao.S)
        ) {
            return this.atribuirFaseChecagem(fase, parametros);
        }

        return FaseCredenciamento.CHECAGEM;
    }
    
    private FaseCredenciamento atribuirFaseChecagem(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.CONFERENCIA_AUTENTICACAO && param.getValidar() == SimNao.S)
        ) {
            
            return this.atribuirFaseConferencia(fase, parametros);
        }

        return FaseCredenciamento.CONFERENCIA;
    }
    
    private FaseCredenciamento atribuirFaseConferencia(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
        if (parametros.stream().noneMatch(
                param -> param.getNome() == ParametroVisitante.ANALISE_CERTIDAO && param.getValidar() == SimNao.S)
        ) {
            return this.atribuirFaseAnalise(fase);
        }

        return FaseCredenciamento.ANALISE;
    }
    
    private FaseCredenciamento atribuirFaseAnalise(FaseCredenciamento fase) {
        return FaseCredenciamento.APROVACAO;
    }

    private FaseCredenciamento atribuirFaseAprovacao(FaseCredenciamento fase) {
        return FaseCredenciamento.RECEBIMENTO;
    }

	public void atualizarStatusExcluir(FaseDoCredenciamento fase) {
		Credenciamento vr = this.buscarPorId(fase.getCredenciamento().getId());
		vr.setStatusCredenciamento(StatusCredenciamento.PENDENTE.getId());
        if(fase.getFaseCredenciamento() == FaseCredenciamento.RECEBIMENTO)
        	vr.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO.getId());
        else
        	vr.setFaseCredenciamento(this.buscarFaseExcluir(fase.getFaseCredenciamento()));
        this.salvar(vr);
		
	}

	private Integer buscarFaseExcluir(FaseCredenciamento fase) {
		
        List<ParametroUnidade> parametros = parametroUnidadeDAO.buscaPorCampo("unidadePrisional", SecurityUtil.getUnidadeSelecionada());

        switch (fase) {
	        case APROVACAO:
	        	return this.atribuirFaseAprovacaoExcluir(fase, parametros).getId();
            case ANALISE:
            	return this.atribuirFaseAnaliseExcluir(fase, parametros).getId();
            case CONFERENCIA:
            	return this.atribuirFaseConferenciaExcluir(fase, parametros).getId();
            case CHECAGEM:
                return this.atribuirFaseChecagemExcluir(fase, parametros).getId();
            default:
                return FaseCredenciamento.RECEBIMENTO.getId();
        }
	}
	
    private FaseCredenciamento atribuirFaseAprovacaoExcluir(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
    	int i = parametros.size();
    	while (true)  {
    		i--;
    		if(i<0) break;
    		ParametroUnidade param = parametros.get(i);
    		if(param.getNome() == ParametroVisitante.ANALISE_CERTIDAO && param.getValidar() == SimNao.S) {
    			return FaseCredenciamento.ANALISE;
    		}
    	}
    	
    	return this.atribuirFaseAnaliseExcluir(fase, parametros);
    }
    
    private FaseCredenciamento atribuirFaseAnaliseExcluir(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
    	int i = parametros.size();
    	while (true)  {
    		i--;
    		if(i<0) break;
    		ParametroUnidade param = parametros.get(i);
    		if(param.getNome() == ParametroVisitante.CONFERENCIA_AUTENTICACAO && param.getValidar() == SimNao.S) {
    			return FaseCredenciamento.CONFERENCIA;
    		}
    	}
    	
    	return this.atribuirFaseConferenciaExcluir(fase, parametros);
    }
    
    private FaseCredenciamento atribuirFaseConferenciaExcluir(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
    	int i = parametros.size();
    	while (true)  {
    		i--;
    		if(i<0) break;
    		ParametroUnidade param = parametros.get(i);
    		if(param.getNome() == ParametroVisitante.CHECAGEM_DOCUMENTO && param.getValidar() == SimNao.S) {
    			return FaseCredenciamento.CHECAGEM;
    		}
    	}
    	
    	return this.atribuirFaseChecagemExcluir(fase, parametros);
    }
    
    private FaseCredenciamento atribuirFaseChecagemExcluir(FaseCredenciamento fase, List<ParametroUnidade> parametros) {
    	int i = parametros.size();
    	while (true)  {
    		i--;
    		if(i<0) break;
    		ParametroUnidade param = parametros.get(i);
    		if(param.getNome() == ParametroVisitante.RECEBIMENTO_DOCUMENTACAO && param.getValidar() == SimNao.S) {
    			return FaseCredenciamento.RECEBIMENTO;
    		}
    	}
    	
    	return FaseCredenciamento.RECEBIMENTO;
    }

	public List<Credenciamento> inativarVisitantesRecuperando(Recuperando recuperando) {
        try {
        	String jpql = "SELECT vr FROM Credenciamento vr WHERE vr.recuperando = :recuperando";
            Query query = this.em.createQuery(jpql, Credenciamento.class);
            query.setParameter("recuperando", recuperando);
            List<Credenciamento> lista= query.getResultList();
            for(Credenciamento vr : lista){
            	
            	vr.setStatusCredenciamento(StatusCredenciamento.INATIVO.getId());
            	this.em.merge(vr);
            	for(FaseDoCredenciamento vc : vr.getFasesDoCredenciamento()){
            		vc.setStatusCredenciamento(StatusCredenciamento.INATIVO);
            		this.em.merge(vc);
            	}
            	this.visitanteService.atribuirStatusVisitantePosCredenciamentoInativar(vr.getVisitante());
            	
            }
            return lista;
        }catch(NoResultException e){
            return new ArrayList<Credenciamento>();
        }
	}
	
	public FaseDoCredenciamento buscarUltimoCredenciamento(Credenciamento vr) {
		
		String jpql = "SELECT vr FROM Credenciamento vr WHERE vr = :vr ";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("vr", vr);
        Credenciamento v = (Credenciamento)query.getSingleResult();
        
		jpql = "SELECT vc FROM FaseDoCredenciamento vc WHERE vc.credenciamento = :vr ORDER BY faseCredenciamento DESC ";
        query = this.em.createQuery(jpql, FaseDoCredenciamento.class);
        query.setParameter("vr", vr);
        
        List<FaseDoCredenciamento> vc = query.getResultList();
        
        if(vc.size() > 0)
        	return vc.get(0);
        
        return new FaseDoCredenciamento();
        
	}
	
	@SuppressWarnings("unchecked")
	public List<FaseDoCredenciamento> listaFasesDoCredenciamento(Credenciamento vr) {
		Session session = this.em.unwrap(Session.class);
        String sql  = "SELECT * FROM SIGEPEN_OWNER.SGP_CREDENCIAMENTO_FASE CF WHERE CF.ID_CREDENCIAMENTO = :credenciamentoId";
   
        return session.createSQLQuery(sql).addEntity(FaseDoCredenciamento.class).setParameter("credenciamentoId", vr.getId()).list();
	}

	public void atualizarStatusCredenciamento(Credenciamento credenciamento) {
		Credenciamento vr = this.em.find(Credenciamento.class, credenciamento.getId());
		FaseDoCredenciamento vc = this.buscarUltimoCredenciamento(credenciamento);
		if(vc.getId() == null) {
			vr.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO.getId());
			vr.setStatusCredenciamento(StatusCredenciamento.PENDENTE.getId());
		} else {
			vr.setFaseCredenciamento(parametroUnidadeDAO.buscarFase(vc).getId());
			if(FaseCredenciamento.APROVACAO == vc.getFaseCredenciamento()) {
				vr.setStatusCredenciamento(vc.getStatusCredenciamento().getId());
			} else {
				vr.setStatusCredenciamento(StatusCredenciamento.PENDENTE.getId());
			}
		}
		this.em.merge(vr);
	}
	

	public void atualizarStatusCredenciamento(Visitante visitante) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
        	for(Credenciamento vr : lista) {
        		this.atualizarStatusCredenciamento(vr);
        	}
        }
	}

	public void atualizarStatusAprovacao(Credenciamento visitante, StatusCredenciamento status) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE vr = :visitante";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
        	Credenciamento m = lista.get(0);
        	m.setFaseCredenciamento(FaseCredenciamento.APROVACAO.getId());
        	m.setStatusCredenciamento(status.getId());
        	this.em.merge(m);
        }
	}

	public Credenciamento buscarCredenciamento(Visitante visitante, Recuperando recuperando) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante AND recuperando = :recuperando ORDER BY id DESC";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        query.setParameter("recuperando", recuperando);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
        	return lista.get(0);
        }
        return new Credenciamento();
	}
	
	public Credenciamento buscarUltimoCredenciamento(Visitante visitante) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante ORDER BY id DESC";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
        	return lista.get(0);
        }
        return new Credenciamento();
	}
	
	public List<Credenciamento> buscarPorVisitante(Visitante visitante) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante ORDER BY id DESC";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        List<Credenciamento> lista = query.getResultList();
        return lista;
	}
	
	public List<Credenciamento> buscarCredenciamentoEntidadesRelacionadasPorVisitante(Visitante visitante) {

		Session session = this.em.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Credenciamento c ");
		sql.append("LEFT JOIN FETCH c.unidadePrisional ");
		sql.append("LEFT JOIN FETCH c.tipoVisita ");
		sql.append("LEFT JOIN FETCH c.recuperando ");
		sql.append("LEFT JOIN FETCH c.recuperando.pessoa "); 
		sql.append("WHERE c.visitante.id = :visitanteId");
		
		return session.createQuery(sql.toString())
                .setParameter("visitanteId", visitante.getId())
                .list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Credenciamento> buscarPorVisitanteId(Long visitanteId) {
		Session session = this.em.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Credenciamento c ");
		sql.append("LEFT JOIN FETCH c.unidadePrisional ");
		sql.append("LEFT JOIN FETCH c.visitante ");
		sql.append("LEFT JOIN FETCH c.tipoVisita ");
		sql.append("LEFT JOIN FETCH c.recuperando ");
		sql.append("LEFT JOIN FETCH c.parente ");
		sql.append("LEFT JOIN FETCH c.recuperando.pessoa "); 
		sql.append("WHERE c.visitante.id = :visitanteId");
		
		return session.createQuery(sql.toString())
                .setParameter("visitanteId", visitanteId)
                .list();
               
	}
	
	@SuppressWarnings("unchecked")
	public List<Credenciamento> buscarCredenciamentoComFasesPorVisitanteId(Long visitanteId) {
	    Session session = this.em.unwrap(Session.class);
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT DISTINCT c FROM Credenciamento c ");
	    sql.append(" LEFT JOIN FETCH c.unidadePrisional ");
	    sql.append(" LEFT JOIN FETCH c.visitante ");
	    sql.append(" LEFT JOIN FETCH c.tipoVisita ");
	    sql.append(" LEFT JOIN FETCH c.recuperando ");
	    sql.append(" LEFT JOIN FETCH c.recuperando.pessoa ");
	    sql.append(" LEFT JOIN FETCH c.fasesDoCredenciamento f ");
	    sql.append(" LEFT JOIN FETCH f.usuarioAprovacao ");  
	    sql.append(" WHERE c.visitante.id = :visitanteId ");
	 
	    List<Credenciamento> credenciamentos = session.createQuery(sql.toString())
	        .setParameter("visitanteId", visitanteId)
	        .list();
	 
	    return credenciamentos;
	}
	
	
	public void buscarCredenciamentosEFasesDTO(VisitanteDTO visitanteDto) {
		Criteria crit = em.unwrap(Session.class).createCriteria(Credenciamento.class);
		crit.createAlias("visitante", "visitante", JoinType.INNER_JOIN);
		
		ProjectionList proj = Projections.projectionList();

        proj.add(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("statusCredenciamento"), "status")
                .add(Projections.property("faseCredenciamento"), "faseAtual")
        );
				
		crit.setProjection(proj);
		crit.setResultTransformer(new AliasToBeanResultTransformer(CredenciamentoDTO.class));
		crit.addQueryHint(QueryHints.READ_ONLY);		
		crit.add(Restrictions.eq("visitante.id", visitanteDto.getId()));

		@SuppressWarnings("unchecked")
		List<CredenciamentoDTO> credenciamentos = crit.list();
		
		credenciamentos.forEach(c -> {
			Criteria critC = em.unwrap(Session.class).createCriteria(FaseDoCredenciamento.class);
			critC.createAlias("credenciamento", "credenciamento", JoinType.INNER_JOIN);
			
			ProjectionList projC = Projections.projectionList();
	
			projC.add(Projections.projectionList()
	                .add(Projections.property("id"), "id")
	                .add(Projections.property("statusCredenciamento"), "statusCredenciamento")
	                .add(Projections.property("faseCredenciamento"), "faseCredenciamento")
	        );
					
	        critC.setProjection(projC);
	        critC.setResultTransformer(new AliasToBeanResultTransformer(FaseDoCredenciamentoDTO.class));
	        critC.addQueryHint(QueryHints.READ_ONLY);			
	        critC.add(Restrictions.eq("credenciamento.id", c.getId()));
	
			@SuppressWarnings("unchecked")
			List<FaseDoCredenciamentoDTO> fases = critC.list();
			c.setFases(fases);
		});
		
		visitanteDto.setCredenciamentos(credenciamentos);
	}
	
	
	public Credenciamento listarCredenciamentoVisitante(long id) {
		Session session = this.em.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT c FROM Credenciamento c ");
		sql.append("LEFT JOIN FETCH c.unidadePrisional ");
		sql.append("LEFT JOIN FETCH c.visitante ");
		sql.append("LEFT JOIN FETCH c.tipoVisita ");
		sql.append("LEFT JOIN FETCH c.recuperando ");
		sql.append("LEFT JOIN FETCH c.recuperando.pessoa "); 
		sql.append("WHERE c.id = :id");

		
		Credenciamento credenciamento = (Credenciamento) session.createQuery(sql.toString())
                .setParameter("id", id)
                .uniqueResult();
		

        if (credenciamento != null) {
            Hibernate.initialize(credenciamento.getFasesDoCredenciamento());
            Optional.ofNullable(credenciamento.getRecuperando()).ifPresent(
                    recuperando -> Hibernate.initialize(recuperando.getFotos())
            );
        }
		
		return credenciamento;

	}

	public Credenciamento buscarCredenciamentoVisitante(Long id) {
		Session session = this.em.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c FROM Credenciamento c ");
	    sql.append("LEFT JOIN FETCH c.fasesDoCredenciamento f ");
	    sql.append("LEFT JOIN FETCH c.visitante v ");
	    sql.append("LEFT JOIN FETCH v.pessoa ");
	    sql.append("WHERE f.id = :id");

		return (Credenciamento) session.createQuery(sql.toString())
                .setParameter("id", id)
                .uniqueResult();
	}
}
