package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.OcorrenciaDAO;
import br.gov.mt.sejudh.sigepen.model.AutoriaRegistro;
import br.gov.mt.sejudh.sigepen.model.Ocorrencia;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class OcorrenciaService extends BaseService<Ocorrencia, Long> {

	private static final long serialVersionUID = 7230046305068303409L;
	
	@Inject
    private OcorrenciaDAO ocorrenciaDAO;

    @Inject
    private AuditoriaDAO auditoriaDAO;

    @Inject
    public void setOcorrenciaDAO(OcorrenciaDAO ocorrenciaDAO) {
        this.ocorrenciaDAO = ocorrenciaDAO;
        super.setDao(ocorrenciaDAO);
    }

    public List<Ocorrencia> listarTodasOcorrencias() {
        return ocorrenciaDAO.listarTodasOcorrencias();
    }
	
	public Ocorrencia buscarPorIdComTipo(Long id)  {
		Ocorrencia ocorrencia = ocorrenciaDAO.buscarPorIdComTipo(id);
		ocorrencia = dao.buscarAutorRegistro(ocorrencia);
		return ocorrencia;
	}

	public void excluirLogico(Ocorrencia ocorrencia) {
		if (ocorrencia != null) {
			ocorrencia.setAtivo(false);
			ocorrenciaDAO.salvar(ocorrencia);
		}
	}

    public List<Ocorrencia> buscarOcorrenciasPorPlantao(Long plantaoId) {
        List<Ocorrencia> lista = ocorrenciaDAO.buscarPorPlantao(plantaoId);
        if (lista != null && !lista.isEmpty()) {
            List<Long> ids = lista.stream().map(Ocorrencia::getId).collect(Collectors.toList());
            Map<Long, AutoriaRegistro> primeiros = auditoriaDAO.autoresRegistroOcorrenciaPrimeiraRevisao(ids);
            Map<Long, AutoriaRegistro> ultimos = auditoriaDAO.autoresRegistroOcorrenciaUltimaRevisao(ids);
            for (Ocorrencia o : lista) {
                Long id = o.getId();
                if (primeiros != null && primeiros.containsKey(id)) {
                    o.setAutorRegistro(primeiros.get(id));
                }
                if (ultimos != null && ultimos.containsKey(id)) {
                    o.setAutorUltimaAlteracao(ultimos.get(id));
                }
            }
        }
        return lista;
    }
}
