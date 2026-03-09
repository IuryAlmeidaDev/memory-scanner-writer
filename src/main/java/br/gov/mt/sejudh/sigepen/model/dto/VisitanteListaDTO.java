package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Parentesco;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Visitante;

public class VisitanteListaDTO {
	private Long codigo;
	private String visitante;
	private String cpf;
	private Date dataHoraCadastro;
	private StatusVisitante situacaoVisitante;
	private String visitacao;
	private String recuperando;
	private String parentesco;
	private String unidade;
	private StatusCredenciamento situacaoCadastro;
	private Date dataValidade;
	
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public void setVisitante(String visitante) {
		this.visitante = visitante;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public void setDataHoraCadastro(Date dataHoraCadastro) {
		this.dataHoraCadastro = dataHoraCadastro;
	}
	public void setSituacaoVisitante(String situacaoVisitante) {
		this.situacaoVisitante = StatusVisitante.valueOf(situacaoVisitante);
	}
	public void setVisitacao(String visitacao) {
		this.visitacao = visitacao;
	}
	public void setRecuperando(String recuperando) {
		this.recuperando = recuperando;
	}
	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public void setSituacaoCadastro(Integer situacaoCadastro) {
		this.situacaoCadastro = StatusCredenciamento.getValue(situacaoCadastro);
	}
	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public List<Visitante> toVisita(List<VisitanteListaDTO> visitanteDto) {
		List<Visitante> visitantes = new ArrayList<>();
		for(VisitanteListaDTO vDto:visitanteDto) {
			boolean visitanteCadastrado = false;
			
			Visitante v = new Visitante();
			for(Visitante vs : visitantes){
				if(vs.getId().equals(vDto.codigo)) {
					visitanteCadastrado = true;
					v = vs;
				}
			}
			
			if(!visitanteCadastrado) {
				v.setId(vDto.codigo);
				v.setDataHoraCadastro(vDto.dataHoraCadastro);
				v.setStatus(vDto.situacaoVisitante);
				
				Pessoa p = new Pessoa();
				p.setNome(vDto.visitante);
				
				TipoDocumento td = new TipoDocumento();
				td.setDescricao("CPF");
				
				Documento d = new Documento();
				d.setTipoDocumento(td);
				d.setNumero(vDto.cpf);
				p.setDocumentos(Arrays.asList(d));
				v.setPessoa(p);
			}
			
			Credenciamento c = new Credenciamento();
			c.setDataValidade(vDto.dataValidade);
			
			FaseDoCredenciamento fc = new FaseDoCredenciamento();
			fc.setStatusCredenciamento(vDto.situacaoCadastro);
			c.setFasesDoCredenciamento(Arrays.asList(fc));
			
			TipoVisita tv = new TipoVisita();
			tv.setDescricao(vDto.visitacao);
			c.setTipoVisita(tv);
			
			
			Pessoa p = new Pessoa();
			p = new Pessoa();
			p.setNome(vDto.recuperando);
			
			Recuperando r = new Recuperando();
			r.setPessoa(p);
			c.setRecuperando(r);
			
			Parentesco pr = new Parentesco();
			pr.setDescricao(vDto.parentesco);
			Parente parente = new Parente();
			parente.setParentesco(pr);
			c.setParente(parente);
			
			UnidadePrisional up = new UnidadePrisional();
			up.setNome(vDto.unidade);
			c.setUnidadePrisional(up);
			
			List<Credenciamento> credenciamentos;
			
			if(!visitanteCadastrado) {
				credenciamentos = new ArrayList<Credenciamento>();
			}
			v.getCredenciamentos().add(c);
			
			
			visitantes.add(v);
		}
		return visitantes;
	}
	
}
