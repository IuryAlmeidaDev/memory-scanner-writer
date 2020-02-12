package br.gov.mt.sejudh.sigepen.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoriaRegistro {

	private Date data;
	
	private String usuario;

	public AutoriaRegistro() {
	}

	public String getData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dateFormat.format(data);
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	
}


