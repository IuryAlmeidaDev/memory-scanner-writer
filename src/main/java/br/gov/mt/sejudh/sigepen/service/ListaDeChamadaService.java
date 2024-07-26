package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.ListaDeChamadaDao;
import br.gov.mt.sejudh.sigepen.model.relatorio.EstruturaRelatorio;
import br.gov.mt.sejudh.sigepen.model.relatorio.RecuperandoRelatorio;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.List;

@ManagedBean
public class ListaDeChamadaService {
    @Inject
    private ListaDeChamadaDao listaDeChamadaDao;

    public List<EstruturaRelatorio> listarEstrutura(long id) {
        return listaDeChamadaDao.listarEstrutura(id);
    }

    public List<RecuperandoRelatorio> listarRecuperando(Long idEstrutura, boolean comFoto) {
        if (comFoto) {
            return listaDeChamadaDao.listarRecuperandoComFoto(idEstrutura);
        }
        return listaDeChamadaDao.listarRecuperando(idEstrutura);
    }
}
