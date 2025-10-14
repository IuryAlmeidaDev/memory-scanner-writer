package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.PlantaoServidorDAO;
import br.gov.mt.sejudh.sigepen.dao.PlantaoVisitaEstruturaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Stateless
public class PlantaoVisitaEstruturaService extends BaseService<PlantaoVisitaEstrutura, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private PlantaoVisitaEstruturaDAO dao;

	@Inject
	private ParametroVisitaService parametroVisitaService;

	@Inject
	private EstruturaService estruturaService;

	@Inject
	public void setPlantaoVisitaEstruturaDAO(PlantaoVisitaEstruturaDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public List<PlantaoVisitaEstrutura> buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(Integer tipoEntrada, Plantao plantao){
		List<PlantaoVisitaEstrutura> plantaoVisitaList = dao.buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(tipoEntrada, plantao.getUnidadePrisional().getId(), plantao.getDataPlantao());

		if(!plantaoVisitaList.isEmpty() && tipoEntrada != null && plantao.getUnidadePrisional() != null){

			ParametroVisita parametroVisita = new ParametroVisita();

			parametroVisita.setTipoEntrada(TipoEntrada.getValue(tipoEntrada));
			parametroVisita.setUnidade(plantao.getUnidadePrisional());

			setDiaSenamaParametroVisita(plantao.getDataPlantao(),parametroVisita);

			setHoraInicoEhoraFimVisita(plantaoVisitaList,parametroVisita);

		}

		return plantaoVisitaList;
	}

	private void setHoraInicoEhoraFimVisita(List<PlantaoVisitaEstrutura> plantaoVisitaList, ParametroVisita parametroVisita){
		List<ParametroVisita> parametroVisitaList = this.parametroVisitaService.buscarParametrosVisitaPorUnidadeAndTipoEntrada(parametroVisita);
		for(PlantaoVisitaEstrutura pve : plantaoVisitaList){
			for(ParametroVisita pv : parametroVisitaList){
				for(ParametroVisitaEstrutura pave : pv.getEstruturas()){

					Estrutura estrutura = new Estrutura();
					estrutura.setId(pve.getEstruturaId());

					List<Long> estruturaList = buscarIdsEstrutura(pave.getEstrutura());

					String horaInicio = pv.getHoraEntrada();
					String horaFim = pv.getHoraSaida();
					String horaFormatadaInicio = "";
					String horaFormatadaFim = "";
					if(estruturaList.contains(pve.getEstruturaId())){

						horaFormatadaInicio = horaInicio.substring(0, 2) + ":" + horaInicio.substring(2);
						horaFormatadaFim = horaFim.substring(0, 2) + ":" + horaFim.substring(2);

						pve.setHoraInicio(horaFormatadaInicio);
						pve.setHoraFim(horaFormatadaFim);

					}else if(estruturaList.isEmpty() && pve.getEstruturaId().equals(pave.getEstrutura().getId())){

						horaFormatadaInicio = horaInicio.substring(0, 2) + ":" + horaInicio.substring(2);
						horaFormatadaFim = horaFim.substring(0, 2) + ":" + horaFim.substring(2);

						pve.setHoraInicio(horaFormatadaInicio);
						pve.setHoraFim(horaFormatadaFim);
					}
				}
			}

		}

	}

	private List<Long> buscarIdsEstrutura(Estrutura estrutura){

		List<Long> idsEstrutura = new ArrayList<>();
		List<Estrutura> estuturas = new ArrayList<>();

		this.estruturaService.buscarEstruturasFilhos(estrutura,estuturas);

		estuturas.forEach( e -> idsEstrutura.add(e.getId()));

		return idsEstrutura;
	}

	public void setDiaSenamaParametroVisita(Date data,ParametroVisita parametroVisita){
		String diaSenama = this.obterDiaDaSemana(data);
		switch (diaSenama){
			case "DOMINGO":
				parametroVisita.setDomingo(true);
				break;
			case "SEGUNDA":
				parametroVisita.setSegunda(true);
				break;
			case "TERCA":
				parametroVisita.setTerca(true);
				break;
			case "QUARTA":
				parametroVisita.setQuarta(true);
				break;
			case "QUINTA":
				parametroVisita.setQuinta(true);
				break;
			case "SEXTA":
				parametroVisita.setSexta(true);
				break;
			case "SABADO":
				parametroVisita.setSabado(true);
				break;
		}

	}

	public String obterDiaDaSemana(Date data) {

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("pt", "BR"));
		String dia = sdf.format(data);
		dia = dia.replace("-feira", "").toUpperCase();

		return dia;
	}

}
