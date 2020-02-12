package br.gov.mt.sejudh.sigepen.biometrics;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NMatchingSpeed;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.biometrics.client.NClusterBiometricConnection;
import com.neurotec.licensing.NLicense;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ApplicationScoped
public class BiometriaSDKCliente implements Serializable {
	private static final long serialVersionUID = 1L;

	final static String components = "Biometrics.FingerExtraction,Biometrics.FingerMatching,Images.WSQ";
	
	private NBiometricClient biometricClient;
	
	@PostConstruct
	private void init() {
		try {
			validarLicencas();
			inicializar();
		} catch (Exception e) {
			
		}
	}

	public static void validarLicencas() throws IOException, Exception {
		if (!NLicense.obtainComponents("/local", 5000, components)) {
			System.out.println("Não foi possível obter licença.");
		} else {
			System.out.println("Licença verificada.");
		}
	}

	private void inicializar() {
		biometricClient = new NBiometricClient();
		biometricClient.setUseDeviceManager(false);
		biometricClient.setFingersQualityThreshold((byte) 48);
		biometricClient.setFingersFastExtraction(false);
		biometricClient.setFingersMatchingSpeed(NMatchingSpeed.LOW);
		
		NClusterBiometricConnection connection = new NClusterBiometricConnection();
		connection.setHost(Sigepen.IP_NSERVER);
		connection.setPort(Sigepen.PORT_NSERVER);
		
		biometricClient.getRemoteConnections().add(connection);		
	}

	public NBiometricStatus identify(NSubject subject) {
		return this.biometricClient.identify(subject);
	}

	public NBiometricStatus enroll(NSubject subject) {
		return this.biometricClient.enroll(subject);
	}

	public NBiometricStatus verify(NSubject subject1, NSubject subject2) {
		return this.biometricClient.verify(subject1, subject2);
	}

	public NBiometricStatus delete(String subjectId) {
		return this.biometricClient.delete(subjectId);
	}
	
	/*public NBiometricClient getClient() {
		if (biometricClient == null) {
			init();
		}
		
		return biometricClient;
	}*/
}
