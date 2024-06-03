package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Audited
@Entity
@Table(name = "ESTRUTURA", schema = Sigepen.SEJUDH_SCHEMA)
public class Estrutura implements Serializable, PersistentAttributeInterceptable {

    private static final long serialVersionUID = 9073895500689612495L;

    @SequenceGenerator(name = "SEQ_ESTRUTURA_GENERATOR", sequenceName = "SEQ_ESTRUTURA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ESTRUTURA_GENERATOR")
    @Column(name = "ID")
    private long id;

	@Transient
	private Long versao;

    @Column(name="NOME", length = 80, nullable = false)
    private String nome;

    @Transient
    private String nomeCompleto;

    @Column(name="CELA")
    private boolean cela;

    @Column(name="CAPACIDADE")
    private int capacidade;

    @Enumerated
    @Column(name = "SITUACAO")
    private SituacaoUnidade situacaoUnidade;

    @OneToMany(mappedBy = "estrutura", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<SituacaoEstruturaUnidade> historicoSituacaoEstrutura;

    @ManyToOne(fetch = FetchType.LAZY)
    private UnidadePrisional unidadePrisional;

    @ManyToOne(fetch = FetchType.LAZY)
    private Estrutura estruturaPai;

    @OneToMany(mappedBy="estruturaPai", fetch = FetchType.LAZY)
    private List<Estrutura> estruturas;

    @Transient
    private int capacidadeTotal;

    @Transient
    private boolean temEstruturaFilho;

    @OneToOne(mappedBy="estrutura", fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private EstruturaComplemento estruturaComplemento;

    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

    public Estrutura() {
        this.historicoSituacaoEstrutura = new ArrayList<SituacaoEstruturaUnidade>();
    }

    public Estrutura(String nome){
        this.nome = Sigepen.formatarEspacos(nome);
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
        this.nome =  Sigepen.formatarEspacos(nome);
    }

    public EstruturaComplemento getEstruturaComplemento() {
        if (this.persistentAttributeInterceptor != null) {
            return (EstruturaComplemento) this.persistentAttributeInterceptor.readObject(this, "estruturaComplemento", estruturaComplemento);
        }

        return estruturaComplemento;
    }

    public void setEstruturaComplemento(EstruturaComplemento estruturaComplemento) {
        if (this.persistentAttributeInterceptor != null) {
            this.estruturaComplemento = (EstruturaComplemento) this.persistentAttributeInterceptor.writeObject(this, "estruturaComplemento", this.estruturaComplemento, estruturaComplemento);
        } else {
            this.estruturaComplemento = estruturaComplemento;
        }
    }

    public String getNomeCompleto() {
        return estruturaComplemento.getNomeCompletoUnidade();
    }

    public boolean getCela() {
        return cela;
    }

    public void setCela(boolean cela) {
        this.cela = cela;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }


    public SituacaoUnidade getSituacaoUnidade() {
        return situacaoUnidade;
    }

    public void setSituacaoUnidade(SituacaoUnidade situacaoUnidade) {
        this.situacaoUnidade = situacaoUnidade;
    }

    public Estrutura getEstruturaPai() {
        return estruturaPai;
    }

    public void setEstruturaPai(Estrutura estruturaPai) {
        this.estruturaPai = estruturaPai;
    }

    public List<Estrutura> getEstruturas() {
        return estruturas;
    }

    public void setEstruturas(List<Estrutura> estruturas) {
        this.estruturas = estruturas;
    }

    public UnidadePrisional getUnidadePrisional() {
        return unidadePrisional;
    }

    public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
        this.unidadePrisional = unidadePrisional;
    }

    public int getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(int capacidaTotal) {
        this.capacidadeTotal = capacidaTotal;
    }

    public boolean isTemEstruturaFilho() {
        return temEstruturaFilho;
    }

    public void setTemEstruturaFilho(boolean temEstrturaFilho) {
        this.temEstruturaFilho = temEstrturaFilho;
    }

    public List<SituacaoEstruturaUnidade> getHistoricoSituacaoEstrutura() {
        return historicoSituacaoEstrutura;
    }

    public void setHistoricoSituacaoEstrutura(List<SituacaoEstruturaUnidade> historicoSituacaoEstrutura) {
        this.historicoSituacaoEstrutura = historicoSituacaoEstrutura;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((estruturaPai == null) ? 0 : estruturaPai.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime
                * result
                + ((unidadePrisional == null) ? 0 : unidadePrisional.hashCode());
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
        Estrutura other = (Estrutura) obj;
        if (estruturaPai == null) {
            if (other.estruturaPai != null)
                return false;
        } else if (!estruturaPai.equals(other.estruturaPai))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Estrutura [id=" + id + ", nome=" + nome + "]";
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }
}
