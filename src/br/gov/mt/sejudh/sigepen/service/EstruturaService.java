package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mt.sejudh.sigepen.dao.EstruturaDAO;
import br.gov.mt.sejudh.sigepen.dao.ServidorDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;


@Stateless
public class EstruturaService {
	
	@Inject
	private EstruturaDAO dao;
	
	@Inject
	private ServidorDAO daoServidor;
	
	
	public EstruturaService(){		
		}
	
	public Estrutura salvar(Estrutura estrutura) {
		if(!estrutura.getCela()){
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
	
	public Estrutura buscaEstraturaRaizUnidade(Long id) {		
		return dao.buscaEstraturaRaizUnidade(id);
	}
	
	public Estrutura buscaEstruturaCompletaPorId(Long id) {
		return dao.buscaEstruturaCompletaPorId(id);
	}
	
	public void montarArvoreEstrutura(Estrutura estrutura, TreeNode no) {
		List<Estrutura> filhos = dao.buscaEstruturaFilho(estrutura);
		if(filhos.size() > 0){
			estrutura.setTemEstruturaFilho(true);
		}
		TreeNode pai = new DefaultTreeNode(estrutura, no);
		pai.setExpanded(true);
		for (Estrutura e : filhos) {
			montarArvoreEstrutura(e, pai);
		}
	}

	public int capacidadeTotalEstrutura(DefaultTreeNode no) {
		Estrutura e = (Estrutura) no.getData();
		int capTotal = e.getCapacidade();
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			capTotal += capacidadeTotalEstrutura((DefaultTreeNode)filho);
		}
		e.setCapacidadeTotal(capTotal);
		no.setData(e);
		
		return capTotal;
	}
	
	public long lotacaoTotalEstrutura(DefaultTreeNode no) {
		Estrutura e = (Estrutura) no.getData();
		long lotacaoTotal = dao.quantidadeRecuperandosLotados(e);
		List<TreeNode> filhos = no.getChildren();
		for (TreeNode filho : filhos) {
			lotacaoTotal += capacidadeTotalEstrutura((DefaultTreeNode)filho);
		}		
		return lotacaoTotal;
	}
	
	public DefaultTreeNode buscarNo(DefaultTreeNode root, Estrutura estrutura, Estrutura estruturaOrigem){
		if(estrutura != null){
			if(estrutura.getId() == estruturaOrigem.getId()){
				return root;
			}
		}
		List<TreeNode> filhos = root.getChildren();
		for (TreeNode filho : filhos) {
			Estrutura e = (Estrutura) filho.getData();
			if(e.getId() == estruturaOrigem.getId()){
				return (DefaultTreeNode) filho;
			} else {
				DefaultTreeNode no =  buscarNo((DefaultTreeNode)filho, e, estruturaOrigem);
				if(no != null){
					return no;
				}
			}
		}
		return null;
	}

	/**
	 * Situação Estrutura 
	 * @param situacaoEstruturaUnidade
	 * @throws ValidacaoException 
	 */
	public void salvarSituacaoEstrutura(SituacaoEstruturaUnidade situacaoEstruturaUnidade,  DefaultTreeNode root) throws ValidacaoException {		
		situacaoEstruturaUnidade.setLoginUsuario((SecurityUtil.getLoginUsuario()));
		situacaoEstruturaUnidade.setOrigemAlteracao(true);
		DefaultTreeNode noEstrutura = buscarNo(root, null, situacaoEstruturaUnidade.getEstrutura());
		Estrutura est = (Estrutura) root.getData();
		if (situacaoEstruturaUnidade.getSituacao() == SituacaoUnidade.INATIVO) {
			if(((Estrutura)noEstrutura.getData()).getEstruturaPai() == null && daoServidor.unidadePossuiServidoresLotados(situacaoEstruturaUnidade.getUnidadePrisional())){
				throw new ValidacaoException("Unidade possui servidores lotados, não pode ser Inativada!");
			}
			if (lotacaoTotalEstrutura(noEstrutura) > 0) {
				throw new ValidacaoException("Estrutura possui recuperandos lotados, não pode ser Inativada!");
			}			
		}
		
		switch (situacaoEstruturaUnidade.getSituacao()) {
			case INTERDITADO_TOTAL:
				if(est.getSituacaoUnidade() != SituacaoUnidade.INATIVO){
					alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura, situacaoEstruturaUnidade.getEstrutura());
					alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
				}
				
			case ATIVO:
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura, situacaoEstruturaUnidade.getEstrutura());
				alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
			case INATIVO:
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, noEstrutura, situacaoEstruturaUnidade.getEstrutura());
				alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noEstrutura);
			default:
				break;
		}
		
		
	}
	
	/**
	 * # INTERDITADO_TOTAL:
	 * 		Ao modificar situação para "interdito_total", os filhos do nó irão receber interditado_total exceto os filhos inativos.
	 * 		O nó pai irá receber "interditado_parcial" se tiver um filho ativo ou interditado_parcial, senão, recebe "interditado_total". 
	 * 
	 * # INTERDITADO_PARCIAL:
	 * 		O usuário não poderá alterar alguma estrutura para "interditado_parcial"
	 * 	
	 * 
	 * # ATIVO
	 * 		
	 * $ INATIVO
	 * 		
	 * @param situacaoEstruturaUnidade
	 */
	public void alterarSituacaoEstruturasFilhos(SituacaoEstruturaUnidade situacaoEstruturaUnidade, DefaultTreeNode root, Estrutura estrutura){
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
			if(est.equals(estrutura)){
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else{
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for(TreeNode filho : root.getChildren()){
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode)filho, estrutura);
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
			if(est.equals(estrutura)){
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else{
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for(TreeNode filho : root.getChildren()){
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode)filho, estrutura);
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
			if(est.equals(estrutura)){
				situacaoEstruturaUnidade.setOrigemAlteracao(true);
				dao.salvarSituacaoEstrutura(situacaoEstruturaUnidade);
			} else{
				s.setOrigemAlteracao(false);
				dao.salvarSituacaoEstrutura(s);
			}
			for(TreeNode filho : root.getChildren()){
				alterarSituacaoEstruturasFilhos(situacaoEstruturaUnidade, (DefaultTreeNode)filho, estrutura);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 
	 * @param situacaoEstruturaUnidade
	 * @param root
	 */
	public void alterarSituacaoEstruturaPai(SituacaoEstruturaUnidade situacaoEstruturaUnidade, DefaultTreeNode root){
		Estrutura e = (Estrutura) root.getData();
		Estrutura est;
		Estrutura estruturaOrigem;
		DefaultTreeNode noPai;
		SituacaoEstruturaUnidade s;
		
		if(e.getEstruturaPai() != null){
			switch (situacaoEstruturaUnidade.getSituacao()) {
			case INTERDITADO_TOTAL:
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
				
				if(estruturaOrigem.getSituacaoUnidade() == SituacaoUnidade.INTERDITADO_PARCIAL){
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_PARCIAL);
				} else if(validaFilhoAtivoOuParcial(noPai)){
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_PARCIAL);
				} else{
					est.setSituacaoUnidade(SituacaoUnidade.INTERDITADO_TOTAL);
				}
				dao.salvar(est);
				noPai.setData(est);
				
				s.setSituacao(est.getSituacaoUnidade());
				s.setEstrutura(est);
				dao.salvarSituacaoEstrutura(s);
				
				this.alterarSituacaoEstruturaPai(situacaoEstruturaUnidade, noPai);
				break;
				
			default:
				/**
				 *  Atende os casos de ATIVO e INATIVO
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
				
				if(validaFilhoAtivoOuInativo(noPai)){
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
				break;
			}
		}
	}
	
	/**
	 * Verifica se se tem pelo menos 1 filho com situação de Ativo ou Inativo.
	 * Se encontrar 1, retorna verdadeiro, senão falso.
	 * @param nó da estruturaUnidade
	 * @return true or false
	 */
	public boolean validaFilhoAtivoOuParcial(DefaultTreeNode no){
		List<TreeNode> filhos =  no.getChildren();
		for (TreeNode filho : filhos) {
			if(((Estrutura)filho.getData()).getSituacaoUnidade() == SituacaoUnidade.ATIVO || ((Estrutura)filho.getData()).getSituacaoUnidade() == SituacaoUnidade.INTERDITADO_PARCIAL){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * Se encontrar pelo menos um filho diferente de Ativo e Inativo, o retorno será false, caso contrário o retorno será true;  
	 * @param nó da estruturaUnidade
	 * @return true or false
	 */
	public boolean validaFilhoAtivoOuInativo(DefaultTreeNode no){
		List<TreeNode> filhos =  no.getChildren();
		for (TreeNode filho : filhos) {
			if(((Estrutura)filho.getData()).getSituacaoUnidade() != SituacaoUnidade.ATIVO && ((Estrutura)filho.getData()).getSituacaoUnidade() != SituacaoUnidade.INATIVO){
				return false;
			}
		}
		
		return true;
	}

	public List<SituacaoEstruturaUnidade> listarPorUnidade(Long unidadePrisional) {
		return dao.listarPorUnidade(unidadePrisional);
	}

}
