package br.gov.mt.sejudh.sigepen.util;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.shiro.SecurityUtils;

import br.gov.mt.sejudh.sigepen.enumerator.Dashboard;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import java.io.Serializable;

@Named
@SessionScoped
public class SecurityUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String getLoginUsuario() {
		return SecurityUtils.getSubject().getPrincipal().toString();
	}
	
	public static String getNomeUsuario() {
		return (getUsuario().getServidor() == null) ? "Administrador" : getUsuario().getServidor().getNome();
	}
	
	public static String getMatriculaUsuario() {
		return (getUsuario().getServidor() == null) ? "0" : String.valueOf(getUsuario().getServidor().getMatricula());
	}
	
	public static String getNomeUnidadeUsuario() {
		return (getUnidadeSelecionada() == null) ? "SEJUDH" : getUnidadeSelecionada().getNome();
	}
	
	public static Usuario getUsuario() {
		Usuario usuario = (Usuario) SecurityUtils.getSubject().getSession().getAttribute("usuario");
		return usuario;
	}

	public static UnidadePrisional getUnidadeSelecionada() {
		UnidadePrisional unidade = (UnidadePrisional) SecurityUtils.getSubject().getSession().getAttribute("unidadeSelecionada");
		return unidade;
	}
	
	public static void setUnidadeSelecionada(UnidadePrisional unidade) {
		SecurityUtils.getSubject().getSession().setAttribute("unidadeSelecionada", unidade);
	}
	
	public boolean isAutenticado() {
		return SecurityUtils.getSubject().isAuthenticated();
	}
	
	public boolean possuiPerfil(String perfil) {
		return SecurityUtils.getSubject().hasRole(perfil);
	}
	
	public boolean isAtivarModuloVisitantes() {
		UnidadePrisional unidade = (UnidadePrisional) SecurityUtils.getSubject().getSession().getAttribute("unidadeSelecionada");
		return unidade.getAtivaModuloVisitante();
	}

	public boolean possuiPermissao(String permissao) {
		if(permissao == Dashboard.SOLICITACAO_VISITA_PENDENTE.getDescricao() && !this.isAtivarModuloVisitantes()) {
			return false;
		}

		return SecurityUtils.getSubject().isPermitted(permissao);
	}
	
	public boolean exibeCategoriaMenu(String categoria) {		
		boolean resultado = false;
		
		switch (categoria.toLowerCase()) {
			case "tabelasauxiliares":
				resultado =		this.possuiPermissao("ListarCategoria") ||
								this.possuiPermissao("ListarCargo") ||
								this.possuiPermissao("ListarCrime") ||
								this.possuiPermissao("ListarFuncao") ||
								this.possuiPermissao("ListarEstabelecimento") ||
								this.possuiPermissao("ListarOrgao") ||
								this.possuiPermissao("ListarPessoa") ||
								this.possuiPermissao("ListarServidor") ||
								this.possuiPermissao("ListarUnidade") ||
								this.possuiPermissao("ListarTipoVisita") || 
								this.possuiPermissao("ListarTipoImpedimento") ||
								this.possuiPermissao("ListarCertidao") ||
								this.possuiPermissao("ListarTipoFaltaDisciplinar") ||
								this.possuiPermissao("ListarQuadroVagas");
				break;

			case "controledeacesso":
				resultado = this.possuiPermissao("ListarUsuario") || 
							this.possuiPermissao("ListarPerfil")  ||
							this.possuiPermissao("ListarUsuarioResetSenha")  || 
							this.possuiPermissao("ListarPermissao");
				break;
							
			case "crime":
				resultado = this.possuiPermissao("CadastrarCrime") ||
						this.possuiPermissao("AlterarCrime") || 
						this.possuiPermissao("ExcluirCrime");
				break;
				
			case "recuperando":
				resultado = this.possuiPermissao("ListarRecuperando") || 
							this.possuiPermissao("ListarFaltaDisciplinar") || 
							this.possuiPermissao("UnificarRecuperando") ||
							this.possuiPermissao("ExcluirProcesso");
				break;
				
			case "movimentacoes":
				resultado = this.possuiPermissao("ListarPedidoTransito") ||
							this.possuiPermissao("ListarMovimentacaoExterna") ||
							this.possuiPermissao("ListarMovimentacaoInterna") ||
							this.possuiPermissao("ListarTransferencia") ||
							this.possuiPermissao("ListarReceberTransferencia") ||
							this.possuiPermissao("ListarRemanejamento");
				break;
				
			case "auditoria":
				resultado = this.possuiPermissao("ListarLog");
				break;
            case "visitantes":
                resultado = this.possuiPermissao("ListarVisitante")
                        || this.possuiPermissao("ListarSolicitacaoVisita")
                        || this.possuiPermissao("ListarImpedimento")
                        || this.possuiPermissao("ListarEntradaSaida")
        				|| this.possuiPermissao("ConsultarListaVisitas");
                break;
			case "relatorio":
				resultado = this.possuiPermissao("Relatorio");
				break;
			case "mapaalimentacao":
				resultado = this.possuiPermissao("ListarFornecedor")
				|| this.possuiPermissao("ListarContrato")
				|| this.possuiPermissao("ListarApostilamento")
				|| this.possuiPermissao("ListarRefeicao")
				|| this.possuiPermissao("CadastrarContato")
				|| this.possuiPermissao("AlterarContato")
				|| this.possuiPermissao("ListarDietaEspecial")
				|| this.possuiPermissao("ListarGerarPedido")
				|| this.possuiPermissao("ListarGerarPedidoEstrutura")
				|| this.possuiPermissao("ListarMapaConsolidado");
				break;
				
			case "servidor":
				resultado = this.possuiPermissao("PermiteCadastrarPlantao");
				break;
				
			case "plantao":
				resultado = this.possuiPermissao("CadastrarPlantao")
				|| this.possuiPermissao("ListarPlantao")
				|| this.possuiPermissao("ExcluirPlantao")
				|| this.possuiPermissao("CadastrarRegistroPlantao")
				|| this.possuiPermissao("ListarRegistroPlantao");
				break;	
		}
		
		return resultado;
	}
}
