package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKCliente;

@Stateless
public class AgendadorTarefas {
	
	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private VisitanteService visitanteService;
	
	@Inject
	private ImpedimentoService impedimentoService;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	@Schedule(hour = "6", minute = "30")
	public void expirarPedidosTransito(){
		 pedidoService.expirarPedidosTransito();
	}
	
	@Schedule(minute = "*/15", hour ="*", persistent = false)
	public void renovarLicencaBiometria(){
		try {
			BiometriaSDKCliente.validarLicencas();
		} catch (Exception e) {
			System.out.println("Erro ao validar licença: " + e.getMessage());
		}
	}

	@Schedule(hour ="0", minute = "5", persistent = true)
	public void inativarVisitantes(){
		visitanteService.inativarVisitantes();
	}
	
	@Schedule(hour ="0", minute = "10", persistent = true)
	public void inativarImpedimentos(){
		impedimentoService.inativarImpedimentosVencidos();
	}
	
	@Schedule(hour = "0", minute = "30")
	public void expirarGestante(){
		 recuperandoService.expirarGestante();
	}
	
	@Schedule(hour = "0", minute = "35")
	public void expirarLactante(){
		 recuperandoService.expirarLactante();
	}
	
}	
