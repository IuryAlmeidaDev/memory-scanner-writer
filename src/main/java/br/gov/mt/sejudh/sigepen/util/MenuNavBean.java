package br.gov.mt.sejudh.sigepen.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.PrettyContext;

/**
 * Detecta a rota PrettyFaces corrente e expõe à sidebar (menu.xhtml) qual grupo
 * deve abrir e qual item deve aparecer ativo.
 *
 * O PrimeFaces 5.2.1 não detecta rota ativa server-side (o {@code PanelMenuRenderer}
 * emite sempre {@code ui-state-default}), então a detecção é feita aqui: o id do
 * {@code @URLMapping} (token do {@code outcome="pretty:..."}) tem o verbo CRUD
 * removido, formando a "família" da feature — telas irmãs
 * ({@code Listar/Cadastrar/Alterar/Visualizar...}) compartilham a mesma família.
 *
 * Espelha {@link SecurityUtil} (CDI {@code @Named}); request-scoped porque a
 * navegação PrettyFaces é reload de página.
 */
@Named("menuNav")
@RequestScoped
public class MenuNavBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Verbos CRUD que prefixam ids de {@code @URLMapping} (espelha {@code MetodosDePaginaBean}). */
	private static final String[] VERBOS = {
			"Listar", "Cadastrar", "Alterar", "Visualizar", "Iniciar",
			"Concluir", "Finalizar", "Receber", "Credenciar"
	};

	/** família → grupoKey (1 entrada por item de menu renderável; sem explosão de verbos). */
	private static final Map<String, String> FAMILIA_GRUPO;

	static {
		Map<String, String> m = new HashMap<String, String>();

		// Tabelas Auxiliares
		put(m, "TabelasAuxiliares", "Categoria", "Cargo", "Funcao", "Crime", "Orgao",
				"Servidor", "Pessoa", "Estabelecimento", "Unidade", "QuadroVagas",
				"TipoVisita", "TipoImpedimento", "Certidao", "TipoFaltaDisciplinar",
				"TipoOcorrencia");

		// Recuperando
		put(m, "Recuperando", "Recuperando", "FaltaDisciplinar");

		// Relatórios
		put(m, "Relatorios", "Relatorio");

		// Visita
		put(m, "Visita", "Visitante", "SolicitacaoVisita", "Impedimento",
				"EntradaSaida", "ConsultarListaVisitas");

		// Movimentações  (ReceberTransferencia → família "Transferencia")
		put(m, "Movimentacoes", "PedidoTransito", "MovimentacaoInterna",
				"MovimentacaoExterna", "Remanejamento", "Transferencia");

		// Gestão de Alimentação
		put(m, "Alimentacao", "GerarPedido", "HistoricoPedidoAlimento",
				"MapaConsolidado", "DietaEspecial", "Alimento", "TipoDieta",
				"Cardapio", "Fornecedor", "Contrato", "Apostilamento", "HorarioRefeicao");

		// Plantão
		put(m, "Plantao", "Plantao");

		// Auditoria
		put(m, "Auditoria", "Log");

		// Controle de Acesso
		put(m, "ControleAcesso", "Perfil", "Permissao", "Usuario", "UsuarioResetSenha");

		// Usuário (pessoal)
		put(m, "Usuario", "Senha", "UnidadeUsuario");

		// Administrador
		put(m, "Administrador", "Parametro", "NServer");

		FAMILIA_GRUPO = Collections.unmodifiableMap(m);
	}

	private static void put(Map<String, String> m, String grupo, String... familias) {
		for (String f : familias) {
			m.put(f, grupo);
		}
	}

	/** Id do {@code @URLMapping} corrente, ou {@code ""} se não houver rota (login/erro). */
	public String getRotaId() {
		PrettyContext ctx = PrettyContext.getCurrentInstance();
		if (ctx == null || ctx.getCurrentMapping() == null) {
			return "";
		}
		String id = ctx.getCurrentMapping().getId();
		return (id == null) ? "" : id;
	}

	/** Remove o prefixo de verbo CRUD do id. {@code CadastrarAlimento → Alimento}. */
	public String familia(String id) {
		if (id == null || id.isEmpty()) {
			return "";
		}
		for (String verbo : VERBOS) {
			if (id.length() > verbo.length()
					&& id.startsWith(verbo)
					&& Character.isUpperCase(id.charAt(verbo.length()))) {
				return id.substring(verbo.length());
			}
		}
		return id; // telas sem verbo (Relatorio, Parametro, NServer, ...)
	}

	/** Item ativo: mesma família que a rota corrente. */
	public boolean itemAtivo(String itemId) {
		String f = familia(itemId);
		return !f.isEmpty() && f.equals(familia(getRotaId()));
	}

	/** Grupo expandido: a família da rota corrente pertence a esse grupo (acordeão por rota). */
	public boolean grupoExpandido(String grupoKey) {
		String grupoDaRota = FAMILIA_GRUPO.get(familia(getRotaId()));
		return grupoKey != null && grupoKey.equals(grupoDaRota);
	}
}
