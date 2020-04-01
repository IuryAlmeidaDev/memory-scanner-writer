package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

public class ExtratoRecuperandoDTO {

	String nome;
	Long matricula;
	String nomeMae;
	Date dataNascimento;
	String regime;
	String condicao;
	String lotacao;
	
	List<PassagemExtratoDTO> passagens;
	List<HistoricoRecuperando> historicos;
	
	public ExtratoRecuperandoDTO(Recuperando r) {
		this.nome = r.getPessoa().getNome();
		this.matricula = r.getId();
		this.nomeMae = r.getPessoa().getNomeMae();
		this.dataNascimento = r.getPessoa().getDataNascimento();
		this.regime = r.getRegimeAtual().getDescricao();
		this.condicao = r.getCondicaoAtual().getDescricao();
		this.passagens = new ArrayList<ExtratoRecuperandoDTO.PassagemExtratoDTO>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public String getCondicao() {
		return condicao;
	}

	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}

	public List<PassagemExtratoDTO> getPassagens() {
		return passagens;
	}

	public void setPassagens(List<PassagemExtratoDTO> passagens) {
		this.passagens = passagens;
	}

	public List<HistoricoRecuperando> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<HistoricoRecuperando> historicos) {
		this.historicos = historicos;
	}

	public String getLotacao() {
		return lotacao;
	}

	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	public class PassagemExtratoDTO {
		Long passagemId;
		Date dataInicio;
		Date dataTermino;
		String procedencia;
		String observacaoEntrada;
		String observacaoSaida;
		String estruturaEntrada;
		String estruturaSaida;
		
		List<MovimentacaoExtratoDTO> movimentacoes;

		public PassagemExtratoDTO(Passagem pas) {
			this.passagemId = pas.getId();
			this.dataInicio = pas.getDataEntrada();
			this.dataTermino = pas.getDataSaida();
			this.observacaoEntrada = pas.getPassagemProcedencia().getObservacao();
			this.observacaoSaida = pas.getObsDesligamento();
			this.procedencia = pas.getPassagemProcedencia().getProcedencia().getNome();
			this.movimentacoes = new ArrayList<ExtratoRecuperandoDTO.MovimentacaoExtratoDTO>();
			
			this.estruturaEntrada = buscaEstruturaEntrada(pas);
		}
		
		private String buscaEstruturaEntrada(Passagem p) {
			Optional<PassagemLotacao> lotacaoEntrada = p.getLotacao().stream().filter(z->z.getTipoTransito().equals(TipoTransito.ENTRADA)).findFirst();
			
			if(lotacaoEntrada.isPresent()) {
				return lotacaoEntrada.get().getEstrutura().getNomeCompleto();
			} else {
				return null;
			}
		}

		public void criarMovimentacoes(List<PedidoTransitoDTO> pedidos) {
			for (PedidoTransitoDTO pedidoTransitoDTO : pedidos) {
				MovimentacaoExtratoDTO movimentacao = new MovimentacaoExtratoDTO(pedidoTransitoDTO);
				movimentacoes.add(movimentacao);
			}

			setMovimentacoes(movimentacoes.stream().sorted(Comparator.comparing(MovimentacaoExtratoDTO::getDataInicio)).collect(Collectors.toList()));
		}

		public Long getPassagemId() {
			return passagemId;
		}

		public void setPassagemId(Long passagemId) {
			this.passagemId = passagemId;
		}

		public Date getDataInicio() {
			return dataInicio;
		}

		public void setDataInicio(Date dataInicio) {
			this.dataInicio = dataInicio;
		}

		public Date getDataTermino() {
			return dataTermino;
		}

		public void setDataTermino(Date dataTermino) {
			this.dataTermino = dataTermino;
		}

		public String getObservacaoEntrada() {
			return observacaoEntrada;
		}

		public void setObservacaoEntrada(String observacaoEntrada) {
			this.observacaoEntrada = observacaoEntrada;
		}

		public String getObservacaoSaida() {
			return observacaoSaida;
		}

		public void setObservacaoSaida(String observacaoSaida) {
			this.observacaoSaida = observacaoSaida;
		}

		public String getEstruturaEntrada() {
			return estruturaEntrada;
		}

		public void setEstruturaEntrada(String estruturaEntrada) {
			this.estruturaEntrada = estruturaEntrada;
		}

		public String getEstruturaSaida() {
			return estruturaSaida;
		}

		public void setEstruturaSaida(String estruturaSaida) {
			this.estruturaSaida = estruturaSaida;
		}

		public List<MovimentacaoExtratoDTO> getMovimentacoes() {
			return movimentacoes;
		}

		public void setMovimentacoes(List<MovimentacaoExtratoDTO> movimentacoes) {
			this.movimentacoes = movimentacoes;
		}

		public String getProcedencia() {
			return procedencia;
		}

		public void setProcedencia(String procedencia) {
			this.procedencia = procedencia;
		}
	}
	
	public class MovimentacaoExtratoDTO {
		TipoTransito tipoTransito;
		String estrutura;
		Date dataInicio;
		Date dataTermino;
		String observacao;

		public MovimentacaoExtratoDTO(PedidoTransitoDTO pedidoTransitoDTO) {
			this.tipoTransito = pedidoTransitoDTO.getTipoTransito();
			this.dataInicio = pedidoTransitoDTO.getDataPrevista();
			
			switch(pedidoTransitoDTO.getTipoTransito()) {
				case INTERNA:
					this.dataTermino = pedidoTransitoDTO.getDataTerminoMovInt();
					this.estrutura = pedidoTransitoDTO.getEstruturaDestinoNomeCompletoComUnidade();
					break;
					
				case EXTERNA:
					this.dataTermino = pedidoTransitoDTO.getDataTerminoMovExt();
					this.estrutura = pedidoTransitoDTO.getEstabelecimentoNome();
					break;
					
				case TRANSFERENCIA:
					this.dataTermino = pedidoTransitoDTO.getDataTerminoTransf();
					this.estrutura = pedidoTransitoDTO.getNomeUnidadeDestino();
					break;
					
				case REMANEJAMENTO:
					this.estrutura = pedidoTransitoDTO.getNomeUnidadeDestino();
					break;
					
				default:
					break;
			}

			this.observacao = pedidoTransitoDTO.getObservacao();
		}

		public TipoTransito getTipoTransito() {
			return tipoTransito;
		}

		public void setTipoTransito(TipoTransito tipoTransito) {
			this.tipoTransito = tipoTransito;
		}

		public String getEstrutura() {
			return estrutura;
		}

		public void setEstrutura(String estrutura) {
			this.estrutura = estrutura;
		}

		public Date getDataInicio() {
			return dataInicio;
		}

		public void setDataInicio(Date dataInicio) {
			this.dataInicio = dataInicio;
		}

		public Date getDataTermino() {
			return dataTermino;
		}

		public void setDataTermino(Date dataTermino) {
			this.dataTermino = dataTermino;
		}

		public String getObservacao() {
			return observacao;
		}

		public void setObservacao(String observacao) {
			this.observacao = observacao;
		}
	}
}
