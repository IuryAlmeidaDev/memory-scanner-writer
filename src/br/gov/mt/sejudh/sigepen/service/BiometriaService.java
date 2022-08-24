package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.neurotec.biometrics.NBiometricStatus;

import br.gov.mt.sejudh.sigepen.bean.outros.BiometriaUtilBean;
import br.gov.mt.sejudh.sigepen.biometrics.BiometriaDAO;
import br.gov.mt.sejudh.sigepen.biometrics.BiometriaDedoPosicaoImagem;
import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKDao;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.BiometriaTemporaria;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaBiometriaWSDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class BiometriaService {
	
	@Inject
	private BiometriaDAO dao;
	
	@Inject
	private BiometriaSDKDao bioDao;

	public NBiometricStatus excluirBiometriaSDK(Biometria biometria) {
		return bioDao.excluir(biometria);
	}
	
	private Boolean alterarBiometriaSDK(Biometria biometria) throws Exception {
		return bioDao.alterar(biometria);
	}
	
	public Boolean salvarBiometriaSDK(Biometria biometria) throws Exception {
		NBiometricStatus status = bioDao.salvar(biometria);
		return (status.equals(NBiometricStatus.OK));
	}
	
	public Biometria buscarBiometriaConvertida(long id) throws Exception {
		BiometriaTemporaria bioTemp = dao.buscarBiometriaTemporaria(id);
		
		if (bioTemp == null) {
			throw new Exception("Não foi possível converter biometria.");
		}
		
		Biometria bio = converterBiometriaTemporariaEmBiometria(bioTemp);
		
		if (bio == null) {
			throw new Exception("Não foi possível converter biometria.");
		}
		
		dao.removerBiometriaTemporaria(id);
		
		return bio;
	}
	
	public BiometriaTemporaria salvarBiometriaTemporaria(String json) {
		BiometriaTemporaria bioTemp = new BiometriaTemporaria();
		
		try {
			bioTemp = criarBiometriaTemporariaFromJson(json);
			bioTemp = dao.salvarBiometriaTemporaria(bioTemp);
		} catch (Throwable t) {
			throw t;
		}
		
		return bioTemp;
	}
	
	public List<Long> pesquisarBiometriaPorTemplateByte(byte[] template) throws Exception {
		return bioDao.pesquisarPorTemplate(template);
	}
	
	public List<Long> pesquisarBiometriaPorTemplateString(String template) throws Exception {
		return pesquisarBiometriaPorTemplateByte(Sigepen.decodeStringToByte(template));
	}

	public Long pesquisaPorTemplateParaPessoaWS(String template) throws Exception {
		return bioDao.pesquisaPorTemplateParaPessoaWS(Sigepen.decodeStringToByte(template));
	}
	
	public Biometria converterBiometriaColetadaAppEmBiometriaObjeto(String biometriaAppJson) {
		BiometriaTemporaria bioTemp = criarBiometriaTemporariaFromJson(biometriaAppJson);
		return converterBiometriaTemporariaEmBiometria(bioTemp);
	}
	
	private BiometriaTemporaria criarBiometriaTemporariaFromJson(String json) {
		BiometriaTemporaria bioT = new BiometriaTemporaria();
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(json, true);
		bioT.setTemplate(bioUtil.getBiometriaTemplate());
		bioT.setJson(bioUtil.getBiometriaJsonString());
		
		return bioT;
	}
	
	private Biometria converterBiometriaTemporariaEmBiometria(BiometriaTemporaria bioTemp) {
		Biometria bio = new Biometria();
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(bioTemp.getJson(), true);
		bio.setTemplate(bioUtil.getBiometriaTemplate());
		bio.setDataCaptura(Sigepen.getDataAtual());
		
		bioUtil.getBiometriaFromJson(bio);

		return bio;
	}

	public void vincularBiometriaAPessoa(Biometria bio, Pessoa pessoa) {
		bio.setIdPessoa(pessoa.getId());
		bio.setBiometriaFoiAlterada(true);
		pessoa.setBiometria(bio);
	}
	
	public void salvarBiometriaNServer(Pessoa p) throws Exception {
    	if (p.getBiometria() == null || p.getBiometria().getTemplate() == null)
    		return;
    	
    	if (p.getBiometria().getBiometriaFoiAlterada() != null && p.getBiometria().getBiometriaFoiAlterada()) {    	
			Biometria bio = p.getBiometria();
			bio.setIdPessoa(p.getId());
	
			alterarBiometriaSDK(bio);
    	}
	}

	public Boolean verificarSeAmostrasSaoIdenticasPorImagem(String listaAmostras) throws Exception {
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(listaAmostras, false);
		
		ArrayList<byte[]> amostras = bioUtil.getAmostrasFromJson();

		if (amostras.size() <= 0)
			return false;

		for (int i=0; i<amostras.size()-1; i++) {
			if (!bioDao.verificarPorImagem(amostras.get(i), amostras.get(i+1))) {
				return false;
			}
		}
		
		return true;
	}
	
	public Boolean verificarSeAmostrasSaoIdenticasPorTemplate(String listaAmostras) throws Exception {
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(listaAmostras, false);
		
		ArrayList<byte[]> amostras = bioUtil.getAmostrasFromJson();

		if (amostras.size() <= 0)
			return false;

		for (int i=0; i<amostras.size()-1; i++) {
			if (!bioDao.verificarPorTemplate(amostras.get(i), amostras.get(i+1))) {
				return false;
			}
		}
		
		return true;
	}

	public String verificarSeExistemDedosRepetidosPorImagem(String listaImagensDedos) throws Exception {
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(listaImagensDedos, false);
		
		ArrayList<BiometriaDedoPosicaoImagem> dedos = bioUtil.getDedosListFromJson();

		BiometriaDedoPosicaoImagem biometria = dedos.stream().filter(x -> x.isValidando()).findFirst().get();
		List<BiometriaDedoPosicaoImagem> listaBiometria = dedos.stream().filter(x -> x.isValidando() == false).collect(Collectors.toList());
		
		for (BiometriaDedoPosicaoImagem biometriaAux : listaBiometria) {
			if (bioDao.verificarPorImagem(biometria.getImagem(), biometriaAux.getImagem())) {
				return "[" + biometria.getPosicao() + ", " + biometriaAux.getPosicao() + "]";
			}
		}

		return null;
	}
	
	public String verificarSeExistemDedosRepetidosPorTemplate(String listaTemplatesDedos) throws Exception {
		BiometriaUtilBean bioUtil = new BiometriaUtilBean();
		
		bioUtil.setBioJsonString(listaTemplatesDedos, false);
		
		ArrayList<BiometriaDedoPosicaoImagem> dedos = bioUtil.getDedosListFromJson();

		BiometriaDedoPosicaoImagem biometria = dedos.stream().filter(x -> x.isValidando()).findFirst().get();
		List<BiometriaDedoPosicaoImagem> listaBiometria = dedos.stream().filter(x -> x.isValidando() == false).collect(Collectors.toList());
		
		for (BiometriaDedoPosicaoImagem biometriaAux : listaBiometria) {
			if (bioDao.verificarPorTemplate(biometria.getImagem(), biometriaAux.getImagem())) {
				return "[" + biometria.getPosicao() + ", " + biometriaAux.getPosicao() + "]";
			}
		}

		return null;
	}

	public String buscarVersaoAtualAplicativo() {
		return dao.buscarVersaoAtualAplicativo();
	}

	public PessoaBiometriaWSDTO buscarIdsVinculadosAPessoa(Long idPessoa) {
		return dao.buscarIdsVinculadosAPessoa(idPessoa);
	}
}
