package br.gov.mt.sejudh.sigepen.model.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class GerarPedidoEstruturaDTO {

	private long id;
	private String nome;
	private boolean cela;
	private int capacidade;
	private SituacaoUnidade situacaoUnidade;
	private UnidadePrisional unidadePrisional;
	private GerarPedidoEstruturaDTO estruturaPai;
	private List<GerarPedidoEstruturaDTO> estruturas;
	private int capacidadeTotal;
	private int totalDietaPadrao;
	private int totalDietaEspecial;
	private boolean temEstruturaFilho;
	private Long dietaPadrao;
	private Long dietaEspecial;

	public GerarPedidoEstruturaDTO() {
		this.dietaPadrao = 0l;
		this.dietaEspecial = 0l;
	}

	public GerarPedidoEstruturaDTO(Object[] row) {
		this.id = ((BigDecimal) row[0]).longValue();
		this.capacidade = ((BigDecimal) row[1]).intValue();
		this.cela = ((BigDecimal) row[2]).longValue() == 1;
		this.nome = (String) row[3];
		this.dietaPadrao = this.cela ? ((BigDecimal) row[4]).longValue() : 0;
		this.dietaEspecial = this.cela ? ((BigDecimal) row[5]).longValue() : 0;
	}

	public GerarPedidoEstruturaDTO(long id, int capacidade, boolean cela, String nome, Long dietaPadrao,
			Long dietaEspecial) {
		this.id = id;
		this.capacidade = capacidade;
		this.cela = cela;
		this.nome = nome;
		this.dietaPadrao = dietaPadrao;
		this.dietaEspecial = dietaEspecial;

	}

	public GerarPedidoEstruturaDTO(String nome) {
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
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getNomeCompleto() {
		String nome = "";
		GerarPedidoEstruturaDTO est = this;
		while (est.getEstruturaPai() != null) {
			nome = est.getEstruturaPai().getNome() + " > " + nome;
			est = est.getEstruturaPai();
		}
		return nome + getNome();
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

	public GerarPedidoEstruturaDTO getEstruturaPai() {
		return estruturaPai;
	}

	public void setEstruturaPai(GerarPedidoEstruturaDTO estruturaPai) {
		this.estruturaPai = estruturaPai;
	}

	public List<GerarPedidoEstruturaDTO> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(List<GerarPedidoEstruturaDTO> estruturas) {
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
	
	public int getTotalDietaPadrao() {
		return this.totalDietaPadrao;
	}
	
	public void setTotalDietaPadrao(int totalDietaPadrao) {
		this.totalDietaPadrao = totalDietaPadrao;
	}
	
	public int getTotalDietaEspecial() {
		return this.totalDietaEspecial;
	}
	
	public void setTotalDietaEspecial(int totalDietaEspecial) {
		this.totalDietaEspecial = totalDietaEspecial;
	}

	public boolean isTemEstruturaFilho() {
		return temEstruturaFilho;
	}

	public void setTemEstruturaFilho(boolean temEstrturaFilho) {
		this.temEstruturaFilho = temEstrturaFilho;
	}

	public Long getDietaPadrao() {
		return this.dietaPadrao;
	}

	public Long setDietaPadrao(Long dietaPadrao) {
		return this.dietaPadrao;
	}

	public Long getDietaEspecial() {
		return this.dietaEspecial;
	}

	public Long setDietaEspecial(Long dietaEspecial) {
		return this.dietaEspecial;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacidade, cela, dietaEspecial, dietaPadrao, id, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GerarPedidoEstruturaDTO other = (GerarPedidoEstruturaDTO) obj;
		return capacidade == other.capacidade && cela == other.cela
				&& Objects.equals(dietaEspecial, other.dietaEspecial) && Objects.equals(dietaPadrao, other.dietaPadrao)
				&& id == other.id && Objects.equals(nome, other.nome);
	}

	@Override
	public String toString() {
		return "GerarPedidoEstruturaDTO [id=" + id + ", nome=" + nome + ", cela=" + cela + ", capacidade=" + capacidade
				+ ", dietaPadrao=" + dietaPadrao + ", dietaEspecial=" + dietaEspecial + "]";
	}

}
