package br.gov.mt.sejudh.sigepen.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.ImpedimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroImpedimento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;

@Stateless
public class ImpedimentoService extends BaseService<Impedimento, Long> {

	private static final long serialVersionUID = 188964177342185804L;

    private ImpedimentoDAO impedimentoDAO;
    
    @Inject
    private VisitanteService visitanteService;
    
    @Inject
    public void setImpedimentoDAO(ImpedimentoDAO impedimentoDAO) {
		this.impedimentoDAO = impedimentoDAO;
		this.setDao(impedimentoDAO);
	}

    @Override
    public Impedimento salvar(Impedimento impedimento) throws ValidacaoException {
    	this.validarDataImpedimento(impedimento);
    	this.validarDataFimImpedimento(impedimento);
    	if(impedimento.getId() == null) {
            this.impedirDependentesVisitante(impedimento);
            this.impedirVisitante(impedimento);
		} else {
			alterarStatusDependentes(impedimento);
		}

        return super.salvar(impedimento);
    }

    private void impedirDependentesVisitante(Impedimento impedimento) throws ValidacaoException {

        List<Visitante> dependentes = this.visitanteService.buscarDependentesDoVisitanteResponsavel(impedimento.getVisitante());

        for (Visitante dependente : dependentes) {
            Impedimento impedimentoDependente = new Impedimento();
            impedimentoDependente.setVisitante(dependente);
            impedimentoDependente.setDataImpedimento(impedimento.getDataImpedimento());
            impedimentoDependente.setFimImpedimento(impedimento.getFimImpedimento());
            impedimentoDependente.setObservacao(this.impedimentoResponsavel(impedimento));
            impedimentoDependente.setStatus(impedimento.getStatus());
            impedimentoDependente.setNomeDocumento(impedimento.getNomeDocumento());
            impedimentoDependente.setNumeroDocumento(impedimento.getNumeroDocumento());
            impedimentoDependente.setTipoImpedimento(impedimento.getTipoImpedimento());

            super.salvar(impedimentoDependente);
            this.impedirVisitante(impedimentoDependente);
        }
    }

    private void impedirVisitante(Impedimento impedimento) throws ValidacaoException {
        Visitante visitante = impedimento.getVisitante();
        StatusVisitante statusAnterior = visitante.getStatus();
        boolean impedimentoAtivo = this.impedimentoPeriodoAtivo(impedimento) && impedimento.getStatus() == SgpStatus.A;

        if (impedimentoAtivo) {
            visitante.setStatus(StatusVisitante.M);
        }
        if (statusAnterior == StatusVisitante.M && !impedimentoAtivo) {
            visitante.setStatus(StatusVisitante.P);
        }

        this.visitanteService.salvar(visitante);
    }

    private String impedimentoResponsavel(Impedimento impedimento) {
        return "Responsável(".concat(impedimento.getVisitante().getPessoa().getNome()).concat(") impedido.");
    }

    @Override
    protected void validaDuplicidade(Impedimento impedimento) throws ValidacaoException {
        if (impedimento.getStatus() == SgpStatus.I) {
            return;
        }

        FiltroImpedimento filtro = new FiltroImpedimento();
        filtro.setVisitante(impedimento.getVisitante());
        filtro.setStatus(SgpStatus.A);

        List<Impedimento> list = dao.buscaPorFiltro(filtro, false);
        if (list.stream().anyMatch(x -> impedimento.getId() == null || !x.getId().equals(impedimento.getId()))) {
            throw new ValidacaoException("Operação não permitida: Existe Impedimento Ativo para o Visitante.");
        }
    }

    @Override
    protected void validarExclusao(Impedimento impedimento) throws ValidacaoException {
        if (impedimento.getFimImpedimento() == null) {
            return;
        }

        if (this.impedimentoPeriodoAtivo(impedimento)) {
            throw new ValidacaoException("Não é possível excluir! Impedimento do visitante está em período ativo!");
        }
    }

    private Boolean impedimentoPeriodoAtivo(Impedimento impedimento) {
        Date dataHoje = Calendar.getInstance().getTime();
        if(impedimento.getFimImpedimento() != null) {
            return dataHoje.after(impedimento.getDataImpedimento()) && dataHoje.before(impedimento.getFimImpedimento());
        }

        return dataHoje.after(impedimento.getDataImpedimento());
    }
    
