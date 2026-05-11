package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.DadosRecuperandoUnificacaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;

@RequestScoped
public class UnificarRecuperandoDAO {

	@Inject
	private EntityManager em;

	private Recuperando recuperando1;

	private Recuperando recuperando2;

	private DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO;

	@Inject
	private BiometriaService bioService;

	@Inject
	private PedidoService pedidoService;

	@Inject
	private HistoricoRecuperandoService historicoRecuperandoService;

	@Inject
	private RecuperandoService service;

	public int buscarQuantidadeFiltrada(FiltroRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Pessoa.class, em);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	public void unificarRecuperando(Recuperando recuperando1, Recuperando recuperando2,
			DadosRecuperandoUnificacaoDTO dados) throws Exception {
		// RN01 - O Sistema deve automaticamente selecionar o número de matrícula que
		// for mais antigo (menor número de matrícula)
		
		
		this.setRecuperando1(recuperando1);
		this.setRecuperando2(recuperando2);
		this.setDadosRecuperandoUnificacaoDTO(dados);

		if (getRecuperando1().getId() < getRecuperando2().getId()) {
			unificarDadosPessoais(getRecuperando1());
			unificarAlcunha(getRecuperando1(), getRecuperando2());
			unificarVidaSocial(getRecuperando1());
			unificarFotos(getRecuperando1());
			unificarPassagens(getRecuperando1());
			unificarParticularidades(getRecuperando1());
			unificarOutrosNomes(getRecuperando1());
			unificarDocumentos(getRecuperando1(), getRecuperando2());
			unificarEnderecos(getRecuperando1());
			unificarContatos(getRecuperando1());
			unificarParentes(getRecuperando1());
			unificarPedidoTransito(getRecuperando1());
			unificarHistoricoRecuperando(getRecuperando1());
			unificarAutorizacaoCadastro(getRecuperando1());
			unificarStatusRecuperando(getRecuperando1());

			if ((getRecuperando1().getPessoa().getBiometria() == null || 
					(getRecuperando1().getPessoa().getBiometria() != null && getRecuperando1().getPessoa().getBiometria().getTemplate() == null)) && 
					(getRecuperando2().getPessoa().getBiometria() != null && getRecuperando2().getPessoa().getBiometria().getTemplate() != null)) {
				Biometria b = getRecuperando2().getPessoa().getBiometria();
				b.setIdPessoa(getRecuperando1().getPessoa().getId());
				bioService.salvarBiometriaSDK(b);
				getRecuperando1().getPessoa().setBiometria(getRecuperando2().getPessoa().getBiometria());
			} 
			if(getRecuperando2().getPessoa().getBiometria() != null) {
				Biometria b = getRecuperando2().getPessoa().getBiometria();
				b.setIdPessoa(getRecuperando2().getPessoa().getId());
				bioService.excluirBiometriaSDK(b);
			}
			
			service.salvarRecuperandoUnificacao(getRecuperando1());
			service.excluirRecuperandoUnificacao(getRecuperando2());
			
			service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando1());
		} else {
			unificarDadosPessoais(getRecuperando2());
			unificarAlcunha(getRecuperando2(), getRecuperando1());
			unificarVidaSocial(getRecuperando2());
			unificarFotos(getRecuperando2());
			unificarPassagens(getRecuperando2());
			unificarParticularidades(getRecuperando2());
			unificarOutrosNomes(getRecuperando2());
			unificarDocumentos(getRecuperando2(), getRecuperando1());
			unificarEnderecos(getRecuperando2());
			unificarContatos(getRecuperando2());
			unificarParentes(getRecuperando2());
			unificarPedidoTransito(getRecuperando2());
			unificarHistoricoRecuperando(getRecuperando2());
			unificarAutorizacaoCadastro(getRecuperando2());
			unificarStatusRecuperando(getRecuperando2());

			if ((getRecuperando2().getPessoa().getBiometria() == null || 
					(getRecuperando2().getPessoa().getBiometria() != null && getRecuperando2().getPessoa().getBiometria().getTemplate() == null)) && 
					(getRecuperando1().getPessoa().getBiometria() != null && getRecuperando1().getPessoa().getBiometria().getTemplate() != null)) {
				Biometria b = getRecuperando1().getPessoa().getBiometria();
				b.setIdPessoa(getRecuperando2().getPessoa().getId());
				bioService.salvarBiometriaSDK(b);
				getRecuperando2().getPessoa().setBiometria(getRecuperando1().getPessoa().getBiometria());
			}
			if(getRecuperando1().getPessoa().getBiometria() != null) {
				Biometria b = getRecuperando1().getPessoa().getBiometria();
				b.setIdPessoa(getRecuperando1().getPessoa().getId());
				bioService.excluirBiometriaSDK(b);
			}

			service.salvarRecuperandoUnificacao(getRecuperando2());
			service.excluirRecuperandoUnificacao(getRecuperando1());
			
			service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando2());
		}
		
	}
	
	public Recuperando getRecuperando1() {
		return recuperando1;
	}

	public void setRecuperando1(Recuperando recuperando1) {
		this.recuperando1 = recuperando1;
	}

	public Recuperando getRecuperando2() {
		return recuperando2;
	}

	public void setRecuperando2(Recuperando recuperando2) {
		this.recuperando2 = recuperando2;
	}

	private void unificarDadosPessoais(Recuperando recuperando) {
		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			OutroNome trinca = new OutroNome();
			trinca.setDataNascimento(getRecuperando2().getPessoa().getDataNascimento());
			trinca.setNome(getRecuperando2().getPessoa().getNome());
			trinca.setNomeMae(getRecuperando2().getPessoa().getNomeMae());
			trinca.setRecuperando(recuperando);
			recuperando.getOutrosNomes().add(trinca);
			recuperando.getPessoa().setNome(getRecuperando1().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando1().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando1().getPessoa().getDataNascimento());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			OutroNome trinca = new OutroNome();
			trinca.setDataNascimento(getRecuperando1().getPessoa().getDataNascimento());
			trinca.setNome(getRecuperando1().getPessoa().getNome());
			trinca.setNomeMae(getRecuperando1().getPessoa().getNomeMae());
			trinca.setRecuperando(recuperando);
			recuperando.getOutrosNomes().add(trinca);
			recuperando.getPessoa().setNome(getRecuperando2().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando2().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando2().getPessoa().getDataNascimento());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando1().getPessoa().getSexo());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando2().getPessoa().getSexo());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando1().getPessoa().getNacionalidade());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando2().getPessoa().getNacionalidade());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando1().getPessoa().getNaturalidade());
			recuperando.getPessoa().setPais(getRecuperando1().getPessoa().getPais());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando2().getPessoa().getNaturalidade());
			recuperando.getPessoa().setPais(getRecuperando2().getPessoa().getPais());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando1().getTemSinalOuTatuagem());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando2().getTemSinalOuTatuagem());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando1().getAltura());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando2().getAltura());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando1().getPeso());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando2().getPeso());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando1().getTipoSanguineo());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando2().getTipoSanguineo());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando1().getRaca());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando2().getRaca());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando1().getCorOlho());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando2().getCorOlho());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando1().getCorCabelo());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando2().getCorCabelo());
		}

	}

	public DadosRecuperandoUnificacaoDTO getDadosRecuperandoUnificacaoDTO() {
		return dadosRecuperandoUnificacaoDTO;
	}

	public void setDadosRecuperandoUnificacaoDTO(DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO) {
		this.dadosRecuperandoUnificacaoDTO = dadosRecuperandoUnificacaoDTO;
	}

	private void unificarAlcunha(Recuperando recuperando, Recuperando rec_retirar) {

		for (Alcunha alcunha1 : rec_retirar.getAlcunhas()) {
			boolean duplicada = false;
			Alcunha alcunha_dupl = null;

			for (Alcunha alcunha2 : recuperando.getAlcunhas()) {

				if (alcunha1.isPrincipal() && alcunha2.isPrincipal()) {
					if (verificaPassagemMaisRecente(recuperando, rec_retirar)) {
						alcunha1.setPrincipal(false);
					} else {
						alcunha2.setPrincipal(false);
					}
				}

				if (alcunha1.getNome().equalsIgnoreCase(alcunha2.getNome())) {
					duplicada = true;
					alcunha_dupl = alcunha2;
				}
			}

			if (!duplicada) {
				alcunha1.setRecuperando(recuperando);
				recuperando.getAlcunhas().add(alcunha1);
			} else if (alcunha1.isPrincipal()) {
				alcunha_dupl.setPrincipal(true);
			}

		}
	}

	private void unificarOutrosNomes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
				Boolean duplicada = false;
				for (OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
					if (outroNome1.getNome().equalsIgnoreCase(outroNome2.getNome())
							&& outroNome1.getNomeMae().equalsIgnoreCase(outroNome2.getNomeMae())
							&& outroNome1.getDataNascimento().compareTo(outroNome2.getDataNascimento()) == 0
							&& !duplicada) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					outroNome1.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome1);
				}
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
				Boolean duplicada = false;
				for (OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
					if (outroNome2.getNome().equalsIgnoreCase(outroNome1.getNome())
							&& outroNome2.getNomeMae().equalsIgnoreCase(outroNome1.getNomeMae())
							&& outroNome2.getDataNascimento().compareTo(outroNome1.getDataNascimento()) == 0
							&& !duplicada) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					outroNome2.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome2);
				}
			}
		}
	}

	private void unificarDocumentosList(boolean isPessoaList, Recuperando recuperando, List<Documento> listManter,
			Recuperando rec_retirar, List<Documento> listRetirar) {

		for (Documento documento1 : listRetirar) {
			boolean duplicada = false;
			for (Documento documento2 : listManter) {

				if (documento1.getTipoDocumento().getId() == documento2.getTipoDocumento().getId()
						&& documento1.getNumero().equalsIgnoreCase(documento2.getNumero()) && !duplicada) {
					duplicada = true;
				}
			}
			if (!duplicada) {

				if (isPessoaList) {
					documento1.setPessoa(recuperando.getPessoa());
					if (recuperando.getPessoa().getDocumentos() != null) {
						recuperando.getPessoa().getDocumentos().add(documento1);
					} else {
						recuperando.getPessoa().setDocumentos(new ArrayList<Documento>());
						recuperando.getPessoa().getDocumentos().add(documento1);
					}

				} else {
					documento1.setRecuperando(recuperando);
					if (recuperando.getDocumentos() != null) {
						recuperando.getDocumentos().add(documento1);
					} else {
						recuperando.setDocumentos(new ArrayList<Documento>());
						recuperando.getDocumentos().add(documento1);
					}
				}

			}
		}
	}

	private void unificarDocumentos(Recuperando recuperando, Recuperando rec_retirar) {

		unificarDocumentosList(false, recuperando, recuperando.getDocumentos(), rec_retirar,
				rec_retirar.getDocumentos());
		unificarDocumentosList(true, recuperando, recuperando.getPessoa().getDocumentos(), rec_retirar,
				rec_retirar.getPessoa().getDocumentos());

	}

	private void unificarEnderecos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for (Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
					if (endereco1.getCidade().getEstado().getNome()
							.equalsIgnoreCase(endereco2.getCidade().getEstado().getNome())
							&& endereco1.getCidade().getId() == endereco2.getCidade().getId()
							&& endereco1.getBairro().equalsIgnoreCase(endereco2.getBairro())
							&& endereco1.getLogradouro().equalsIgnoreCase(endereco2.getLogradouro())
							&& ((endereco1.getNumero() == null && endereco2.getNumero() == null)
									|| (endereco1.getNumero().equalsIgnoreCase(endereco2.getNumero())))
							&& ((endereco1.getComplemento() == null && endereco2.getComplemento() == null)
									|| (endereco1.getComplemento().equalsIgnoreCase(endereco2.getComplemento())))) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					endereco1.setPessoa(recuperando.getPessoa());
					if (recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco1);
					} else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco1);
					}
				}
			}

		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for (Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
					if (endereco2.getCidade().getEstado().getNome()
							.equalsIgnoreCase(endereco1.getCidade().getEstado().getNome())
							&& endereco2.getCidade().getId() == endereco1.getCidade().getId()
							&& endereco2.getBairro().equalsIgnoreCase(endereco1.getBairro())
							&& endereco2.getLogradouro().equalsIgnoreCase(endereco1.getLogradouro())
							&& ((endereco2.getNumero() == null && endereco1.getNumero() == null)
									|| (endereco2.getNumero().equalsIgnoreCase(endereco1.getNumero())))
							&& ((endereco2.getComplemento() == null && endereco1.getComplemento() == null)
									|| (endereco2.getComplemento().equalsIgnoreCase(endereco1.getComplemento())))) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					endereco2.setPessoa(recuperando.getPessoa());
					if (recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco2);
					} else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco2);
					}
				}
			}
		}
	}

	private void unificarContatos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for (Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
					if (contato1.getNome().equalsIgnoreCase(contato2.getNome())
							&& contato1.getTipoContato().getDescricao()
									.equalsIgnoreCase(contato2.getTipoContato().getDescricao())
							&& contato1.getValor().equalsIgnoreCase(contato2.getValor()) && !duplicada) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					contato1.setPessoa(recuperando.getPessoa());
					if (recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato1);
					} else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato1);
					}
				}
			}

		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for (Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
					if (contato2.getNome().equalsIgnoreCase(contato1.getNome())
							&& contato2.getTipoContato().getDescricao()
									.equalsIgnoreCase(contato1.getTipoContato().getDescricao())
							&& contato2.getValor().equalsIgnoreCase(contato1.getValor()) && !duplicada) {
						duplicada = true;
					}
				}
				if (!duplicada) {
					contato2.setPessoa(recuperando.getPessoa());
					if (recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato2);
					} else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato2);
					}
				}
			}
		}
	}

	private void unificarParentes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando1().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando1().getAlteracaoParentes() != null
					&& recuperando.getAlteracaoParentes().before(getRecuperando1().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando1().getAlteracaoParentes());
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando1().getAlteracaoParentes() != null
					&& getRecuperando1().getAlteracaoParentes().before(recuperando.getAlteracaoParentes()))
				recuperando.setAlteracaoParentes(getRecuperando1().getAlteracaoParentes());
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando2().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando2().getAlteracaoParentes() != null
					&& recuperando.getAlteracaoParentes().before(getRecuperando2().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando2().getAlteracaoParentes());
			}
			if (recuperando.getAlteracaoParentes() != null && getRecuperando2().getAlteracaoParentes() != null
					&& getRecuperando2().getAlteracaoParentes().before(recuperando.getAlteracaoParentes()))
				recuperando.setAlteracaoParentes(getRecuperando2().getAlteracaoParentes());
		}
	}

	private boolean verificaPassagemMaisRecente(Recuperando recuperando, Recuperando rec_retirar) {
		boolean isRecente = true;
		for (Passagem passagem1 : recuperando.getPassagens()) {
			isRecente = true;
			for (Passagem passagem2 : rec_retirar.getPassagens()) {
				if (passagem1.getDataEntrada().before(passagem2.getDataEntrada())) {
					isRecente = false;
				}
			}
			if (isRecente) {
				break;
			}
		}

		return isRecente;
	}

	private void unificarPassagens(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando1().getPassagens()) {
				passagem.setRecuperando(recuperando);
				passagem.setFoto(recuperando.getFotos().get(0));
				recuperando.getPassagens().add(passagem);
			}

			for (Processo processo : getRecuperando1().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando2().getPassagens()) {
				passagem.setRecuperando(recuperando);
				passagem.setFoto(recuperando.getFotos().get(0));
				recuperando.getPassagens().add(passagem);
			}

			for (Processo processo : getRecuperando2().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		}
	}

	private void unificarVidaSocial(Recuperando recuperando) {

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual()) {
			recuperando.setOrientacaoSexual(getRecuperando1().getOrientacaoSexual());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual()) {
			recuperando.setOrientacaoSexual(getRecuperando2().getOrientacaoSexual());
		}

		if (dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero() != null) {

			if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero()) {
				recuperando.setIdentidadeDeGenero(getRecuperando1().getIdentidadeDeGenero());
			} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero()) {
				recuperando.setIdentidadeDeGenero(getRecuperando2().getIdentidadeDeGenero());
			}
		}

		if (dadosRecuperandoUnificacaoDTO.getIdNomeSocial() != null) {

			if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeSocial()) {
				recuperando.getPessoa().setNomeSocial(getRecuperando1().getPessoa().getNomeSocial());
			} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeSocial()) {
				recuperando.getPessoa().setNomeSocial(getRecuperando2().getPessoa().getNomeSocial());
			}
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdEstadoCivil()) {
			recuperando.setEstadoCivil(getRecuperando1().getEstadoCivil());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdEstadoCivil()) {
			recuperando.setEstadoCivil(getRecuperando2().getEstadoCivil());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdEscolaridade()) {
			recuperando.setEscolaridade(getRecuperando1().getEscolaridade());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdEscolaridade()) {
			recuperando.setEscolaridade(getRecuperando2().getEscolaridade());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdProfissao()) {
			recuperando.setProfissao(getRecuperando1().getProfissao());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdProfissao()) {
			recuperando.setProfissao(getRecuperando2().getProfissao());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdReligiao()) {
			recuperando.setReligiao(getRecuperando1().getReligiao());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdReligiao()) {
			recuperando.setReligiao(getRecuperando2().getReligiao());
		}

		if (getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdPne()) {
			recuperando.setPne(getRecuperando1().getPne());
		} else if (getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdPne()) {
			recuperando.setPne(getRecuperando2().getPne());
		}

	}

	private void unificarFotos(Recuperando recuperando) {
		if (getRecuperando1().getId() == recuperando.getId()) {
			for (Foto foto : getRecuperando1().getFotos()) {
				foto.setIsAtual(false);
			}
		} else if (getRecuperando2().getId() == recuperando.getId()) {
			for (Foto foto : getRecuperando2().getFotos()) {
				foto.setIsAtual(false);
			}
		}

		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando1().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
			getRecuperando1().getFotos().clear();
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando2().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
			getRecuperando2().getFotos().clear();
		}
	}

	private void unificarParticularidades(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando1().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando2().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		}
	}

	private void unificarPedidoTransito(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando1().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);

			for (PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}

		} else if (getRecuperando2().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando2().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);

			for (PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}
		}
	}

	private void unificarHistoricoRecuperando(Recuperando recuperando) throws Exception {
		if (getRecuperando1().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService
					.listarHistoricoRecuperando(getRecuperando1().getId());

			for (HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}

		} else if (getRecuperando2().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService
					.listarHistoricoRecuperando(getRecuperando2().getId());

			for (HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}
		}
	}

	private void unificarAutorizacaoCadastro(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			if (recuperando.getAutorizacaoCadastroSemBiometria() == null
					|| (getRecuperando1().getAutorizacaoCadastroSemBiometria() != null
							&& getRecuperando1().getAutorizacaoCadastroSemBiometria().getDataAutorizacao()
									.after(recuperando.getAutorizacaoCadastroSemBiometria().getDataAutorizacao()))) {
				recuperando.setAutorizacaoCadastroSemBiometria(getRecuperando1().getAutorizacaoCadastroSemBiometria());
			}

		} else if (getRecuperando2().getId() != recuperando.getId()) {
			if (recuperando.getAutorizacaoCadastroSemBiometria() == null
					|| (getRecuperando2().getAutorizacaoCadastroSemBiometria() != null
							&& getRecuperando2().getAutorizacaoCadastroSemBiometria().getDataAutorizacao()
									.after(recuperando.getAutorizacaoCadastroSemBiometria().getDataAutorizacao()))) {
				recuperando.setAutorizacaoCadastroSemBiometria(getRecuperando2().getAutorizacaoCadastroSemBiometria());
			}
		}
	}

	private void unificarStatusRecuperando(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId() && !recuperando.getStatus().equals(StatusRecuperando.ATIVO)
				&& getRecuperando1().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		} else if (getRecuperando2().getId() != recuperando.getId()
				&& !recuperando.getStatus().equals(StatusRecuperando.ATIVO)
				&& getRecuperando2().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		}
	}

}
