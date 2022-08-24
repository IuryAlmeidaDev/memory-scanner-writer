package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoriaRegistro implements Serializable {

	private static final long serialVersionUID = -8783260402451055006L;

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