    private void validarDataImpedimento(Impedimento impedimento) throws ValidacaoException {
    	Date dt = this.getDate();
    	if(impedimento.getDataImpedimento().after(dt)) {
    		throw new ValidacaoException("A data do impedimento não deve ser posterior a data atual.");
    	}
    }

    private void validarDataFimImpedimento(Impedimento impedimento) throws ValidacaoException {
    	Date dt = this.getDate();
    	if(impedimento.getFimImpedimento().before(dt)) {
    		throw new ValidacaoException("A data fim do impedimento não deve ser anterior a data atual.");
    	}
    }

	private Date getDate() {
		final GregorianCalendar gc = new GregorianCalendar();
	    gc.setTime( new Date() );
	    gc.set( Calendar.HOUR_OF_DAY, 0 );
	    gc.set( Calendar.MINUTE, 0 );
	    gc.set( Calendar.SECOND, 0 );
	    gc.set( Calendar.MILLISECOND, 0 );
	    return gc.getTime();		
	}
	
	private void alterarStatusDependentes(Impedimento impedimento) throws ValidacaoException {

		List<Visitante> dependentes = this.visitanteService
				.buscarDependentesDoVisitanteResponsavel(impedimento.getVisitante());
		String nomeResponsavel = impedimento.getVisitante().getPessoa().getNome();

		for (Visitante dependente : dependentes) {
			
			//if (dependente.getResponsavel().getId().equals(impedimento.getVisitante().getId())) {
				for (Impedimento x : dependente.getImpedimentos()) {
					if (x.getObservacao() != null && x.getObservacao().contains(nomeResponsavel)) {
						x.setStatus(impedimento.getStatus());
						x.setDataImpedimento(impedimento.getDataImpedimento());
						x.setFimImpedimento(impedimento.getFimImpedimento());
						if (SgpStatus.A == impedimento.getStatus())
							x.getVisitante().setStatus(StatusVisitante.M);
						else {
							Visitante o = this.visitanteService.calcularStatusAtual(dependente);
							x.getVisitante().setStatus(o.getStatus());
						}
						this.impedimentoDAO.salvar(x);
					}
				}
			}
		//}
		
		this.impedimentoDAO.salvar(impedimento);
		
		Visitante visitante = this.visitanteService.calcularStatusAtual(impedimento.getVisitante());
		if (SgpStatus.A == impedimento.getStatus())
			visitante.setStatus(StatusVisitante.M);
		this.visitanteService.salvar(visitante);

	}

	public void excluir(Impedimento impedimento) throws ValidacaoException {
		List<Visitante> dependentes = this.visitanteService
				.buscarDependentesDoVisitanteResponsavel(impedimento.getVisitante());
		String nomeResponsavel = impedimento.getVisitante().getPessoa().getNome();

		for (Visitante dependente : dependentes) {
			int i = 0;
			while (true) {
				if (dependente.getImpedimentos().size() > 0 && i < dependente.getImpedimentos().size()) {
					Impedimento x = dependente.getImpedimentos().get(i);
					if (x.getObservacao().contains(nomeResponsavel)) {
						dependente.getImpedimentos().remove(x);
       				this.visitanteService.salvar(dependente);
						Visitante visitanteDep = this.visitanteService.calcularStatusAtual(dependente);
						this.visitanteService.salvar(visitanteDep);
					} else
						i++;
				} else
					break;
			}
		}

		Visitante visitante = this.visitanteService.calcularStatusAtual(impedimento.getVisitante());
		visitante.getImpedimentos().remove(impedimento);
		visitante = this.visitanteService.calcularStatusAtual(impedimento.getVisitante());
		this.visitanteService.salvar(visitante);

	}
	
	public void inativarImpedimentosVencidos() {
		this.impedimentoDAO.inativarImpedimentosVencidos();
	}

	public int buscarQuantidadeFiltrada(FiltroImpedimento filtro) {
		return this.impedimentoDAO.buscarQuantidadeFiltrada(filtro);
	}

	public List<Impedimento> buscarImpedimentosPorFiltro(FiltroImpedimento filtro, boolean isBuscaRelatorio) {
		return this.impedimentoDAO.buscarImpedimentosPorFiltro(filtro, isBuscaRelatorio);
	}

	public List<Impedimento> buscaPorFiltro(FiltroConsulta filtro, boolean isRelatorio) {
        return this.dao.buscaPorFiltro(filtro, isRelatorio);
    }
}
