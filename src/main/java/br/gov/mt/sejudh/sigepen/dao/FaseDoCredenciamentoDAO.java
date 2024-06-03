package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@RequestScoped
public class FaseDoCredenciamentoDAO extends BaseDAO<FaseDoCredenciamento, Long> {

	private static final long serialVersionUID = -8114962078076068852L;
	@Inject
	private CredenciamentoDAO credenciamentoDAO;

	public Integer inativarCredenciamentoRecuperando(Credenciamento Credenciamento) {
        String update = "UPDATE FaseDoCredenciamento c SET c.statusCredenciamento = :inativo WHERE c.Credenciamento = :cred ";

        Query query = em.createQuery(update);
        query.setParameter("inativo", StatusCredenciamento.INATIVO);
        query.setParameter("cred", Credenciamento);

        return query.executeUpdate();
    }
	
	public Integer inativarUltimoCredenciamentoVR(Credenciamento vr) {
        String update = "UPDATE FaseDoCredenciamento c SET c.statusCredenciamento = :inativo WHERE c = :fase ";

        Query query = em.createQuery(update);
        query.setParameter("inativo", StatusCredenciamento.INATIVO);
        query.setParameter("fase", vr.getUltimoCredenciamento());

        return query.executeUpdate();
    }
	
	public void excluirFaseCredenciamento(FaseDoCredenciamento fase) {
		this.em.remove(fase);
	}
	
