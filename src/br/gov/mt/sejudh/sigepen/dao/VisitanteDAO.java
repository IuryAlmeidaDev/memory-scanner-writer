package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitanteRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class VisitanteDAO extends BaseDAO<Visitante, Long> {


	private static final long serialVersionUID = 1635530084073694819L;

	@Inject
	private ParametroUnidadeDAO parametroUnidadeDAO;

	@Override
    public Visitante salvar(Visitante t) {
        Visitante visitante = super.salvar(t);
        initialize(visitante.getPessoa().getDocumentos());
        initialize(visitante.getPessoa().getContatos());

        return visitante;
    }

    @Override
    public Visitante buscarPorId(Long id) {

        return this.buscarPorId(id, false);
    }

    @Override
    public Visitante buscarPorId(Long id, Boolean buscarAuditoria) {
        Visitante visitante = super.buscarPorId(id, buscarAuditoria);
        if (visitante == null) return null;

        this.inicializarColecoes(visitante);

        return visitante;
    }

    private void inicializarColecoes(Visitante visitante) {
    	Hibernate.initialize(visitante.getTipoVisita());
    	Hibernate.initialize(visitante.getPessoa());
        Hibernate.initialize(visitante.getFotos());
        Hibernate.initialize(visitante.getImpedimentos());
        Hibernate.initialize(visitante.getCertidoes());
        Hibernate.initialize(visitante.getCredenciamentos());
        Hibernate.initialize(visitante.getIdentidadeDeGenero());
        visitante.getCredenciamentos().forEach(credenciamento -> {
            Optional.ofNullable(credenciamento.getRecuperando()).ifPresent(
                    recuperando -> Hibernate.initialize(recuperando.getFotos())
            );
            Hibernate.initialize(credenciamento.getFasesDoCredenciamento());
            Hibernate.initialize(credenciamento.getTipoVisita());
        });
        Hibernate.initialize(visitante.getVisitanteDocumentos());
        Hibernate.initialize(visitante.getPessoa().getNacionalidade());
        Hibernate.initialize(visitante.getPessoa().getNaturalidade());
        Hibernate.initialize(visitante.getPessoa().getPais());
        Hibernate.initialize(visitante.getPessoa().getDocumentos());
        if (visitante.getResponsavel() != null) {
            Hibernate.initialize(visitante.getResponsavel().getFotos());
            if(visitante.getResponsavel().getPessoa() != null) {
            	if(visitante.getResponsavel().getPessoa().getNacionalidade() != null) {
            		Hibernate.initialize(visitante.getResponsavel().getPessoa().getNacionalidade().getDescricao());
            	}
                if(visitante.getResponsavel().getPessoa().getPais() != null) {
                	Hibernate.initialize(visitante.getResponsavel().getPessoa().getPais().getNome());
                }
                Hibernate.initialize(visitante.getResponsavel().getPessoa().getNaturalidade());
            }
            Hibernate.initialize(visitante.getResponsavel().getIdentidadeDeGenero());
        }
        Hibernate.initialize(visitante.getPessoa().getContatos());
        Hibernate.initialize(visitante.getPessoa().getEnderecos());
        Hibernate.initialize(visitante.getPessoa().getBiometria());

        if (visitante.getPessoa().getBiometria() != null) {
            Hibernate.initialize(visitante.getPessoa().getBiometria().getDedos());
        }
        if(visitante.getPessoa() != null)
        	Hibernate.initialize(visitante.getPessoa().getVisitante());
    }

    public List<Visitante> listarRelatorio(FiltroVwVisitanteRelatorio filtro) {
        List<Visitante> list = super.buscaPorFiltro(filtro, true);

        for (Visitante visitante : list) {
        	Hibernate.initialize(visitante.getPessoa());
            Hibernate.initialize(visitante.getCredenciamentos());
            Hibernate.initialize(visitante.getPessoa().getDocumentos());
            for (Credenciamento recuperando : visitante.getCredenciamentos()) {
                Hibernate.initialize(recuperando.getFasesDoCredenciamento());
            }
        }
        return list;
    }

    public Optional<Visitante> buscarPorIdPessoa(Long id) {
        try{
            String jpql = "select v  from Visitante v WHERE v.pessoa.id = :id";
            TypedQuery<Visitante> query = em.createQuery(jpql, Visitante.class).setParameter("id", id);

            return Optional.of(query.getSingleResult());
        }catch(NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<Visitante> buscarDependenteVisitanteResponsavel(Visitante visitante) {
        String jpql = "select v from Visitante v Where v.responsavel.id =:visitante";
        Query query = em.createQuery(jpql, Visitante.class).setParameter("visitante", visitante.getId());
        return query.getResultList();
    }
    
    @Override
    public List<Visitante> buscaPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
    	 List<Visitante> visitantes = new ArrayList<Visitante>();
    	 List<Visitante> buscaPorFiltro = super.buscaPorFiltro(filtro, isRelatorio);
    	    for (Visitante visitante : buscaPorFiltro) {
    	    	Hibernate.initialize(visitante.getPessoa().getDocumentos());
    	    	 Hibernate.initialize(visitante.getCredenciamentos());

    	            for (Credenciamento recuperando : visitante.getCredenciamentos()) {
    	                Hibernate.initialize(recuperando.getFasesDoCredenciamento());
    	            }
    	    	visitantes.add(visitante);
    		}
    	    
    	 return visitantes;
    }
    
    public Visitante buscarVisitante(VwVisitante visitante) {
    	StringBuilder jpql = new StringBuilder();
		
		jpql.append("select v ");
		jpql.append("from Visitante v ");
		jpql.append("join fetch v.fotos f ");
		jpql.append("Where v.id =:visitante");

		TypedQuery<Visitante> query = em.createQuery(jpql.toString(), Visitante.class);
		query.setParameter("visitante", visitante.getId());
		Visitante v = query.getSingleResult();
		Hibernate.initialize(v.getPessoa());
		return v;
    }

	public void inativarVisitante(Visitante visitante) {
        String update = "UPDATE Visitante c SET c.status = :inativo WHERE c = :visitante ";

        Query query = em.createQuery(update);
        query.setParameter("inativo", StatusVisitante.I);
        query.setParameter("visitante", visitante);

        query.executeUpdate();
        
        update = "UPDATE Credenciamento c SET c.statusCredenciamento = :inativo WHERE c.visitante = :visitante ";

        query = em.createQuery(update);
        query.setParameter("inativo", StatusVisitante.I.getId());
        query.setParameter("visitante", visitante);

        query.executeUpdate();
    }
	
	public void inativarVisitantesSobResponsabilidade(Visitante visitanteResponsavel) {
		String update = "SELECT v.id FROM Visitante v where v.responsavel.id = :id";
		Query query = em.createQuery(update);
        query.setParameter("id", visitanteResponsavel.getId());
		List<Long> idsVisitantes = query.getResultList();
        
		if (idsVisitantes != null && idsVisitantes.size() > 0) {
	        update = "SELECT c.id FROM Credenciamento c WHERE c.visitante.id IN :ids";
			query = em.createQuery(update);
	        query.setParameter("ids", idsVisitantes);
	        List<Long> idsCredenciamentos = query.getResultList();
	        
	        if (idsCredenciamentos != null && idsCredenciamentos.size() > 0) {
		        update = "UPDATE Visitante v SET v.status = :inativo WHERE v.id in :ids";        
		        query = em.createQuery(update);
		        query.setParameter("inativo", StatusVisitante.I);
		        query.setParameter("ids", idsVisitantes);        
		        query.executeUpdate();
		        
		        update = "UPDATE Credenciamento c SET c.statusCredenciamento = :inativo WHERE c.id in :ids";
		        query = em.createQuery(update);
		        query.setParameter("inativo", StatusCredenciamento.INATIVO.getId());
		        query.setParameter("ids", idsCredenciamentos);
		        query.executeUpdate();
		        
		        update = "UPDATE FaseDoCredenciamento f SET f.statusCredenciamento = :inativo WHERE f.faseCredenciamento = :fase AND f.credenciamento.id in :creds";
		        query = em.createQuery(update);
		        query.setParameter("inativo", StatusCredenciamento.INATIVO);
		        query.setParameter("fase", FaseCredenciamento.APROVACAO);
		        query.setParameter("creds", idsCredenciamentos);
		        query.executeUpdate();
	        }
		}
	}
	
	public Visitante removeDocumentosDuplicados(Visitante visitante) throws ValidacaoException {
		visitante =  this.buscarPorId(visitante.getId());
		List<VisitanteDocumento> remDoc = new ArrayList<VisitanteDocumento>();
		
        for(VisitanteDocumento vd1 : visitante.getVisitanteDocumentos()) {
        	for(VisitanteDocumento vd2 : visitante.getVisitanteDocumentos()) {
        		if(vd1.getId() != vd2.getId()
        			&& vd1.getDocumento().getTipoDocumento() == vd2.getDocumento().getTipoDocumento()
        			&& vd1.getDocumento().getNumero().equals(vd2.getDocumento().getNumero())
        				) {
        			boolean jaCad = false;
        			for(VisitanteDocumento vd3 : remDoc) {
        				if(vd3.getId() == vd1.getId() || vd3.getId() == vd2.getId()) {
        					jaCad = true;
        				}
        			}
        			if(!jaCad)
        				remDoc.add(vd2);
        		}
        	}
        }
        
        List<VisitanteCertidao> remCer = new ArrayList<VisitanteCertidao>();
        
        for(VisitanteCertidao vd1 : visitante.getCertidoes()) {
        	for(VisitanteCertidao vd2 : visitante.getCertidoes()) {
        		if(vd1.getId() != vd2.getId()
        			&& vd1.getCertidao().getId() == vd2.getCertidao().getId()) {
        			boolean jaCad = false;
        			for(VisitanteCertidao vd3 : remCer) {
        				if(vd3.getCertidao().getId() == vd1.getCertidao().getId() || vd3.getCertidao().getId() == vd2.getCertidao().getId()) {
        					jaCad = true;
        				}
        			}
        			if(!jaCad)
        				remCer.add(vd2);
        		}
        	}
        }

        for(VisitanteDocumento vd : remDoc) {
	        String delete = "DELETE FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_VISITANTE_DOCUMENTO WHERE ID_VISITANTE_DOCUMENTO = " + vd.getId();

	        Query query = em.createNativeQuery(delete);

	        query.executeUpdate();
	        
	        delete = "DELETE FROM " + Sigepen.SEJUDH_SCHEMA + ".DOCUMENTO WHERE ID = " + vd.getDocumento().getId();

	        query = em.createNativeQuery(delete);

	        query.executeUpdate();
	        
        }
        
        for(VisitanteCertidao vd : remCer) {
	        String delete = "DELETE FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_VISITANTE_CERTIDAO WHERE ID_VISITANTE_CERTIDAO = " + vd.getId();

	        Query query = em.createNativeQuery(delete);

	        query.executeUpdate();
        }
        
        return this.buscarPorId(visitante.getId());
	}
	
	public Visitante salvarDocumentos(Visitante t) throws ValidacaoException {
		for(VisitanteDocumento vd : t.getVisitanteDocumentos()) {
			if(vd.getDocumento().getId() == 0)
				this.em.persist(vd.getDocumento());
			else
				this.em.merge(vd.getDocumento());
			if(vd.getId() == null || vd.getId() == 0)
				this.em.persist(vd);
			else
				this.em.merge(vd);
		}
		return t;
	}
	
	public Visitante salvarEnderecos(Visitante t) throws ValidacaoException {
		for(Endereco e : t.getPessoa().getEnderecos()) {
			if(e.getId() == 0)
				this.em.persist(e);
			else
				this.em.merge(e);
		}
		return t;
	}

	public Visitante salvarContatos(Visitante t) throws ValidacaoException {
		for(Contato c : t.getPessoa().getContatos()) {
			if(c.getId() == 0)
				this.em.persist(c);
			else
				this.em.merge(c);
		}
		return t;
	}

	public Visitante salvarCertidoes(Visitante t) throws ValidacaoException {
		for(VisitanteCertidao c : t.getCertidoes()) {
			if(c.getCertidao().getId() == null || c.getCertidao().getId() == 0)
				this.em.persist(c.getCertidao());
			else
				this.em.merge(c.getCertidao());
			if(c.getId() == null || c.getId() == 0)
				this.em.persist(c);
			else
				this.em.merge(c);
		}
		return t;
	}

	public void excluirDocumento(VisitanteDocumento vd) throws ValidacaoException {
		VisitanteDocumento visitanteDocumento = this.em.find(VisitanteDocumento.class, vd.getId()); 
		this.em.remove(visitanteDocumento);
		Documento documento = this.em.find(Documento.class, vd.getDocumento().getId());
		this.em.remove(documento);
	}
	
	public Visitante buscarVisitante(Visitante _visitante) {
		String jpql = "select v from Visitante v, UnidadePrisional"
				+ "  "
				+ " where v = :visitante";
		Query query = em.createQuery(jpql);
		query.setParameter("visitante", _visitante);
		List<Visitante> v = query.getResultList();
		Visitante ret = new Visitante();
		for(Visitante x : v) {
			Hibernate.initialize(x.getUnidadePrisional());
			ret = x;
		}
		return ret;
	}

	public int validarConjugeCadastro(Recuperando _recuperando, Visitante _visitante) throws Exception {
		Integer iVisitante = 0;
		String jpql = "SELECT COUNT(VR.ID_CREDENCIAMENTO) CT FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAMENTO VR " + 
				" INNER JOIN " + Sigepen.SEJUDH_SCHEMA + ".PARENTE PR ON PR.ID = VR.ID_PARENTE " + 
				" AND PR.PARENTESCO_ID = 5" + 
				" AND VR.ID_RECUPERANDO = :recuperando" +
				" AND VR.CODG_SITUACAO_CREDENCIAL NOT IN :status";
		
		Query query = em.createNativeQuery(jpql);
		query.setParameter("recuperando", _recuperando.getId());
		query.setParameter("status", Arrays.asList(StatusCredenciamento.INATIVO.getId(), StatusCredenciamento.NAO_APROVADO.getId()));
		Integer iRecuperando = Integer.parseInt(query.getSingleResult().toString());
		
		if(_visitante.getId() != null) {
			jpql = "SELECT COUNT(VR.ID_CREDENCIAMENTO) CT FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAMENTO VR " + 
					" INNER JOIN " + Sigepen.SEJUDH_SCHEMA + ".PARENTE PR ON PR.ID = VR.ID_PARENTE " + 
					" AND PR.PARENTESCO_ID = 5" + 
					" AND VR.ID_VISITANTE = :visitante" +
					" AND VR.CODG_SITUACAO_CREDENCIAL NOT IN :status";
			
			query = em.createNativeQuery(jpql);
			query.setParameter("visitante", _visitante.getId());
			query.setParameter("status", Arrays.asList(StatusCredenciamento.INATIVO.getId(), StatusCredenciamento.NAO_APROVADO.getId()));
			iVisitante = Integer.parseInt(query.getSingleResult().toString());
		}
		if(iRecuperando > 0)
			return 1;
		if(iVisitante > 0)
			return 2;
		return 0;
	}
	
	public void inativarVisitantes() {
    	Date hoje = new Date();
    	hoje.setHours(0);
    	hoje.setMinutes(0);
    	hoje.setSeconds(0);
        String jpql = "SELECT vc FROM FaseDoCredenciamento vc WHERE vc.faseCredenciamento = :fase AND vc.statusCredenciamento = :status AND vc.credenciamento.dataValidade < :data";
        Query query = this.em.createQuery(jpql, FaseDoCredenciamento.class);
        query.setParameter("fase", FaseCredenciamento.APROVACAO);
        query.setParameter("status", StatusCredenciamento.APROVADO);
        query.setParameter("data", hoje);
        List<FaseDoCredenciamento> lista = query.getResultList();
    	for(FaseDoCredenciamento vc : lista) {
            vc.setStatusCredenciamento(StatusCredenciamento.INATIVO);
            this.em.merge(vc);
            
            Credenciamento c = vc.getCredenciamento();
			c.setStatusCredenciamento(StatusCredenciamento.INATIVO.getId());
			this.em.merge(c);
			
			Visitante o = this.calcularStatusAtual(c.getVisitante());
			this.em.merge(o);
    	} 
	}

    public Visitante calcularStatusAtual(Visitante visitante) {
    	long id = visitante.getId();
    	visitante = this.em.find(Visitante.class, id);
    	Date hoje = new Date();
    	hoje.setHours(0);
    	hoje.setMinutes(0);
    	hoje.setSeconds(0);
    	for(Impedimento x : visitante.getImpedimentos()) {
    		if(SgpStatus.A.equals(x.getStatus()) && hoje.before(x.getFimImpedimento())) {
    			visitante.setStatus(StatusVisitante.M);
    			return visitante;
    		}
    	}

    	Integer fase = 99;
    	Integer situacao = StatusCredenciamento.PENDENTE.getId();
        if(visitante.getCredenciamentos() != null && visitante.getCredenciamentos().size() > 0) {
        	for(Credenciamento vr : visitante.getCredenciamentos()) {
        		if(StatusCredenciamento.INATIVO.getId().equals(vr.getStatusCredenciamento()) && (vr.getFasesDoCredenciamento() == null || (vr.getFasesDoCredenciamento() != null && vr.getFasesDoCredenciamento().size() == 0))) {
        			fase = FaseCredenciamento.RECEBIMENTO.getId();
        			situacao = StatusCredenciamento.INATIVO.getId();
        		} else {
        			FaseDoCredenciamento vc = getProximaFaseCredenciamento(vr);
            		vr.setFaseCredenciamento(vc.getFaseCredenciamento().getId());
            		vr.setStatusCredenciamento(vc.getStatusCredenciamento().getId());
    				if(vc.getFaseCredenciamento().getId() < fase) {
    					fase = vc.getFaseCredenciamento().getId();
    					situacao = vc.getStatusCredenciamento().getId(); 
    				} else if(vc.getFaseCredenciamento().getId() == fase) {
    					if(vc.getStatusCredenciamento().getId() < situacao)
    						situacao = vc.getStatusCredenciamento().getId(); 
    				}  
        		}
        	}
        	
        } else {
        	fase = FaseCredenciamento.RECEBIMENTO.getId();
        	situacao = StatusCredenciamento.PENDENTE.getId();
        }
        
		visitante.setFaseAtual(fase);
		if(fase == FaseCredenciamento.APROVACAO.getId() || StatusCredenciamento.INATIVO.getId().equals(situacao))
			visitante.setSituacaoAtual(situacao);
		else
			visitante.setSituacaoAtual(StatusCredenciamento.PENDENTE.getId());
        
    	StatusVisitante result = StatusVisitante.I;
        if (visitante.getCredenciamentos() != null && visitante.getCredenciamentos().size() > 0) {

            boolean existeCredenciamentoAprovado;
            boolean existeCredenciamentoDiferenteAprovado;
            boolean naoExisteCredenciamentos;
            boolean credenciamentosInativos;
            boolean credenciamentosAtivos;
            
            Visitante ovisitante = this.em.find(Visitante.class, id);
            existeCredenciamentoAprovado = isExisteCredenciamentoAprovado(ovisitante);
            naoExisteCredenciamentos = isExisteCredencialAprovadaECredencialNulo(ovisitante, existeCredenciamentoAprovado);
            existeCredenciamentoDiferenteAprovado = isExisteCredenciamentoDiferenteAprovado(ovisitante);
            credenciamentosInativos = isTodosCredenciamentosInativos(ovisitante);
            credenciamentosAtivos = isTodosCredenciamentosAtivos(ovisitante);

            if (credenciamentosInativos) {
            	result = StatusVisitante.I;
            } else if (credenciamentosAtivos) {
            	result = StatusVisitante.A;
            } else if ((existeCredenciamentoAprovado && existeCredenciamentoDiferenteAprovado) ||
                    (naoExisteCredenciamentos && existeCredenciamentoAprovado)) {
            	result = StatusVisitante.T;
            } else {
            	result = StatusVisitante.P;
            }
        }
        visitante.setStatus(result);
        return visitante;
    }
    
    public Visitante calcularStatusAtualInativar(Visitante visitante) {
    	long id = visitante.getId();
    	visitante = this.em.find(Visitante.class, id);
        
    	StatusVisitante result = StatusVisitante.I;
        if (visitante.getCredenciamentos() != null && visitante.getCredenciamentos().size() > 0) {

            boolean existeCredenciamentoAprovado;
            boolean existeCredenciamentoDiferenteAprovado;
            boolean naoExisteCredenciamentos;
            boolean credenciamentosInativos;
            boolean credenciamentosAtivos;
            
            //Visitante ovisitante = this.em.find(Visitante.class, id);
            existeCredenciamentoAprovado = isExisteCredenciamentoAprovado(visitante);
            naoExisteCredenciamentos = isExisteCredencialAprovadaECredencialNulo(visitante, existeCredenciamentoAprovado);
            existeCredenciamentoDiferenteAprovado = isExisteCredenciamentoDiferenteAprovado(visitante);
            credenciamentosInativos = isTodosCredenciamentosInativos(visitante);
            credenciamentosAtivos = isTodosCredenciamentosAtivos(visitante);

            if (credenciamentosInativos) {
            	result = StatusVisitante.I;
            } else if (credenciamentosAtivos) {
            	result = StatusVisitante.A;
            } else if ((existeCredenciamentoAprovado && existeCredenciamentoDiferenteAprovado) ||
                    (naoExisteCredenciamentos && existeCredenciamentoAprovado)) {
            	result = StatusVisitante.T;
            } else {
            	result = StatusVisitante.P;
            }
        }
        visitante.setStatus(result);
        return visitante;
    }
    
    public Visitante atribuirStatusAtual(Visitante visitante) {
    	visitante = calcularStatusAtual(visitante);
        return this.em.merge(visitante);
    }
    
    public Visitante atribuirStatusAtualInativar(Visitante visitante) {
    	visitante = calcularStatusAtualInativar(visitante);
        return this.em.merge(visitante);
    }
    
    private boolean isTodosCredenciamentosAtivos(Visitante visitante) {
        int x = 0;
        for (Credenciamento rec : visitante.getCredenciamentos()) {
            if (StatusCredenciamento.APROVADO.getId() == rec.getStatusCredenciamento()) {
                x++;
            }
        }

        if (visitante.getCredenciamentos().size() == x) {
            return true;
        }

        return false;
    }

    private boolean isTodosCredenciamentosInativos(Visitante visitante) {
        int x = 0;
        for (Credenciamento rec : visitante.getCredenciamentos()) {
            if (StatusCredenciamento.INATIVO.getId() == rec.getStatusCredenciamento()) {
                x++;
            }
        }

        if (visitante.getCredenciamentos().size() == x) {
            return true;
        }

        return false;
    }

    private boolean isExisteCredenciamentoDiferenteAprovado(Visitante visitante) {
        for (Credenciamento rec : visitante.getCredenciamentos()) {
        	if(StatusCredenciamento.APROVADO.getId() != rec.getStatusCredenciamento())
        		return true;
        }
        return false;
    }

    private boolean isExisteCredencialAprovadaECredencialNulo(Visitante visitante, boolean isExisteCredenciamentoAprovado) {
        for (Credenciamento rec : visitante.getCredenciamentos()) {
            if (rec.getUltimoCredenciamento() == null && isExisteCredenciamentoAprovado) {
                return true;
            }
        }
        return false;
    }

    private boolean isExisteCredenciamentoAprovado(Visitante visitante) {
        for (Credenciamento rec : visitante.getCredenciamentos()) {
        	if(StatusCredenciamento.APROVADO.getId() == rec.getStatusCredenciamento()) {
        		return true;
        	}
        }
        return false;
    }

    public Visitante atribuirStatusVisitantePosCredenciamento(Visitante visitante) {
    	return this.atribuirStatusAtual(visitante);
   	}
    
    public Visitante atribuirStatusVisitantePosCredenciamentoInativar(Visitante visitante) {
    	return this.atribuirStatusAtualInativar(visitante);
   	}
    
	public FaseDoCredenciamento getProximaFaseCredenciamento(Credenciamento cred) {
		FaseDoCredenciamento vc = cred.getFasesDoCredenciamento().stream()
                .max(Comparator.comparing(o -> o.getFaseCredenciamento().getId())).orElse(new FaseDoCredenciamento());

		FaseDoCredenciamento retorno = new FaseDoCredenciamento();
		if(vc.getId() == null) {
			retorno.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
			retorno.setStatusCredenciamento(StatusCredenciamento.PENDENTE);
		} else {
			retorno.setFaseCredenciamento(parametroUnidadeDAO.buscarFase(vc));
			if(FaseCredenciamento.APROVACAO == vc.getFaseCredenciamento()) {
				retorno.setStatusCredenciamento(vc.getStatusCredenciamento());
			} else {
				retorno.setStatusCredenciamento(StatusCredenciamento.PENDENTE);
			}
		}
		return retorno;
	}

	public void sincronizar() {
        String jpql = "select v from Visitante v ";
        Query query = em.createQuery(jpql, Visitante.class);
        List<Visitante> lista = query.getResultList(); 
        for(Visitante v : lista) {
        	this.atribuirStatusVisitantePosCredenciamento(v);
        }
	}

	public void salvarPessoaVisitante(Pessoa p) {
		if(p.getRecuperando() == null) {
			this.em.persist(p);
		}
	}
	
    public List<Visitante> buscarPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Visitante.class, em, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(Visitante.class));
		List<Visitante> visitantes = criteria.list(); 
		for(Visitante v : visitantes) {
			Hibernate.initialize(v.getPessoa().getDocumentos());
		}
		return visitantes;
   }

    public Visitante buscarVisitantePorIdPessoa(Long id) {
        String jpql = "select v  from Visitante v WHERE v.pessoa.id = :id";
        TypedQuery<Visitante> query = em.createQuery(jpql, Visitante.class).setParameter("id", id).setMaxResults(1);
    	List<Visitante> lista = query.getResultList();
    	if(lista != null && lista.size() > 0) {
        	Visitante v = (Visitante)lista.get(0);
        	Hibernate.initialize(v.getFotos());
        	Hibernate.initialize(v.getPessoa().getNacionalidade());
        	Hibernate.initialize(v.getPessoa().getNaturalidade());
        	Hibernate.initialize(v.getResponsavel());
        	if(v.getResponsavel() != null) {
        		Hibernate.initialize(v.getResponsavel().getPessoa());
        	}
        	Hibernate.initialize(v.getCredenciamentos());
        	return v;
    	}
    	return new Visitante();
    }

	public boolean existeCredencimento(VisitanteDocumento vd) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT COUNT(E.ID_CREDENCIAL_DOCUMENTO) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAL_DOCUMENTO E ");
			jpql.append(" WHERE E.ID_VISITANTE_DOCUMENTO = " + vd.getId());
			Query query = this.em.createNativeQuery(jpql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}		
		
	}

	public boolean existeCredencimento(VisitanteCertidao c) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("SELECT COUNT(E.ID_CREDENCIAL_CERTIDAO) FROM " + Sigepen.SEJUDH_SCHEMA + ".SGP_CREDENCIAL_CERTIDAO E ");
			jpql.append(" WHERE E.ID_VISITANTE_CERTIDAO = " + c.getId());
			Query query = this.em.createNativeQuery(jpql.toString());
			return ((BigDecimal)query.getSingleResult()).longValue() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}		
		
	}

	public FaseCredenciamento[] carregarFasesCredenciamento(long idUnidade, boolean todosRecuperandos) {
		List<FaseCredenciamento> fases = new ArrayList<FaseCredenciamento>();
		UnidadePrisional u = this.em.find(UnidadePrisional.class, idUnidade); 
		for(ParametroUnidade p : u.getParametrosUnidade()) {
			if(SimNao.S.equals(p.getValidar()) || todosRecuperandos){
				if(ParametroVisitante.RECEBIMENTO_DOCUMENTACAO.equals(p.getNome()))
					fases.add(FaseCredenciamento.RECEBIMENTO);
				else if(ParametroVisitante.CHECAGEM_DOCUMENTO.equals(p.getNome()))
					fases.add(FaseCredenciamento.CHECAGEM);
				else if(ParametroVisitante.CONFERENCIA_AUTENTICACAO.equals(p.getNome()))
					fases.add(FaseCredenciamento.CONFERENCIA);
				else if(ParametroVisitante.ANALISE_CERTIDAO.equals(p.getNome()))
					fases.add(FaseCredenciamento.ANALISE);
				else if(ParametroVisitante.APROVACAO_CADASTRO.equals(p.getNome()))
					fases.add(FaseCredenciamento.APROVACAO);
			}
		}
		FaseCredenciamento[] result = new FaseCredenciamento[fases.size()];
		for(int i = 0; i < fases.size(); i++) {
			result[i] = fases.get(i);
		}
		return result;
	}
	
    public boolean foiRecuperando(long pessoaId) {
        String jpql = "select r from Recuperando r Where r.pessoa.id =:id";
        Query query = em.createQuery(jpql, Recuperando.class).setParameter("id", pessoaId);
        List<Recuperando> lista = query.getResultList();
        if(lista != null && lista.size() > 0)
        	return true;
        return false;
    }

	public Imagem buscarImagemFrente(Long visitanteId) {
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("select i ");
		jpql.append("from VisitanteFoto f, Imagem i ");
		jpql.append("where f.tipo = 'FRENTE' ");
		jpql.append("and f.fotoAtual = 'S' ");
		jpql.append("and f.imagem.id = i.id ");
		jpql.append("and f.visitante.id = :idVisitante ");

		TypedQuery<Imagem> query = em.createQuery(jpql.toString(), Imagem.class);
		query.setParameter("idVisitante", visitanteId);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void excluirCredenciamentoDoVisitante(Credenciamento cred) {
		Visitante v = em.find(Visitante.class, cred.getVisitante().getId());
		v.getCredenciamentos().remove(cred);
		em.merge(v);
	}

	public void buscarRecuperandosDoVisitante(Visitante visitante) {
		visitante.getCredenciamentos().stream().forEach(x -> {
			Hibernate.initialize(x.getRecuperando());
		});
	}

	public boolean verificaVisitanteResponsavelPossuiCredenciamento(Visitante visitante, Recuperando recuperando, TipoVisita tipoVisita) {
		Query query = em.createQuery("Select count(vr.id) from Visitante v, Visitante vr, Credenciamento c"
				+ " where vr.id = v.responsavel.id"
				+ " and v.id = :visitanteId"
				+ " and c.visitante.id = vr.id"
				+ " and c.recuperando.id = :recuperandoId"
				+ " and c.tipoVisita.id = :tipoVisitaId"
				+ " and c.statusCredenciamento in :status");
		query.setParameter("visitanteId", visitante.getId());
		query.setParameter("recuperandoId", recuperando.getId());
		query.setParameter("tipoVisitaId", tipoVisita.getId());
		query.setParameter("status", Arrays.asList(
											StatusCredenciamento.PENDENTE.getId(), 
											StatusCredenciamento.IRREGULAR.getId(),
											StatusCredenciamento.REGULAR.getId(),
											StatusCredenciamento.APROVADO.getId()));
		
		Long count = (Long) query.getSingleResult();
		
		return (count > 0);
	}

	public void inativarCredenciamentosVinculadosAoRecuperando(Recuperando recuperando) throws Exception {
		String sql = "SELECT c.visitante.id FROM Credenciamento c where c.recuperando.id = :id and c.statusCredenciamento != :status";
		Query query = em.createQuery(sql);
        query.setParameter("id", recuperando.getId());
        query.setParameter("status", StatusCredenciamento.INATIVO.getId());
        List<Long> idsVisitantes = query.getResultList();
		
        if (idsVisitantes.size() > 0) {
        	sql = "SELECT f FROM FaseDoCredenciamento f join f.credenciamento c"
        			+ " WHERE f.faseCredenciamento = :fase AND f.statusCredenciamento = :status "
        			+ " AND c.visitante.id in :visitantes "
        			+ " AND c.recuperando.id = :recuperando";
        		    
    		query = this.em.createQuery(sql, FaseDoCredenciamento.class);
    		query.setParameter("fase", FaseCredenciamento.APROVACAO);
    		query.setParameter("status", StatusCredenciamento.APROVADO);
    		query.setParameter("visitantes", idsVisitantes);
    		query.setParameter("recuperando", recuperando.getId());
    		 
    		List<FaseDoCredenciamento> fases = query.getResultList();
    		
    		for(FaseDoCredenciamento f : fases) {
    		    f.setStatusCredenciamento(StatusCredenciamento.INATIVO);
    		    this.em.merge(f);
    		    
    		    Credenciamento c = f.getCredenciamento();
    			c.setStatusCredenciamento(StatusCredenciamento.INATIVO.getId());
    			this.em.merge(c);
    			
    			Visitante o = this.calcularStatusAtual(c.getVisitante());
    			this.em.merge(o);
    		} 
        }
	}
}
