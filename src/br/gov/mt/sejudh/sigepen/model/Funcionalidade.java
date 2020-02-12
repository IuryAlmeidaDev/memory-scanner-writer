package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "FUNCIONALIDADE", schema = Sigepen.SEJUDH_SCHEMA)
public class Funcionalidade implements Serializable {

	private static final long serialVersionUID = 7170072825778386548L;

	@Id
    @Column(name = "ID")
    private long id;
    
    @Column(name = "NOME", length=50, nullable = false)
    private String nome;
    
    @OneToMany(mappedBy = "funcionalidade")
    private List<Permissao> permissoes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Sigepen.formatarEspacos(nome);
    }
    
	public List<Permissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissao> permissoes) {
		this.permissoes = permissoes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Funcionalidade other = (Funcionalidade) obj;
		if (id != other.id)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}

