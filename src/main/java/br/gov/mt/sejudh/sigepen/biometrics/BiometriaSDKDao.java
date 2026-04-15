package br.gov.mt.sejudh.sigepen.biometrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NFinger;
import com.neurotec.biometrics.NMatchingResult;
import com.neurotec.biometrics.NSubject;
import com.neurotec.io.NBuffer;

import br.gov.mt.sejudh.sigepen.model.Biometria;

@RequestScoped
public class BiometriaSDKDao {

	@Inject
	private BiometriaSDKCliente cliente;

	public long pesquisaPorTemplateParaPessoaWS(byte[] template) throws Exception {
		NSubject subject = new NSubject();
		long idPessoa = (long) 0;
		
		try {
			@SuppressWarnings("unused")
			NBiometricStatus statusOperacao = null;

			subject.setTemplateBuffer(NBuffer.fromArray(template));
			
			try {
				statusOperacao = cliente.identify(subject);
			} catch (Exception e) {
				e.printStackTrace();
				//throw new IOException("Servidor biométrico inoperante, entre em contato com o Suporte Sigepen");
			}

			if (subject.getMatchingResults().size() > 0) {
				NMatchingResult nMatchingResult = subject.getMatchingResults().get(0);
				idPessoa = Long.valueOf(nMatchingResult.getId());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (subject != null) {
				subject.close();
				subject.dispose();
			}
		}
		
		return idPessoa;
	}
	
	public List<Long> pesquisarPorTemplate(byte[] template) throws Exception {
		NSubject subject = new NSubject();
		List<Long> lista = new ArrayList<>();
		
		try {
			@SuppressWarnings("unused")
			NBiometricStatus statusOperacao = null;

			subject.setTemplateBuffer(NBuffer.fromArray(template));
			
			try {
				statusOperacao = cliente.identify(subject);
			} catch (RuntimeException e) {
				throw new RuntimeException("Servidor biométrico inoperante, entre em contato com o Suporte Sigepen", e);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (NMatchingResult matchingResult : subject.getMatchingResults()) {
				lista.add(Long.parseLong(matchingResult.getId()));
			}
			
			if (lista.size() <= 0) {
				lista.add(Long.parseLong("-1"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (subject != null) {
				subject.close();
				subject.dispose();
			}
		}
		
		return lista;
	}

	public NBiometricStatus salvar(Biometria biometria) throws Exception {
		NSubject subject = new NSubject();
		
		try {
			NBiometricStatus statusOperacao = null;
					
			subject.setTemplateBuffer(NBuffer.fromArray(biometria.getTemplate()));
			subject.setId(String.valueOf(biometria.getIdPessoa()));
		
			try {
				statusOperacao = cliente.enroll(subject);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Servidor biométrico inoperante, entre em contato com o Suporte Sigepen", e);
			}
			
			return statusOperacao;
		} catch (Exception e) {
			throw e;
		} finally {
			if (subject != null) {
				subject.close();
				subject.dispose();
			}
		}
	}
	
	public NBiometricStatus excluir(Biometria biometria) {
		return cliente.delete(String.valueOf(biometria.getIdPessoa()));
	}

	public Boolean alterar(Biometria biometria) throws Exception {
		try {			
			NBiometricStatus statusOperacao = excluir(biometria);
			
			if (statusOperacao == NBiometricStatus.OK || statusOperacao == NBiometricStatus.ID_NOT_FOUND) {
				statusOperacao = this.salvar(biometria);
			}
			
			switch (statusOperacao) {
				case COMMUNICATION_ERROR:
					throw new Exception("Servidor biométrico inoperante, entre em contato com o Suporte Sigepen");
	
				default:
					return (statusOperacao == NBiometricStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private NSubject criarSubjectDeImagem(byte[] imagem) {
		NBuffer buff = null;
		NFinger finger = null;
		NSubject sub = null;
		
		try {
			buff = new NBuffer(imagem);
			finger = new NFinger();
			sub = new NSubject();
			
			finger.setSampleBuffer(buff);
			sub.getFingers().add(finger);
			
			return sub;
		} catch (Exception e) {
			throw e;
		} finally {
			if (buff != null) {
				buff.close();
				buff.dispose();
			}
			
			if (finger != null) {
				finger.close();
				finger.dispose();
			}
		}
	}
	
	private NSubject criarSubjectDeTemplate(byte[] template) {
		NBuffer buff = null;
		NSubject sub = null;
		
		try {
			buff = new NBuffer(template);
			sub = new NSubject();
			sub.setTemplateBuffer(buff);
			
			return sub;
		} catch (Exception e) {
			throw e;
		} finally {
			if (buff != null) {
				buff.close();
				buff.dispose();
			}
		}
	}
	
	public Boolean verificarPorImagem(byte[] amostraImagem1, byte[] amostraImagem2) throws Exception {
		NSubject subject1 = null;
		NSubject subject2 = null;
		
		try {
			subject1 = criarSubjectDeImagem(amostraImagem1);
			subject2 = criarSubjectDeImagem(amostraImagem2);
			
			NBiometricStatus status = cliente.verify(subject1, subject2);
			
			return (status.equals(NBiometricStatus.OK));
		} catch (Exception e) {
			throw e;
		} finally {
			if (subject1 != null) {
				subject1.close();
				subject1.dispose();
			}
			
			if (subject2 != null) {
				subject2.close();
				subject2.dispose();
			}
		}
	}
	
	public Boolean verificarPorTemplate(byte[] template1, byte[] template2) throws Exception {
		NSubject subject1 = null;
		NSubject subject2 = null;
		
		try {
			subject1 = criarSubjectDeTemplate(template1);
			subject2 = criarSubjectDeTemplate(template2);
			
			NBiometricStatus status = cliente.verify(subject1, subject2);
			
			return (status.equals(NBiometricStatus.OK));
		} catch (Exception e) {
			throw e;
		} finally {
			if (subject1 != null) {
				subject1.close();
				subject1.dispose();
			}
			
			if (subject2 != null) {
				subject2.close();
				subject2.dispose();
			}
		}
	}
}
