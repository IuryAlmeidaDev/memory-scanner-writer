package br.gov.mt.sejudh.sigepen.biometrics;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class BiometriaDedoPosicaoImagem {
	int posicao;
	String imagem;
	boolean isValidando;
	
	public BiometriaDedoPosicaoImagem(int posicao, String imagem, boolean isValidando) {
		super();
		this.posicao = posicao;
		this.imagem = imagem;
		this.isValidando = isValidando;
	}

	public int getPosicao() {
		return posicao;
	}
	
	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}
	
	public byte[] getImagem() {
		return Sigepen.decodeStringToByte(imagem);
	}
	
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public boolean isValidando() {
		return isValidando;
	}

	public void setValidando(boolean isValidando) {
		this.isValidando = isValidando;
	}
}

