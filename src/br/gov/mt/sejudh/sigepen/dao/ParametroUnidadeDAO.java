package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;

@RequestScoped
public class ParametroUnidadeDAO extends BaseDAO<ParametroUnidade, Long> {

	private static final long serialVersionUID = 9130817008392967316L;
	
	public FaseCredenciamento buscarFase(FaseDoCredenciamento vc) {
		
        List<ParametroUnidade> parametros = this.buscaPorCampo("unidadePrisional", vc.getCredenciamento().getUnidadePrisional());
        FaseCredenciamento fase = vc.getFaseCredenciamento();

        switch (fase) {
            case RECEBIMENTO:
                return this.atribuirFaseRecebimento(fase, parametros);
            case CHECAGEM:
                return this.atribuirFaseChecagem(fase, parametros);
            case CONFERENCIA:
            	return this.atribuirFaseConferencia(fase, parametros);
            case ANALISE:
            	return this.atribuirFaseAnalise(fase);
            case APROVACAO:
            	return FaseCredenciamento.APROVACAO;
            default:
                return FaseCredenciamento.RECEBIMENTO;
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
	
}
