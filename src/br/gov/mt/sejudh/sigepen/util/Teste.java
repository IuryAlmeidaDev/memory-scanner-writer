package br.gov.mt.sejudh.sigepen.util;

import java.io.IOException;

import com.neurotec.licensing.NLicense;
import com.neurotec.licensing.NLicenseManager;
import com.neurotec.licensing.NLicensing;
import com.neurotec.licensing.NLicensingService;

public class Teste {

	public static void main(String[] args) {
		try {
			//NLicense.obtainComponents("/local", 5000, "Biometrics.FingerExtraction,Biometrics.FingerMatching,Images.WSQ");
			
			if (!NLicense.obtain("/local", 5000, "FingerClient")) {
				System.out.println("Nao pegou licença");
			}
			
			if (!NLicense.obtain("/local", 5000, "FingerMatcher")) {
				System.out.println("Nao pegou licença");
			}
			
			NLicense.release("FingerClient");
			
			NLicense.release("FingerMatcher");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
