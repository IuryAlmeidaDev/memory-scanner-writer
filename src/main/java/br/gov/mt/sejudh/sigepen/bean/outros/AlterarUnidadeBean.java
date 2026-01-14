package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.realm.SigepenRealm;
import br.gov.mt.sejudh.sigepen.service.ParametroUnidadeService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMapping(id = "AlterarUnidadeUsuario", pattern = "/usuario/alterarunidade", viewId = "/WEB-INF/pages/usuario/AlterarUnidade.xhtml")
public class AlterarUnidadeBean {

    private UnidadePrisional unidadeSelecionada;
    private List<UnidadePrisional> unidades;
    private Usuario usuario;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private ParametroUnidadeService parametroUnidadeService;

    @PostConstruct
    public void init() {
        this.usuario = SecurityUtil.getUsuario();
        carregarUnidades();

        UnidadePrisional unidadeAtual = SecurityUtil.getUnidadeSelecionada();
        if (unidadeAtual != null) {
            this.unidadeSelecionada = unidadeAtual;
        }
    }

    private void carregarUnidades() {
        try {
            this.unidades = new ArrayList<>();

            for (Acesso acesso : this.usuario.getAcessos()) {
                this.unidades.add(acesso.getUnidade());
            }

            this.unidades.sort(Comparator.comparing(UnidadePrisional::getNome));

        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO",
                "Erro ao carregar unidades: " + e.getMessage());
        }
    }

   
    private void clearCache() {
        for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
            if (realm.getName().equals("jdbcRealm")) {
                SigepenRealm realm2 = (SigepenRealm) realm;
                realm2.getAuthorizationCache().remove(SecurityUtils.getSubject().getPrincipals());
            }
        }
    }

   
    private void ativarModuloVisitante() {
        List<ParametroUnidade> parametros = parametroUnidadeService.listarPorUnidade(unidadeSelecionada);
        unidadeSelecionada.setAtivaModuloVisitante(parametros.size() > 0
                && parametros.stream().anyMatch(parametroUnidade -> parametroUnidade.getValidar() == SimNao.S));
    }

    public String alterarUnidade() {
        try {
            if (unidadeSelecionada == null) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO",
                    "Selecione uma unidade.");
                return null;
            }

      
            ativarModuloVisitante();
          
            SecurityUtil.setUnidadeSelecionada(unidadeSelecionada);
            
         
            clearCache();

            facesUtil.exibirMensagemInfo("Unidade alterada com sucesso!", true);

            return "pretty:dashboard";
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
                "Erro ao alterar unidade: " + e.getMessage());
            return null;
        }
    }

    public UnidadePrisional getUnidadeSelecionada() {
        return unidadeSelecionada;
    }

    public void setUnidadeSelecionada(UnidadePrisional unidadeSelecionada) {
        this.unidadeSelecionada = unidadeSelecionada;
    }

    public List<UnidadePrisional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadePrisional> unidades) {
        this.unidades = unidades;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}