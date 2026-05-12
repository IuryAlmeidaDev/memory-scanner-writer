package br.gov.mt.sejudh.sigepen.biometrics;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private static final Logger log = LogManager.getLogger(BiometriaSDKCliente.class);

	final static String components = "Biometrics.FingerExtraction,Biometrics.FingerMatching,Images.WSQ";

	private NBiometricClient biometricClient;

	@PostConstruct
	private void init() {
		try {
			validarLicencas();
			inicializar();
		} catch (Exception e) {
			// intentional: nao re-lanca pra app subir mesmo sem licenca; chamadas biometricas vao falhar com NPE explicito ate licenca voltar
			log.error("Falha ao inicializar BiometriaSDKCliente | host={} | port={}",
					Sigepen.IP_NSERVER, Sigepen.PORT_NSERVER, e);
			// TODO Fase 2 error_coverage: ElasticApm.captureException(e)
		}
	}

	public static void validarLicencas() throws IOException, Exception {
		if (!NLicense.obtainComponents(Sigepen.LICENSE_SERVER, Sigepen.LICENSE_PORT, components)) {
			log.error("Licenca Neurotec NAO obtida | server={} | port={} | components={}",
					Sigepen.LICENSE_SERVER, Sigepen.LICENSE_PORT, components);
			// TODO Fase 2 error_coverage: ElasticApm.captureException(...)
			throw new IllegalStateException(
					"Licenca Neurotec indisponivel - servidor de licencas nao respondeu");
		}
		log.info("Licenca Neurotec OK | server={} | port={} | components={}",
				Sigepen.LICENSE_SERVER, Sigepen.LICENSE_PORT, components);
	}

	private void inicializar() {
		biometricClient = new NBiometricClient();
		biometricClient.setUseDeviceManager(false);
		biometricClient.setFingersQualityThreshold((byte) 48);
		biometricClient.setFingersFastExtraction(false);
		biometricClient.setFingersMatchingSpeed(NMatchingSpeed.MEDIUM);
		
        NClusterBiometricConnection connection = new NClusterBiometricConnection();
        connection.setHost(Sigepen.IP_NSERVER);
        connection.setPort(Sigepen.PORT_NSERVER);
        
        biometricClient.getRemoteConnections().add(connection);
        
		/*NOdbcBiometricConnection connection = new NOdbcBiometricConnection();
		connection.setConnectionString("DSN=DESENVOLVIMENTO;UID=sigepen_user_des;PWD=sigependes;");
		connection.setTableName(Sigepen.SEJUDH_SCHEMA + ".USERTBL");
		connection.setSubjectIdColumn("DBID");
		connection.setTemplateColumn("TEMPLATE");
		connection.setDeleteQuery("delete from " + Sigepen.SEJUDH_SCHEMA + ".USERTBL where DBID = ?");
		connection.setEnrollQuery("insert into " + Sigepen.SEJUDH_SCHEMA + ".USERTBL (DBID, TEMPLATE) values (@DBID@, @TEMPLATE@)");
		connection.setSelectSubjectQuery("select DBID, TEMPLATE from " + Sigepen.SEJUDH_SCHEMA + ".USERTBL where DBID = ?");
		connection.setSelectAllQuery("select DBID, TEMPLATE from " + Sigepen.SEJUDH_SCHEMA + ".USERTBL");

		biometricClient.setDatabaseConnection(connection);*/
	}
	
	public Long checkStatus() {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Long> future = (Future<Long>) executor.submit(() -> {
			return Long.parseLong(String.valueOf(biometricClient.getCount()));
		});
		
		try {
			return future.get(1000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException ex) {
			return -1L;
		} catch (Exception e) {
			return -1L;
		} finally {
		   future.cancel(true);
		}
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
}
