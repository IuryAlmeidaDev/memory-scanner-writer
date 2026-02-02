package br.gov.mt.sejudh.sigepen.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import br.gov.mt.sejudh.sigepen.model.Visitante;

public class VisitanteListener {

	@PrePersist
	@PreUpdate
	public void validarMaioridade(Visitante visitante) {
	    if (visitante.getPessoa() != null && visitante.getPessoa().getDataNascimento() != null) {
	        
	        java.util.Date dtNasc = visitante.getPessoa().getDataNascimento();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(dtNasc);
	        
	        LocalDate nascimento = LocalDate.of(
	            cal.get(Calendar.YEAR),
	            cal.get(Calendar.MONTH) + 1, 
	            cal.get(Calendar.DAY_OF_MONTH)
	        );
	        
	        int idade = Period.between(nascimento, LocalDate.now()).getYears();

	        if (idade >= 18) {
	            visitante.setResponsavel(null);
	            visitante.setEmancipado(null);
	            visitante.setDataHorarioFimResponsavel(new java.util.Date());
	            if (visitante.getVisitantesReponsaveis() != null) {
	                visitante.getVisitantesReponsaveis().clear();
	            }
	        }
	    }
	}
}