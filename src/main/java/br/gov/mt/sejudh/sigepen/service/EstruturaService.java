package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mt.sejudh.sigepen.dao.CardapioPedidoDTO;
import br.gov.mt.sejudh.sigepen.dao.EstruturaDAO;
import br.gov.mt.sejudh.sigepen.dao.ServidorDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoDetalhadoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoItemCardapioDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class EstruturaService {

	@Inject
	private EstruturaDAO dao;

	@Inject
	private ServidorDAO daoServidor;

	public EstruturaService() {
	}

	@Interceptors(EJBExcInterceptor.class)
	public Estrutura salvar(Estrutura estrutura) {
		if (!estrutura.getCela()) {
			estrutura.setCapacidade(0);
		}
		return dao.salvar(estrutura);
	}

	public void excluir(Estrutura estrutura) {
		dao.excluir(estrutura);
	}

	public Estrutura buscaPorId(Long id) {
		return dao.buscaPorId(id);
	}

	public List<EstruturaDto> buscarEstruturasUnidade(Long idUnidade) {
		return dao.buscarEstruturasUnidade(idUnidade);
	}

	public void montarArvoreEstrutura(Estrutura estrutura, TreeNode no) {
		List<Estrutura> filhos = dao.buscaEstruturaFilho(estrutura);
		if (!filhos.isEmpty()) {
			estrutura.setTemEstruturaFilho(true);
		}
		TreeNode pai = new DefaultTreeNode(estrutura, no);
		pai.setExpanded(true);
		for (Estrutura e : filhos) {
			montarArvoreEstrutura(e, pai);
		}
	}

	public void montarArvoreEstruturaGerarPedido(GerarPedidoEstruturaDTO estrutura, TreeNode no, String dataHora, Long tipoRefeicao) {
		List<GerarPedidoEstruturaDTO> filhos = dao.buscaEstruturaFilhoGerarPedido(estrutura, dataHora, tipoRefeicao);
		if (!filhos.isEmpty()) {
			estrutura.setTemEstruturaFilho(true);
		}
		TreeNode pai = new DefaultTreeNode(estrutura, no);
		pai.setExpanded(true);
		for (GerarPedidoEstruturaDTO e : filhos) {
			montarArvoreEstruturaGerarPedido(e, pai, dataHora, tipoRefeicao);
		}
	}

	public int capacidadeTotalEstrutura(DefaultTreeNode no) {
		Estrutura e = (Estrutura) no.getData();
		int capTotal = e.getCapacidade();
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			capTotal += capacidadeTotalEstrutura((DefaultTreeNode) filho);
		}
		e.setCapacidadeTotal(capTotal);
		no.setData(e);

		return capTotal;
	}

	public int capacidadeTotalEstruturaGerarPedido(DefaultTreeNode no) {
		GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) no.getData();
		int dietaPadrao = e.getDietaPadrao().intValue();
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			dietaPadrao += capacidadeTotalEstruturaGerarPedido((DefaultTreeNode) filho);
		}
		e.setCapacidadeTotal(dietaPadrao);
		no.setData(e);

		return dietaPadrao;
	}

	public int capacidadeTotalDietaPadrao(DefaultTreeNode no) {
		GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) no.getData();
		int dietaPadrao = e.getDietaPadrao().intValue();
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			dietaPadrao += capacidadeTotalDietaPadrao((DefaultTreeNode) filho);
		}
		e.setTotalDietaPadrao(dietaPadrao);
		no.setData(e);

		return dietaPadrao;
	}

	public long capacidadeTotalDietaEspecial(DefaultTreeNode no) {
		GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) no.getData();
		int dietaEspecial = e.getDietaEspecial().intValue();
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			dietaEspecial += (int) capacidadeTotalDietaEspecial((DefaultTreeNode) filho);
		}
		e.setTotalDietaEspecial(dietaEspecial);
		no.setData(e);

		return dietaEspecial;
	}

	public long lotacaoTotalEstrutura(DefaultTreeNode no) {
		Estrutura e = (Estrutura) no.getData();
		long lotacaoTotal = dao.quantidadeRecuperandosLotados(e);
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			lotacaoTotal += lotacaoTotalEstrutura((DefaultTreeNode) filho);
		}
		return lotacaoTotal;
	}

	public DefaultTreeNode buscarNo(DefaultTreeNode root, Estrutura estrutura, Estrutura estruturaOrigem) {
		if (estrutura != null) {
			if (estrutura.getId() == estruturaOrigem.getId()) {
				return root;
			}
		}
		List<TreeNode> filhos = root.getChildren();
		for (TreeNode filho : filhos) {
			Estrutura e = (Estrutura) filho.getData();
			if (e.getId() == estruturaOrigem.getId()) {
				return (DefaultTreeNode) filho;
			} else {
				DefaultTreeNode no = buscarNo((DefaultTreeNode) filho, e, estruturaOrigem);
				if (no != null) {
					return no;
				}
			}
		}
		return null;
	}

	public void salvarSituacaoEstrutura(SituacaoEstruturaUnidade situacaoEstruturaUnidade, DefaultTreeNode root)
			throws ValidacaoException {
		situacaoEstruturaUnidade.setLoginUsuario((SecurityUtil.getLoginUsuario()));
		situacaoEstruturaUnidade.setOrigemAlteracao(true);
		DefaultTreeNode noEstrutura = buscarNo(root, null, situacaoEstruturaUnidade.getEstrutura());
		Estrutura est = (Estrutura) root.getData();
		if (situacaoEstruturaUnidade.getSituacao() == SituacaoUnidade.INATIVO) {
			if (((Estrutura) noEstrutura.getData()).getEstruturaPai() == null
					&& daoServidor.unidadePossuiServidoresLotados(situacaoEstruturaUnidade.getUnidadePrisional())) {
				throw new ValidacaoException("Unidade possui servidores lotados, não pode ser Inativada!");
			}
			if (lotacaoTotalEstrutura(noEstrutura) > 0) {
				throw new ValidacaoException("Estrutura possui recuperandos lotados, não pode ser Inativada!");
			}
		}

		switch (situacaoEstruturaUnidade.getSituacao()) {
		case INTERDITADO_TOTAL:
			if (est.getSituacaoUnidade() != SituacaoUnidade.INATIVO) {
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura,
						situacaoEstruturaUnidade.getEstrutura());
				alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
			}

		case ATIVO:
			alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura,
					situacaoEstruturaUnidade.getEstrutura());
			alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
		case INATIVO:
			alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura,
					situacaoEstruturaUnidade.getEstrutura());
			alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
		default:
			break;
		}

	}

	/**
	 * # INTERDITADO_TOTAL: Ao modificar situação para "interdito_total", os filhos
	 * do nó irão receber interditado_total exceto os filhos inativos. O nó pai irá
	 * receber "interditado_parcial" se tiver um filho ativo ou interditado_parcial,
	 * senão, recebe "interditado_total".
	 * <p>
	 * # INTERDITADO_PARCIAL: O usuário não poderá alterar alguma estrutura para
	 * "interditado_parcial"
	 * <p>
	 * 
	 * # ATIVO
	 * <p>
	 * $ INATIVO
	 *
     */
	@Interceptors(EJBExcInterceptor.class)
	public void alterarSituacaoEstruturasFilhos(SituacaoEstruturaUnidade situacaoEstruturaUnidade, DefaultTreeNode root,
			Estrutura estrutura) {
		Estrutura est;
		SituacaoEstruturaUnidade s;
		switch (situacaoEstruturaUnidade.getSituacao()) {
		case INTERDITADO_TOTAL:
			est = (Estrutura) root.getData();
			est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_TOTAL);
			dao.salvar(est);
			root.setData(est);
			s = new SituacaoEstruturaUnidade();
			s.setDataRegistro(situacaoEstruturaUnidade.getDataRegistro());
			s.setLoginUsuario(situacaoEstruturaUnidade.getLoginUsuario());
			s.setMotivo(situacaoEstruturaUnidade.getMotivo());
			s.setSituacao(situacaoEstruturaUnidade.getSituacao());
			s.setTipoInterdicao(situacaoEstruturaUnidade.getTipoInterdicao());
			s.setUnidadePrisional(situacaoEstruturaUnidade.getUnidadePrisional());
			s.setEstrutura(est);
			if (est.equals(estrutura)) {
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else {
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for (TreeNode filho : root.getChildren()) {
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode) filho, estrutura);
			}
			break;

		case ATIVO:
			est = (Estrutura) root.getData();
			est.setSituacaoUnidade(SituacaoUnidade.ATIVO);
			dao.salvar(est);
			root.setData(est);
			s = new SituacaoEstruturaUnidade();
			s.setDataRegistro(situacaoEstruturaUnidade.getDataRegistro());
			s.setLoginUsuario(situacaoEstruturaUnidade.getLoginUsuario());
			s.setMotivo(situacaoEstruturaUnidade.getMotivo());
			s.setSituacao(situacaoEstruturaUnidade.getSituacao());
			s.setTipoInterdicao(situacaoEstruturaUnidade.getTipoInterdicao());
			s.setUnidadePrisional(situacaoEstruturaUnidade.getUnidadePrisional());
			s.setEstrutura(est);
			if (est.equals(estrutura)) {
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else {
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for (TreeNode filho : root.getChildren()) {
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode) filho, estrutura);
			}
			break;
		case INATIVO:
			est = (Estrutura) root.getData();
			est.setSituacaoUnidade(SituacaoUnidade.INATIVO);
			dao.salvar(est);
			root.setData(est);
			s = new SituacaoEstruturaUnidade();
			s.setDataRegistro(situacaoEstruturaUnidade.getDataRegistro());
			s.setLoginUsuario(situacaoEstruturaUnidade.getLoginUsuario());
			s.setMotivo(situacaoEstruturaUnidade.getMotivo());
			s.setSituacao(situacaoEstruturaUnidade.getSituacao());
			s.setTipoInterdicao(situacaoEstruturaUnidade.getTipoInterdicao());
			s.setUnidadePrisional(situacaoEstruturaUnidade.getUnidadePrisional());
			s.setEstrutura(est);
			if (est.equals(estrutura)) {
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else {
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for (TreeNode filho : root.getChildren()) {
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode) filho, estrutura);
			}
			break;
		default:
			break;
		}
	}

	@Interceptors(EJBExcInterceptor.class)
	public void alterarSituacaoEstruturaPai(SituacaoEstruturaUnidade situacaoEstruturaUnidade, DefaultTreeNode root) {
		Estrutura e = (Estrutura) root.getData();
		Estrutura est;
		Estrutura estruturaOrigem;
		DefaultTreeNode noPai;
		SituacaoEstruturaUnidade s;

		if (e.getEstruturaPai() != null) {
			if (situacaoEstruturaUnidade.getSituacao() != null && situacaoEstruturaUnidade.getSituacao() == SituacaoUnidade.INTERDITADO_TOTAL) {
				noPai = (DefaultTreeNode) root.getParent();
				est = (Estrutura) noPai.getData();
				estruturaOrigem = (Estrutura) root.getData();

				s = new SituacaoEstruturaUnidade();
				s.setDataRegistro(situacaoEstruturaUnidade.getDataRegistro());
				s.setLoginUsuario(situacaoEstruturaUnidade.getLoginUsuario());
				s.setMotivo(situacaoEstruturaUnidade.getMotivo());
				s.setTipoInterdicao(situacaoEstruturaUnidade.getTipoInterdicao());
				s.setUnidadePrisional(situacaoEstruturaUnidade.getUnidadePrisional());
				s.setOrigemAlteracao(false);

				if (estruturaOrigem.getSituacaoUnidade() == SituacaoUnidade.INTERDITADO_PARCIAL) {
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_PARCIAL);
				} else if (validaFilhoAtivoOuParcial(noPai)) {
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_PARCIAL);
				} else {
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_TOTAL);
				}
				dao.salvar(est);
				noPai.setData(est);

				s.setSituacao(est.getSituacaoUnidade());
				s.setEstrutura(est);
				dao.salvarSituacaoEstrutura(s);

				this.alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noPai);
			} else {
				/*
				 * Atende os casos de ATIVO e INATIVO
				 */
				noPai = (DefaultTreeNode) root.getParent();
				est = (Estrutura) noPai.getData();

				s = new SituacaoEstruturaUnidade();
				s.setDataRegistro(situacaoEstruturaUnidade.getDataRegistro());
				s.setLoginUsuario(situacaoEstruturaUnidade.getLoginUsuario());
				s.setMotivo(situacaoEstruturaUnidade.getMotivo());
				s.setTipoInterdicao(situacaoEstruturaUnidade.getTipoInterdicao());
				s.setUnidadePrisional(situacaoEstruturaUnidade.getUnidadePrisional());
				s.setOrigemAlteracao(false);

				if (validaFilhoAtivoOuInativo(noPai)) {
					est.setSituacaoUnidade(SituacaoUnidade.ATIVO);
				} else {
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_PARCIAL);
				}
				dao.salvar(est);
				noPai.setData(est);

				s.setSituacao(est.getSituacaoUnidade());
				s.setEstrutura(est);
				dao.salvarSituacaoEstrutura(s);

				this.alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noPai);
			}
		}
	}

	/**
	 * Verifica se se tem pelo menos 1 filho com situação de Ativo ou Inativo. Se
	 * encontrar 1, retorna verdadeiro, senão falso.
	 * 
	 * @param no da estruturaUnidade
	 * @return true or false
	 */
	public boolean validaFilhoAtivoOuParcial(DefaultTreeNode no) {
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			if (((Estrutura) filho.getData()).getSituacaoUnidade() == SituacaoUnidade.ATIVO
					|| ((Estrutura) filho.getData()).getSituacaoUnidade() == SituacaoUnidade.INTERDITADO_PARCIAL) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * Se encontrar pelo menos um filho diferente de Ativo e Inativo, o retorno será
	 * false, caso contrário o retorno será true;
	 * 
	 * @param no da estruturaUnidade
	 * @return true or false
	 */
	public boolean validaFilhoAtivoOuInativo(DefaultTreeNode no) {
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			if (((Estrutura) filho.getData()).getSituacaoUnidade() != SituacaoUnidade.ATIVO
					&& ((Estrutura) filho.getData()).getSituacaoUnidade() != SituacaoUnidade.INATIVO) {
				return false;
			}
		}

		return true;
	}

	public List<SituacaoEstruturaUnidade> listarPorUnidade(Long unidadePrisional) {
		return dao.listarPorUnidade(unidadePrisional);
	}

	public void buscarEstruturasFilhos(Estrutura estrutura, List<Estrutura> retorno) {
		List<Estrutura> filhos = dao.buscaEstruturaFilho(estrutura);
		for (Estrutura e : filhos) {
			if (e.getCela()) {
				retorno.add(e);
			} else {
				retorno.add(e);
				buscarEstruturasFilhos(e, retorno);
			}
		}
	}

	public void montarArvoreDaUnidade(List<EstruturaDto> estruturas, TreeNode no) {
		Optional<EstruturaDto> optPai = estruturas.stream().filter(e -> e.getIdEstruturaPai() == null).findFirst();

		if (optPai.isPresent()) {
			EstruturaDto estruturaPai = optPai.get();
			TreeNode noPai = new DefaultTreeNode(estruturaPai, no);
			List<EstruturaDto> estruturasFilho = estruturas.stream()
					.filter(e -> Objects.equals(e.getIdEstruturaPai(), estruturaPai.getId()))
					.collect(Collectors.toList());

			if (!estruturasFilho.isEmpty()) {
				estruturaPai.setTemEstruturaFilho(true);
				noPai.setExpanded(true);
			}

			estruturasFilho.forEach(e -> montarArvoreDaUnidade(estruturas, e, noPai));
		}
	}

	private void montarArvoreDaUnidade(List<EstruturaDto> estruturas, EstruturaDto estruturaPai, TreeNode no) {
		TreeNode noPai = new DefaultTreeNode(estruturaPai, no);
		List<EstruturaDto> estruturasFilho = estruturas.stream()
				.filter(e -> Objects.equals(e.getIdEstruturaPai(), estruturaPai.getId())).collect(Collectors.toList());

		if (!estruturasFilho.isEmpty()) {
			estruturaPai.setTemEstruturaFilho(true);
			noPai.setExpanded(true);
		}

		estruturasFilho.forEach(e -> montarArvoreDaUnidade(estruturas, e, noPai));
	}

	public PedidoDetalhadoDTO buscaPedidoDetalhado(String data, Long idEstrutura, TipoRefeicao tipoRefeicao) {
		List<PedidoConsultaDTO> pedidoConsulta = dao.buscaPedidoDetalhado(data, idEstrutura, tipoRefeicao.getId());
		return montarPedidoDetalhado(pedidoConsulta, idEstrutura, tipoRefeicao, data);
	}

	public List<PedidoConsultaDTO> buscaPedidoConsulta(String data, Long idEstrutura, TipoRefeicao tipoRefeicao) {
		return dao.buscaPedidoDetalhado(data, idEstrutura, tipoRefeicao.getId());
	}

	private PedidoDetalhadoDTO montarPedidoDetalhado(List<PedidoConsultaDTO> list, Long idEstrutura,
		TipoRefeicao tipoRefeicao, String data) {
		CardapioPedidoDTO pedidoCardapio = new CardapioPedidoDTO();
		PedidoDetalhadoDTO pedidoDetalhado = new PedidoDetalhadoDTO();
		List<CardapioPedidoDTO> listaCardapio = new ArrayList<>();
		pedidoDetalhado.setDataPedido(data);

		int size = list.size();
		for (int i = 0; i < size; i++) {
			PedidoConsultaDTO pedidoConsulta = list.get(i);
			if (pedidoConsulta.isEspecial()) {
				pedidoDetalhado.setQtdEspecial(pedidoConsulta.getRecuperandoTotal().intValue());
			} else {
				pedidoDetalhado.setQtdPadrao(pedidoConsulta.getRecuperandoTotal().intValue());
			}
			PedidoItemCardapioDTO pedidoItem = new PedidoItemCardapioDTO();
			pedidoItem.setDescricao(pedidoConsulta.getItem());
			Double peso = pedidoConsulta.getSexo().equals("M") ? pedidoConsulta.getMasc() : pedidoConsulta.getFem();
			pedidoItem.setQuantidade(Double.valueOf(pedidoConsulta.getRecuperandoTotal()), peso);
			if ((pedidoCardapio.getNome() == null || pedidoConsulta.getCardapio().equals(pedidoCardapio.getNome()))
					&& (pedidoCardapio.getEstrutura() == null
							|| pedidoConsulta.getNomeEstrutura().equals(pedidoCardapio.getEstrutura()))) {
				pedidoCardapio.setNome(pedidoConsulta.getCardapio());
				pedidoCardapio.setEstrutura(pedidoConsulta.getNomeEstrutura());
				pedidoCardapio.getItens().add(pedidoItem);
				pedidoCardapio.setEspecial(pedidoConsulta.isEspecial());
			} else {
				listaCardapio.add(new CardapioPedidoDTO(pedidoCardapio));
				pedidoCardapio.setEspecial(pedidoConsulta.isEspecial());
				pedidoCardapio.getItens().clear();
				pedidoCardapio.setNome(pedidoConsulta.getCardapio());
				pedidoCardapio.setEstrutura(pedidoConsulta.getNomeEstrutura());
				pedidoCardapio.getItens().add(pedidoItem);
			}
			if ((i + 1) == size) {
				listaCardapio.add(new CardapioPedidoDTO(pedidoCardapio));
			}
		}

		pedidoDetalhado.setCardapio(listaCardapio);
		pedidoDetalhado.setEstrutura(nomeEstrutura(idEstrutura));
		pedidoDetalhado.setRefeicao(tipoRefeicao.getDescricao());
		return pedidoDetalhado;
	}

	public String nomeEstrutura(Long idEstrutura) {
		return dao.buscarNomeEstrutura(idEstrutura);
	}

	public List<PedidoEstruturaDTO> montarEstruturaPedido(String data, long idEstrutura) {
		return dao.montarEstruturaPedido(data, idEstrutura);
	}
}
