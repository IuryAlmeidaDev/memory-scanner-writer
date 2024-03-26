package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.ParametroUnidadeDAO;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.CredenciamentoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.FaseDoCredenciamentoDTO;

@Stateless
public class ParametroUnidadeService extends BaseService<ParametroUnidade, Long> {

	
	private static final long serialVersionUID = -427059253422658448L;
	
    private ParametroUnidadeDAO parametroUnidadeDAO;
    
    @Inject
    public void setParametroUnidadeDAO(ParametroUnidadeDAO parametroUnidadeDAO) {
		this.parametroUnidadeDAO = parametroUnidadeDAO;
		this.setDao(parametroUnidadeDAO);
	}

    public List<ParametroUnidade> listarPorUnidade(UnidadePrisional unidadePrisional) {
        return this.dao.buscaPorCampo("unidadePrisional", unidadePrisional);
    }

    public void salvarParametrosVisitanteUnidade(List<ParametroUnidade> parametrosVisitante, UnidadePrisional unidadePrisional) {
        if (unidadePrisional.getId() > 0) {
            unidadePrisional.setParametrosUnidade(this.listarPorUnidade(unidadePrisional));
        }

        for (ParametroUnidade parametroVisitante : parametrosVisitante) {
            ParametroUnidade parametroUnidade = unidadePrisional.getParametrosUnidade().stream().filter(
                    x -> x.getNome() == parametroVisitante.getNome()).findFirst().orElse(null);

            if (parametroUnidade == null) {
                ParametroUnidade novoParametroUnidade = new ParametroUnidade();
                novoParametroUnidade.setNome(parametroVisitante.getNome());
                novoParametroUnidade.setDescricao(parametroVisitante.getNome().getNome());
                novoParametroUnidade.setUnidadePrisional(unidadePrisional);
                novoParametroUnidade.setValidar(SimNao.getByBool(parametroVisitante.getValueValidar()));

                unidadePrisional.getParametrosUnidade().add(novoParametroUnidade);
                continue;
            }
            parametroUnidade.setValidar(SimNao.getByBool(parametroVisitante.getValueValidar()));
        }
    }

	public FaseCredenciamento buscarFase(CredenciamentoDTO cred, FaseDoCredenciamentoDTO fase) {
		return parametroUnidadeDAO.buscarFase(cred, fase);
	}
}
