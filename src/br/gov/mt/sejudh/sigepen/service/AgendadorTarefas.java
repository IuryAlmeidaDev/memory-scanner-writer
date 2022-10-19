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
	
	@Schedule(hour = "5", minute = "0", persistent = false)
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

	@Schedule(hour = "6", minute = "0", persistent = false)
	public void inativarVisitantes(){
		visitanteService.inativarVisitantes();
	}
	
	@Schedule(hour = "5", minute = "0", persistent = false)
	public void inativarImpedimentos() throws Exception{
		impedimentoService.inativarImpedimentosVencidos();
	}
	
	@Schedule(hour = "5", minute = "0", persistent = false)
	public void expirarGestante(){
		 recuperandoService.expirarGestante();
	}
	
	@Schedule(hour = "5", minute = "0", persistent = false)
	public void expirarLactante(){
		 recuperandoService.expirarLactante();
	}
	
}	
