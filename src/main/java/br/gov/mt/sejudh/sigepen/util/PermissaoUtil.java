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
        if(security.possuiPermissao("ListarTodosRecuperandos")) {
        	FiltroRecuperandoView filtroRecuperando = new FiltroRecuperandoView();
            filtroRecuperando.setListarTodasUnidades(true);

            facesUtil.setObjetoSessao("filtroRecuperando", filtroRecuperando);
        }
    }
    
    public void filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos(Boolean dietaEspecial) {
        if(security.possuiPermissao("ListarTodosRecuperandos")) {
        	FiltroRecuperandoView filtroRecuperando = new FiltroRecuperandoView();
            filtroRecuperando.setListarTodasUnidades(true);
            filtroRecuperando.setIsDietaEspecial(dietaEspecial);

            facesUtil.setObjetoSessao("filtroRecuperando", filtroRecuperando);
        }
    }
}
