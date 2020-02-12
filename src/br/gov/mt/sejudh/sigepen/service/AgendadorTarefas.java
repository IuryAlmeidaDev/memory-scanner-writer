package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKCliente;

@Stateless
public class AgendadorTarefas {
	
	@Inject
	private PedidoService pedidoService;
	
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
}	
