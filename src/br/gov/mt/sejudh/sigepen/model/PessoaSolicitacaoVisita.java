package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.util.ModelUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_PESSOA_SOLICITACAO_VISITA", schema = Sigepen.SEJUDH_SCHEMA)
public class PessoaSolicitacaoVisita implements Serializable, PersistentAttributeInterceptable {

    private static final long serialVersionUID = -2036826051154304793L;

    @SequenceGenerator(name = "SEQ_SGP_PESSOA_SOLICITACAO_VISITA_GENERATOR", sequenceName = "SEQ_SGP_PESSOA_SOLICITACAO_VISITA", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SGP_PESSOA_SOLICITACAO_VISITA_GENERATOR")
    @Column(name = "ID")
    private long id;

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
    
    @OneToOne(mappedBy = "pessoa", fetch=FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private Recuperando recuperando;
    
    @Column(name = "CPF", length = 14)
    private String cpf;
    
    @Transient
    private AutoriaRegistro autorRegistro;

    @Transient
    private AutoriaRegistro autorUltimaAlteracao;

    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;
    
    public PessoaSolicitacaoVisita() {
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
        this.nome = Sigepen.formatarEspacos(nome);
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = Sigepen.formatarEspacos(nomeSocial);
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = Sigepen.formatarEspacos(nomePai);
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = Sigepen.formatarEspacos(nomeMae);
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
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
        PessoaSolicitacaoVisita other = (PessoaSolicitacaoVisita) obj;
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
        return "PessoaSolicitacaoVisita [id=" + id + ", nome=" + nome + "]";
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

    public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
    
}
