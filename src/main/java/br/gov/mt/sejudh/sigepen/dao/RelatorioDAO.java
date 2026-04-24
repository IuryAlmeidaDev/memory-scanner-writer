package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;

import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.auxiliar.Relatorio;
import br.gov.mt.sejudh.sigepen.util.Permissoes;

public class RelatorioDAO {

	public List<Relatorio> buscarPorFuncionalidade(Funcionalidade funcionalidade) {
		int i = (int) funcionalidade.getId();
		switch (i) {
		case 3:
			return relatoriosRecuperando();

		default:
			break;
		}
		return new ArrayList<Relatorio>();
	}

	private List<Relatorio> relatoriosRecuperando() {
		List<Relatorio> lista = new ArrayList<Relatorio>();
		lista.add(new Relatorio("relatorio/recuperandos/infopen", "Infopen"));
		lista.add(new Relatorio("relatorio/recuperandos/entrada_saida", "Entrada e Saída de Recuperandos"));
		lista.add(new Relatorio("relatorio/recuperandos/recuperando_secao_jurisdicional", "Recuperando por Seção Jurisdicional do Processo"));

		if (SecurityUtils.getSubject().isPermitted(Permissoes.RELATORIO_JUMBO)) {
			lista.add(new Relatorio("relatorio/recuperandos/jumbo", "Lista de Recuperandos Ativos nas Unidades com Jumbo"));
		}

		return lista;
	}
}
