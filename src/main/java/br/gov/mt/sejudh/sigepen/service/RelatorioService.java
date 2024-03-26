package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.RelatorioDAO;
import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.auxiliar.Relatorio;

@Stateless
public class RelatorioService {

	@Inject
	private RelatorioDAO dao;
	
	@Inject
	private ComboService comboService;
	
	public List<Funcionalidade> listarFuncionalidadesComRelatorio() {
		List<Funcionalidade> funcionalidades = comboService.listarFuncionalidades();
		List<Long> funcsComRelatorio = funcionalidadesComRelatorio();
		List<Funcionalidade> f = new ArrayList<Funcionalidade>();
		
		funcsComRelatorio.forEach(i -> {
			Funcionalidade func = funcionalidades.stream().filter(fu -> fu.getId() == i).findFirst().get();
			f.add(func);
		});

		return f;
	}
	
	public List<Long> funcionalidadesComRelatorio() {
		/* Funcionalidade - ID
		 * Recuperando - 3
		 */
		
		return Arrays.asList(3L);
	}

	public List<Relatorio> buscarPorFuncionalidade(Funcionalidade funcionalidade) {
		return this.dao.buscarPorFuncionalidade(funcionalidade);
	}
	
}
