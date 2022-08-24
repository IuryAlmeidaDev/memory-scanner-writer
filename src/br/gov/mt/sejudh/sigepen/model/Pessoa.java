package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.util.ModelUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PESSOA", schema = Sigepen.SEJUDH_SCHEMA)
public class Pessoa implements Serializable, PersistentAttributeInterceptable {

    private static final long serialVersionUID = -2036826051154304793L;

    @SequenceGenerator(name = "SEQ_PESSOA_GENERATOR", sequenceName = "SEQ_PESSOA", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PESSOA_GENERATOR")
    @Column(name = "ID")
    private long id;

    @Transient
	private long versao;

    @Column(name = "NOME", length = 80, nullable = false)
    private String nome;

    @Column(name = "NOME_SOCIAL", length = 80)
    private String nomeSocial;

    @Column(name = "NOME_MAE", length = 80, nullable = false)
    private String nomeMae;

    @Column(name = "NOME_PAI", length = 80)
    private String nomePai;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", length = 1)
    private Sexo sexo;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="NACIONALIDADE_ID")
    private Nacionalidade nacionalidade;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PAIS_ID")
    private Pais pais;

    @Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CIDADE_ID")
    private Cidade naturalidade;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="pessoa", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Documento> documentos;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="pessoa", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Endereco> enderecos;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="pessoa", fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Contato> contatos;

    @OneToOne(mappedBy = "pessoa", fetch=FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private Recuperando recuperando;
    
    @OneToOne(mappedBy = "pessoa", fetch=FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private Visitante visitante;

    @OneToOne(mappedBy = "pessoa", fetch=FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private VwVisitante vwVisitante;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    @JoinColumn(name="BIOMETRIA_ID")
    private Biometria biometria;

    @Transient
    private AutoriaRegistro autorRegistro;

    @Transient
    private AutoriaRegistro autorUltimaAlteracao;

    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

    @Column(name = "NOME_FONETIZADO", length = 200)
    private String nomeFonetizado;
    
    @Column(name = "NOME_FONETIZADO_MAE", length = 200)
    private String nomeFonetizadoMae;

    @Transient
    private String cpf;
    
    public Pessoa() {
        this.documentos = new ArrayList<Documento>();
        this.enderecos = new ArrayList<Endereco>();
    }

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
        this.nome = Sigepen.removerAcentuacao(Sigepen.formatarEspacos(nome));
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = Sigepen.removerAcentuacao(Sigepen.formatarEspacos(nomeSocial));
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = Sigepen.removerAcentuacao(Sigepen.formatarEspacos(nomePai));
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = Sigepen.removerAcentuacao(Sigepen.formatarEspacos(nomeMae));
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Cidade getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(Cidade naturalidade) {
        this.naturalidade = naturalidade;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    public List<Contato> getContatos() {
        return contatos;
    }

    public void setContatos(List<Contato> contatos) {
        this.contatos = contatos;
    }

    public AutoriaRegistro getAutorRegistro() {
        return autorRegistro;
    }

    public void setAutorRegistro(AutoriaRegistro autorRegistro) {
        this.autorRegistro = autorRegistro;
    }

    public AutoriaRegistro getAutorUltimaAlteracao() {
        return autorUltimaAlteracao;
    }

    public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
        this.autorUltimaAlteracao = autorUltimaAlteracao;
    }

    public Biometria getBiometria() {
        return biometria;
    }

    public void setBiometria(Biometria biometria) {
        this.biometria = biometria;
    }

    public Recuperando getRecuperando() {
        if (this.persistentAttributeInterceptor != null) {
            return (Recuperando) this.persistentAttributeInterceptor.readObject(this, "recuperando", recuperando);
        }

        return this.recuperando;
    }

    public void setRecuperando(Recuperando recuperando) {
        if (this.persistentAttributeInterceptor != null) {
            this.recuperando = (Recuperando) this.persistentAttributeInterceptor.writeObject(this, "recuperando", this.recuperando, recuperando);
        } else {
            this.recuperando = recuperando;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dataNascimento == null) ? 0 : dataNascimento.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((nomeMae == null) ? 0 : nomeMae.hashCode());
        result = prime * result + ((sexo == null) ? 0 : sexo.hashCode());
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
        Pessoa other = (Pessoa) obj;
        if (dataNascimento == null) {
            if (other.dataNascimento != null)
                return false;
        } else if (!dataNascimento.equals(other.dataNascimento))
            return false;
        if (id != other.id)
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (nomeMae == null) {
            if (other.nomeMae != null)
                return false;
        } else if (!nomeMae.equals(other.nomeMae))
            return false;
        if (sexo != other.sexo)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Pessoa [id=" + id + ", nome=" + nome + "]";
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }
    
    public Boolean getMenorIdade() {
    	return ModelUtil.isMenorDeIdade(this.dataNascimento);
	}
    
    public String getCPF() {
    	if(this.documentos.size() > 0) {
    		Optional<Documento> doc = this.documentos.stream().filter(d -> d.getTipoDocumento().getDescricao().equals("CPF")).findFirst();
    		if(doc.isPresent()) {
    			return formataCpf(doc.get().getNumero());
    		}else {
    			return null;
    		}
    	}
    	
    	return null;
    }
    
    private String formataCpf(String cpf) {
    	if(cpf.length() == 11) {
    		return cpf.replaceAll("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})", "$1.$2.$3-$4");
    	}
    	
    	return " - ";
    }

	public String getNomeFonetizado() {
		return nomeFonetizado;
	}

	public void setNomeFonetizado(String nomeFonetizado) {
		this.nomeFonetizado = nomeFonetizado;
	}

	public String getNomeFonetizadoMae() {
		return nomeFonetizadoMae;
	}

	public void setNomeFonetizadoMae(String nomeFonetizadoMae) {
		this.nomeFonetizadoMae = nomeFonetizadoMae;
	}

	public Visitante getVisitante() {
		return visitante;
	}

	public void setVisitante(Visitante visitante) {
		this.visitante = visitante;
	}

	public String getCpf() {
		return this.getCPF();
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public VwVisitante getVwVisitante() {
		return vwVisitante;
	}

	public void setVwVisitante(VwVisitante vwVisitante) {
		this.vwVisitante = vwVisitante;
	}
}
