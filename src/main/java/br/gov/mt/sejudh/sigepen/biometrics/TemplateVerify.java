package br.gov.mt.sejudh.sigepen.biometrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NMatchingSpeed;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.io.NBuffer;
import com.neurotec.licensing.NLicense;

public class TemplateVerify {
	public static void main(String[] args) throws IOException {
		String components = "Biometrics.FingerExtraction,Biometrics.FingerMatching,Images.WSQ";
		
		NBiometricClient biometricClient = null;
		NSubject referenceSubject = null;
		NSubject candidateSubject = null;
		
		if (!NLicense.obtainComponents("/local", 5000, components)) {
			System.out.println("Não foi possível obter licença.");
		}

		biometricClient = new NBiometricClient();
		biometricClient.setUseDeviceManager(false);
		biometricClient.setFingersQualityThreshold((byte) 48);
		biometricClient.setFingersFastExtraction(false);
		biometricClient.setFingersMatchingSpeed(NMatchingSpeed.MEDIUM);
		
		System.out.println(System.getProperty("user.dir"));
		
		File f1 = new File("1798");
		File f2 = new File("1799");
		
		referenceSubject = criarSubjectDeTemplate(Files.readAllBytes(f1.toPath()));
		candidateSubject = criarSubjectDeTemplate(Files.readAllBytes(f2.toPath()));
				
		NBiometricStatus status = biometricClient.verify(referenceSubject, candidateSubject);

		biometricClient.close();
		
		if (status == NBiometricStatus.OK || status == NBiometricStatus.MATCH_NOT_FOUND) {
			int score = referenceSubject.getMatchingResults().get(0).getScore();
			System.out.format("image scored %d, verification.. ", score);
			if (status == NBiometricStatus.OK) {
				System.out.println("succeeded");
			} else {
				System.out.println("failed");
			}
		} else {
			System.out.format("Verification failed. Status: %s", status);
			System.exit(-1);
		}
	}
	
	static NSubject criarSubjectDeTemplate(byte[] template) {
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
}