	public FaseDoCredenciamento buscarCredencialPorId(long id) {
		FaseDoCredenciamento fase = this.em.find(FaseDoCredenciamento.class, id);
		if(fase != null) {
			Hibernate.initialize(fase.getCredenciamento());
			if(fase.getCredenciamento() != null) {
				Hibernate.initialize(fase.getCredenciamento().getVisitante());
				if(fase.getCredenciamento().getVisitante() != null) {
			    	Hibernate.initialize(fase.getCredenciamento().getVisitante().getTipoVisita());
			    	Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getFotos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getImpedimentos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getCertidoes());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getCredenciamentos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getIdentidadeDeGenero());
			        fase.getCredenciamento().getVisitante().getCredenciamentos().forEach(Credenciamento -> {
			            Optional.ofNullable(Credenciamento.getRecuperando()).ifPresent(
			                    recuperando -> Hibernate.initialize(recuperando.getFotos())
			            );
			            Hibernate.initialize(Credenciamento.getFasesDoCredenciamento());
			            Hibernate.initialize(Credenciamento.getTipoVisita());
			        });
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getVisitanteDocumentos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getNacionalidade());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getNaturalidade());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getPais());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getDocumentos());
			        if (fase.getCredenciamento().getVisitante().getResponsavel() != null) {
			            Hibernate.initialize(fase.getCredenciamento().getVisitante().getResponsavel().getFotos());
			            if(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa() != null) {
			            	if(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa().getNacionalidade() != null) {
			            		Hibernate.initialize(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa().getNacionalidade().getDescricao());
			            	}
			                if(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa().getPais() != null) {
			                	Hibernate.initialize(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa().getPais().getNome());
			                }
			                Hibernate.initialize(fase.getCredenciamento().getVisitante().getResponsavel().getPessoa().getNaturalidade());
			            }
			            Hibernate.initialize(fase.getCredenciamento().getVisitante().getResponsavel().getIdentidadeDeGenero());
			        }
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getContatos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getEnderecos());
			        Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getBiometria());
			        if (fase.getCredenciamento().getVisitante().getPessoa().getBiometria() != null) {
			            Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getBiometria().getDedos());
			        }
			        if(fase.getCredenciamento().getVisitante().getPessoa() != null)
			        	Hibernate.initialize(fase.getCredenciamento().getVisitante().getPessoa().getVisitante());
					
				}
				Hibernate.initialize(fase.getCredenciamento().getRecuperando());
			}
				
			Hibernate.initialize(fase.getCredenciamento().getFasesDoCredenciamento());
			Hibernate.initialize(fase.getCredenciamento().getUltimoCredenciamento());
			Hibernate.initialize(fase.getCredencialCertidaos());
			Hibernate.initialize(fase.getCredencialDocumentos());
			
		}
		return fase;
	}

	public FaseDoCredenciamento buscarDocumentosCertidoes(FaseDoCredenciamento c) {
		FaseDoCredenciamento cred = this.em.find(FaseDoCredenciamento.class, c.getId());
		if(cred != null) {
			Hibernate.initialize(cred.getCredencialDocumentos());
			for(CredencialDocumento cd : cred.getCredencialDocumentos()) {
				Hibernate.initialize(cd.getVisitanteDocumento());
			}
			
			Hibernate.initialize(cred.getCredencialCertidaos());
		}
		return cred;
	}

	public FaseDoCredenciamento buscarCredenciamento(long idCredenciamento) {
		FaseDoCredenciamento cred = this.em.find(FaseDoCredenciamento.class, idCredenciamento);
		if(cred != null) {
			Hibernate.initialize(cred.getCredencialDocumentos());
			Hibernate.initialize(cred.getCredencialCertidaos());
			Hibernate.initialize(cred.getCredenciamento().getVisitante().getVisitanteDocumentos());
			Hibernate.initialize(cred.getCredenciamento().getVisitante().getCertidoes());
		}
		return cred;
	}

	public List<FaseDoCredenciamento> buscarCredenciamentos(Visitante visitante, Recuperando recuperando) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante AND recuperando = :recuperando ORDER BY id DESC";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        query.setParameter("recuperando", recuperando);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
    		jpql = "SELECT vc FROM FaseDoCredenciamento vc WHERE vc.Credenciamento = :vr ORDER BY faseCredenciamento ASC ";
            query = this.em.createQuery(jpql, FaseDoCredenciamento.class);
            query.setParameter("vr", lista.get(0));
            
            List<FaseDoCredenciamento> vc = query.getResultList();
            return vc;
        }
        
        
        return new ArrayList<FaseDoCredenciamento>();
		
	}
	
	public FaseDoCredenciamento buscarCredenciamentos(Visitante visitante) {
		String jpql = "SELECT vr FROM Credenciamento vr WHERE visitante = :visitante ORDER BY id DESC";
        Query query = this.em.createQuery(jpql, Credenciamento.class);
        query.setParameter("visitante", visitante);
        List<Credenciamento> lista = query.getResultList();
        if(lista.size() > 0) {
    		jpql = "SELECT vc FROM FaseDoCredenciamento vc WHERE vc.Credenciamento = :vr ORDER BY faseCredenciamento ASC ";
            query = this.em.createQuery(jpql, FaseDoCredenciamento.class);
            query.setParameter("vr", lista.get(0));
            
            List<FaseDoCredenciamento> vc = query.getResultList();
            for(FaseDoCredenciamento x : vc)
            	return x;
        }
        
        
        return null;
		
	}
	
	public FaseDoCredenciamento atribuirProximaFaseCredenciamento(Credenciamento credenciamento) {
		
//		Credenciamento vr = this.em.find(Credenciamento.class, credenciamento.getId());
		Credenciamento vr = this.credenciamentoDAO.listarCredenciamentoFaseCredencimaneto(credenciamento.getId());
		Hibernate.initialize(vr.getFasesDoCredenciamento());
		for(FaseDoCredenciamento vc : vr.getFasesDoCredenciamento()) {
			if(vc.getFaseCredenciamento().getId() == credenciamento.getFaseCredenciamento())
				return vc;
		}

		FaseDoCredenciamento credenciamentoAnterior = credenciamento.getFasesDoCredenciamento().stream()
                .max(Comparator.comparing(o -> o.getFaseCredenciamento().getId())).orElse(new FaseDoCredenciamento());

		FaseDoCredenciamento fase = new FaseDoCredenciamento();
        fase.setCredenciamento(vr);
        fase.setDataMinima(credenciamentoAnterior.getDataRecebimento());
        fase.setUsuarioAprovacao(SecurityUtil.getUsuario());

        fase.setFaseCredenciamento(FaseCredenciamento.getValue(credenciamento.getFaseCredenciamento()));
        fase.setStatusCredenciamento(StatusCredenciamento.PENDENTE);

        return fase;
		
	}
}
