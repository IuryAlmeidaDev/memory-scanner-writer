package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.TipoProcesso;
import br.gov.mt.sejudh.sigepen.model.Regime;

public class RelatorioRecuperandoJurisdicionalDTO {

		
		
	private String regimeAtualDescricao;
	
	private long matricula;
	private String nome;
	private String nomeSocial;
	private String nomeMae;
	private Regime regimeAtual;
	private Condicao condicaoAtual;
	private long idPassagem;
	private String passagemProcedencia;
	private Date passagemDataEntrada;
	private Date passagemDataSaida;
	private long idProcesso;
	private String processoNumero;
	private String processoOficio;
	private String processoInquerito;
	private long idDocumento;
	private String documentoNumero;
	private TipoProcesso documentoTipo;
	private Date documentoData;
	private String documentoSecao;
	private String documentoObservacao;
	private Regime documentoRegime;
	private Condicao documentoCondicao;
	private Modalidade documentoModalidade;
	private Date documentoDataSoltura;
	
	public static void prepararLista(List<RelatorioRecuperandoJurisdicionalDTO> lista, List<RecuperandoJurisdicionalDTO> recuperandos) {
		List<Long> idsRecuperandos = lista.stream().map(r -> r.getMatricula()).distinct().collect(Collectors.toList());
		
		idsRecuperandos.forEach(i -> {
			Optional<RecuperandoJurisdicionalDTO> optRec = lista.stream().filter(r -> r.getMatricula() == i).map(r -> r.new RecuperandoJurisdicionalDTO(r.getMatricula(), r.getNome(), r.getNomeMae(), r.getRegimeAtual(), r.getCondicaoAtual(), r.getNomeSocial())).findFirst();
			RecuperandoJurisdicionalDTO recuperando = optRec.get();
		
			List<PassagemJurisdicionalDTO> passagens = lista.stream().filter(r -> r.getMatricula() == i).map(r -> r.new PassagemJurisdicionalDTO(r.getIdPassagem(), r.getPassagemProcedencia(), r.getPassagemDataEntrada(), r.getPassagemDataSaida())).distinct().collect(Collectors.toList());
			List<ProcessoJurisdicionalDTO> processos = lista.stream().filter(r -> r.getMatricula() == i).map(r -> r.new ProcessoJurisdicionalDTO(r.getIdProcesso(), r.getProcessoNumero(), r.getProcessoOficio(), r.getProcessoInquerito())).distinct().collect(Collectors.toList());

			processos.forEach(p -> {
				List<DocumentoJurisdicionalDTO> documentos = lista.stream().filter(r -> r.getIdProcesso() == p.processoId).map(r -> r.new DocumentoJurisdicionalDTO(r.getIdDocumento(), r.getDocumentoNumero(), r.getDocumentoTipo(), r.getDocumentoData(), r.getDocumentoSecao(), r.getDocumentoObservacao(), r.getDocumentoRegime(), r.getDocumentoCondicao(), r.getDocumentoModalidade(), r.getDocumentoDataSoltura())).distinct().collect(Collectors.toList());
				p.adicionarDocumentos(documentos);
			});
			
			recuperando.adicionarPassagens(passagens);
			recuperando.adicionarProcessos(processos);
			
			recuperandos.add(recuperando);
		});
	}

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public class RecuperandoJurisdicionalDTO {
		private long matricula;
		private String nome;
		private String nomeSocial;
		private String nomeMae;
		private Regime regimeAtual;
		private Condicao condicaoAtual;
		
		private List<PassagemJurisdicionalDTO> passagens;
		private List<ProcessoJurisdicionalDTO> processos;
		
		public RecuperandoJurisdicionalDTO(long matricula, String nome, String nomeMae, Regime regimeAtual, Condicao condicaoAtual, String nomeSocial) {
			super();
			this.matricula = matricula;
			this.nome = nome;
			this.nomeSocial = nomeSocial;
			this.nomeMae = nomeMae;
			this.regimeAtual = regimeAtual;
			this.condicaoAtual = condicaoAtual;
			
			this.passagens = new ArrayList<RelatorioRecuperandoJurisdicionalDTO.PassagemJurisdicionalDTO>();
			this.processos = new ArrayList<RelatorioRecuperandoJurisdicionalDTO.ProcessoJurisdicionalDTO>();
		}

		public long getMatricula() {
			return matricula;
		}

		public String getNome() {
			return nome;
		}

		public String getNomeMae() {
			return nomeMae;
		}

		public Regime getRegimeAtual() {
			return regimeAtual;
		}

		public Condicao getCondicaoAtual() {
			return condicaoAtual;
		}

		public List<PassagemJurisdicionalDTO> getPassagens() {
			return passagens;
		}

		public List<ProcessoJurisdicionalDTO> getProcessos() {
			return processos;
		}
		
		public void adicionarPassagens(List<PassagemJurisdicionalDTO> passagens) {
			this.passagens.addAll(passagens);
		}
		
		public void adicionarProcessos(List<ProcessoJurisdicionalDTO> processos) {
			this.processos.addAll(processos);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (matricula ^ (matricula >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			RecuperandoJurisdicionalDTO other = (RecuperandoJurisdicionalDTO) obj;

			if (matricula != other.matricula)
				return false;
			return true;
		}

        public String getNomeSocial() {
            return nomeSocial;
        }

        public void setNomeSocial(String nomeSocial) {
            this.nomeSocial = nomeSocial;
        }
    }
	
	public class PassagemJurisdicionalDTO {
		private long passagemId;
		private String passagemProcedencia;
		private Date passagemDataEntrada;
		private Date passagemDataSaida;
		
		public PassagemJurisdicionalDTO(long passagemId, String passagemProcedencia, Date passagemDataEntrada, Date passagemDataSaida) {
			super();
			this.passagemId = passagemId;
			this.passagemProcedencia = passagemProcedencia;
			this.passagemDataEntrada = passagemDataEntrada;
			this.passagemDataSaida = passagemDataSaida;
		}
		
		public long getPassagemId() {
			return passagemId;
		}
		
		public String getPassagemProcedencia() {
			return passagemProcedencia;
		}
		
		public Date getPassagemDataEntrada() {
			return passagemDataEntrada;
		}
		
		public Date getPassagemDataSaida() {
			return passagemDataSaida;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (passagemId ^ (passagemId >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			PassagemJurisdicionalDTO other = (PassagemJurisdicionalDTO) obj;

			if (passagemId != other.passagemId)
				return false;
			return true;
		}
	}
	
	public class ProcessoJurisdicionalDTO {
		private long processoId;
		private String processoNumero;
		private String processoOficio;
		private String processoInquerito;
		
		private List<DocumentoJurisdicionalDTO> documentos;
		
		public ProcessoJurisdicionalDTO(long processoId, String processoNumero, String processoOficio, String processoInquerito) {
			super();
			this.processoId = processoId;
			this.processoNumero = processoNumero;
			this.processoOficio = processoOficio;
			this.processoInquerito = processoInquerito;
			
			this.documentos = new ArrayList<RelatorioRecuperandoJurisdicionalDTO.DocumentoJurisdicionalDTO>();
		}

		public long getProcessoId() {
			return processoId;
		}

		public String getProcessoNumero() {
			return processoNumero;
		}

		public String getProcessoOficio() {
			return processoOficio;
		}

		public String getProcessoInquerito() {
			return processoInquerito;
		}
		
		public List<DocumentoJurisdicionalDTO> getDocumentos() {
			return documentos;
		}

		public void adicionarDocumentos(List<DocumentoJurisdicionalDTO> documentos) {
			this.documentos.addAll(documentos);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (processoId ^ (processoId >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			ProcessoJurisdicionalDTO other = (ProcessoJurisdicionalDTO) obj;

			if (processoId != other.processoId)
				return false;
			return true;
		}
	}
	
	public class DocumentoJurisdicionalDTO {
		private long idDocumento;
		private String documentoNumero;
		private TipoProcesso documentoTipo;
		private Date documentoData;
		private String documentoSecao;
		private String documentoObservacao;
		private Regime documentoRegime;
		private Condicao documentoCondicao;
		private Modalidade documentoModalidade;
		private Date documentoDataSoltura;
		
		public DocumentoJurisdicionalDTO(long idDocumento, String documentoNumero, TipoProcesso documentoTipo, Date documentoData, String documentoSecao,
				String documentoObservacao, Regime documentoRegime, Condicao documentoCondicao,
				Modalidade documentoModalidade, Date documentoDataSoltura) {
			super();
			this.idDocumento = idDocumento;
			this.documentoNumero = documentoNumero;
			this.documentoTipo = documentoTipo;
			this.documentoData = documentoData;
			this.documentoSecao = documentoSecao;
			this.documentoObservacao = documentoObservacao;
			this.documentoRegime = documentoRegime;
			this.documentoCondicao = documentoCondicao;
			this.documentoModalidade = documentoModalidade;
			this.documentoDataSoltura = documentoDataSoltura;
		}

		public long getIdDocumento() {
			return idDocumento;
		}
		
		public Date getDocumentoData() {
			return documentoData;
		}
		
		public String getDocumentoNumero() {
			return documentoNumero;
		}

		public TipoProcesso getDocumentoTipo() {
			return documentoTipo;
		}

		public String getDocumentoSecao() {
			return documentoSecao;
		}
		
		public String getDocumentoObservacao() {
			return documentoObservacao;
		}
		
		public Regime getDocumentoRegime() {
			return documentoRegime;
		}
		
		public Condicao getDocumentoCondicao() {
			return documentoCondicao;
		}
		
		public Modalidade getDocumentoModalidade() {
			return documentoModalidade;
		}
		
		public Date getDocumentoDataSoltura() {
			return documentoDataSoltura;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (idDocumento ^ (idDocumento >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			DocumentoJurisdicionalDTO other = (DocumentoJurisdicionalDTO) obj;
			
			if (idDocumento != other.idDocumento)
				return false;
			return true;
		}
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Regime getRegimeAtual() {
		return regimeAtual;
	}

	public void setRegimeAtual(Regime regimeAtual) {
		this.regimeAtual = regimeAtual;
	}

	public Condicao getCondicaoAtual() {
		return condicaoAtual;
	}

	public void setCondicaoAtual(Condicao condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}

	public long getIdPassagem() {
		return idPassagem;
	}

	public void setIdPassagem(long idPassagem) {
		this.idPassagem = idPassagem;
	}

	public String getPassagemProcedencia() {
		return passagemProcedencia;
	}

	public void setPassagemProcedencia(String passagemProcedencia) {
		this.passagemProcedencia = passagemProcedencia;
	}

	public Date getPassagemDataEntrada() {
		return passagemDataEntrada;
	}

	public void setPassagemDataEntrada(Date passagemDataEntrada) {
		this.passagemDataEntrada = passagemDataEntrada;
	}

	public Date getPassagemDataSaida() {
		return passagemDataSaida;
	}

	public void setPassagemDataSaida(Date passagemDataSaida) {
		this.passagemDataSaida = passagemDataSaida;
	}

	public long getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(long idProcesso) {
		this.idProcesso = idProcesso;
	}

	public String getProcessoNumero() {
		return processoNumero;
	}

	public void setProcessoNumero(String processoNumero) {
		this.processoNumero = processoNumero;
	}

	public String getProcessoOficio() {
		return processoOficio;
	}

	public void setProcessoOficio(String processoOficio) {
		this.processoOficio = processoOficio;
	}

	public String getProcessoInquerito() {
		return processoInquerito;
	}

	public void setProcessoInquerito(String processoInquerito) {
		this.processoInquerito = processoInquerito;
	}

	public long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(long idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getDocumentoNumero() {
		return documentoNumero;
	}

	public void setDocumentoNumero(String documentoNumero) {
		this.documentoNumero = documentoNumero;
	}

	public TipoProcesso getDocumentoTipo() {
		return documentoTipo;
	}

	public void setDocumentoTipo(TipoProcesso documentoTipo) {
		this.documentoTipo = documentoTipo;
	}

	public Date getDocumentoData() {
		return documentoData;
	}

	public void setDocumentoData(Date documentoData) {
		this.documentoData = documentoData;
	}

	public String getDocumentoSecao() {
		return documentoSecao;
	}

	public void setDocumentoSecao(String documentoSecao) {
		this.documentoSecao = documentoSecao;
	}

	public String getDocumentoObservacao() {
		return documentoObservacao;
	}

	public void setDocumentoObservacao(String documentoObservacao) {
		this.documentoObservacao = documentoObservacao;
	}

	public Regime getDocumentoRegime() {
		return documentoRegime;
	}

	public void setDocumentoRegime(Regime documentoRegime) {
		this.documentoRegime = documentoRegime;
	}

	public Condicao getDocumentoCondicao() {
		return documentoCondicao;
	}

	public void setDocumentoCondicao(Condicao documentoCondicao) {
		this.documentoCondicao = documentoCondicao;
	}

	public Modalidade getDocumentoModalidade() {
		return documentoModalidade;
	}

	public void setDocumentoModalidade(Modalidade documentoModalidade) {
		this.documentoModalidade = documentoModalidade;
	}

	public Date getDocumentoDataSoltura() {
		return documentoDataSoltura;
	}

	public void setDocumentoDataSoltura(Date documentoDataSoltura) {
		this.documentoDataSoltura = documentoDataSoltura;
	}

	public String getRegimeAtualDescricao() {
		return regimeAtualDescricao;
	}

	public void setRegimeAtualDescricao(String regimeAtualDescricao) {
		this.regimeAtualDescricao = regimeAtualDescricao;
	}
}
