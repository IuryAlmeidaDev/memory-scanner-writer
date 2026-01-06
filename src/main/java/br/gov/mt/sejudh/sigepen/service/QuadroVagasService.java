package br.gov.mt.sejudh.sigepen.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.QuadroVagasDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;

@Stateless
public class QuadroVagasService {

	@Inject
	private QuadroVagasDAO dao;

	@Inject
	private AuditoriaDAO auditDAO;

	public UnidadePrisional buscaPorId(Long id) {
		UnidadePrisional u = dao.buscaPorId(id);

		if (u != null) {
			u.setQuantidadeAtual(quantidadeAtual(id));
			u.setAutorRegistro(auditDAO.autorRegistro(u, u.getId()));
			u.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(u, u.getId()));
		}

		return u;
	}

	public List<QuadroVagasDTO> buscarPorFiltro(FiltroUnidadePrisional filtro, boolean isRelatorio) {
		return dao.buscaPorFiltro(filtro, isRelatorio);
	}

	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroQuadroVagas filtroQuadroVagas, boolean relatorio) {

		List<QuadroVagasDTO> lista = dao.buscaQuantidadeRecuperados(filtroQuadroVagas, relatorio);

		Map<Long, Long> capacidadeMap = dao.buscaCapacidadeTotalUnidade().stream()
				.collect(Collectors.toMap(QuadroVagasDTO::getId, QuadroVagasDTO::getCapacidadeTotal));

		for (QuadroVagasDTO dto : lista) {
			long capacidade = capacidadeMap.getOrDefault(dto.getId(), 0L);
			long total = dto.getTotalRecuperando() != null ? dto.getTotalRecuperando() : 0L;

			dto.setCapacidadeTotal(capacidade);
			dto.setVagas(Math.max(0, capacidade - total));
			dto.setDeficit(Math.max(0, total - capacidade));
		}

		ordenarLista(lista, filtroQuadroVagas);

		return lista;
	}

	private void ordenarLista(List<QuadroVagasDTO> lista, FiltroQuadroVagas filtro) {

		if (filtro == null || filtro.getOrdenacao() == null)
			return;

		Comparator<QuadroVagasDTO> comp;

		switch (filtro.getOrdenacao().getPropertyName()) {

		case "deficit":
			comp = Comparator.comparingLong(dto -> dto.getDeficit() != null ? dto.getDeficit() : 0L);
			break;

		case "vagas":
			comp = Comparator.comparingLong(QuadroVagasDTO::getVagas);
			break;

		case "capacidadeTotal":
			comp = Comparator.comparingLong(QuadroVagasDTO::getCapacidadeTotal);
			break;

		default:
			return;
		}

		if (!filtro.isAscendente()) {
			comp = comp.reversed();
		}

		lista.sort(comp);
	}

	public int quantidadeFiltrados(FiltroQuadroVagas filtro) {
		return dao.quantidadeFiltrados(filtro).intValue();
	}

	public Long quantidadeAtual(Long id) {
		return dao.quantidadeAtual(id);
	}
	
	public List<QuadroVagasDTO> carregarQuadroVgas(FiltroQuadroVagas filtro) {
		return dao.buscarQuadroVagas(filtro);
	}

}
