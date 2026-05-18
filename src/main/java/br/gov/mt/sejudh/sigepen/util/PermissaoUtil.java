package br.gov.mt.sejudh.sigepen.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;

@Named
@RequestScoped
public class PermissaoUtil {

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private SecurityUtil security;

    public void filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos() {
        FiltroRecuperandoView filtroRecuperando = new FiltroRecuperandoView();
        filtroRecuperando.setListarTodasUnidades(security.possuiPermissao("ListarTodosRecuperandos"));

        facesUtil.setObjetoSessao("filtroRecuperando", filtroRecuperando);
    }
    
    public void filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos(Boolean dietaEspecial) {
        FiltroRecuperandoView filtroRecuperando = new FiltroRecuperandoView();
        filtroRecuperando.setListarTodasUnidades(security.possuiPermissao("ListarTodosRecuperandos"));
        filtroRecuperando.setIsDietaEspecial(dietaEspecial);

        facesUtil.setObjetoSessao("filtroRecuperando", filtroRecuperando);
    }
}
