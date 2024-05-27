package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKCliente;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class AgendadorTarefas {
	private static final Log logger = LogFactory.getLog(AgendadorTarefas.class);


	@Inject
	private PedidoService pedidoService;

	@Inject
	private VisitanteService visitanteService;

	@Inject
	private ImpedimentoService impedimentoService;

	@Inject
	private RecuperandoService recuperandoService;

	@Schedule(hour = "5", minute = "30", persistent = false)
	public void expirarPedidosTransito(){
		try {
			logger.info("***** INICIADA EXPIRAR PEDIDO TRANSITO *****");
			pedidoService.expirarPedidosTransito();
			logger.info("***** FINALIZADA EXPIRAR PEDIDO TRANSITO *****");
		}catch (Exception e){
			logger.error("***** ERRO AO EXPIRAR PEDIDO TRANSITO: "+ e.getMessage() + " *****");
		}
	}

	//@Schedule(minute = "*/15", hour ="*", persistent = false)
	public void renovarLicencaBiometria(){
		try {
			logger.info("***** INICIADA RENOVAR LICENÇA BIOMETRIA *****");
			BiometriaSDKCliente.validarLicencas();
			logger.info("***** FINALIZADA RENOVAR LICENÇA BIOMETRIA *****");
		} catch (Exception e) {
			logger.error("***** ERRO AO VALIDAR LICENÇA: "+ e.getMessage() + " *****");
		}
	}

	@Schedule(hour = "6", minute = "0", persistent = false)
	public void inativarVisitantes(){
		try {
			logger.info("***** INICIADA INATIVAR VISITANTES *****");
			visitanteService.inativarVisitantes();
			logger.info("***** FINALIZADA INATIVAR VISITANTES *****");
		}catch (Exception e){
			logger.error("***** ERRO AO INATIVAR VISITANTES: "+ e.getMessage() + " *****");
		}
	}

	@Schedule(hour = "5", minute = "0", persistent = false)
	public void inativarImpedimentos() throws Exception{
		try {
			logger.info("***** INICIADA INATIVAR IMPEDIMENTOS *****");
			impedimentoService.inativarImpedimentosVencidos();
			logger.info("***** FINALIZADA INATIVAR IMPEDIMENTOS *****");
		}catch (Exception e){
			logger.error("***** ERRO AO INATIVAR IMPEDIMENTOS: "+ e.getMessage() + " *****");
		}
	}

	@Schedule(hour = "4", minute = "30", persistent = false)
	public void expirarGestante(){
		try {
			logger.info("***** INICIADA EXPIRAR GESTANTE *****");
			recuperandoService.expirarGestante();
			logger.info("***** FINALIZADA EXPIRAR GESTANTE *****");
		}catch (Exception e){
			logger.error("***** ERRO AO EXPIRAR GESTANTE: "+ e.getMessage() + " *****");
		}
	}

	@Schedule(hour = "4", minute = "0", persistent = false)
	public void expirarLactante(){
		try {
			logger.info("***** INICIADA EXPIRAR LACTANTE *****");
			recuperandoService.expirarLactante();
			logger.info("***** FINALIZADA EXPIRAR LACTANTE *****");
		}catch (Exception e){
			logger.error("***** ERRO AO EXPIRAR LACTANTE: "+ e.getMessage() + " *****");
		}
	}


}
