package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NSubject;
import com.neurotec.io.NBuffer;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKCliente;
import br.gov.mt.sejudh.sigepen.dao.PessoaDAO;
import br.gov.mt.sejudh.sigepen.model.Pessoa;

@Stateless
public class NServerService {

	@Inject
	private BiometriaSDKCliente cliente;
	
	@Inject
	private PessoaDAO pessoaDao;
	
	public NBiometricStatus excluirTemplate(Long pessoaId) throws Exception {
		validarPessoaId(pessoaId);
		
		return cliente.delete(pessoaId.toString());
	}
	
	public NBiometricStatus importarTemplate(long pessoaId) throws Exception {
		NSubject sub = new NSubject();
		
		try {
			validarPessoaId(pessoaId);
			
			Pessoa pessoa = pessoaDao.buscarPessoaComBiometriaPorId(pessoaId);
			
			if (pessoa == null) throw new Exception("Pessoa não encontrada!");
			if (pessoa.getBiometria() == null) throw new Exception("Pessoa informada sem biometria!");
			
			sub.setTemplateBuffer(NBuffer.fromArray(pessoa.getBiometria().getTemplate()));
			sub.setId(String.valueOf(pessoa.getId()));

			return cliente.enroll(sub);
		} finally {
			sub.close();
			sub.dispose();
		}
	}
	
	public Boolean checkStatus() {
		return cliente.checkStatus();
	}
	
	private void validarPessoaId(Long pessoaId) throws Exception {
		if (pessoaId == null || pessoaId <= 0L) {
			throw new Exception("Preencha o campo ID corretamente!");
		}
	}
}
