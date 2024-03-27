package br.gov.mt.sejudh.sigepen.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class ModelUtil {

	public static Boolean isMenorDeIdade(Date dataNascimento){
		if (dataNascimento == null)
			return false;
        
		LocalDate localDate = Instant.ofEpochMilli(dataNascimento.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();	
		return (Period.between(localDate, LocalDate.now()).getYears()) < 18;
	}
	
	public static Boolean isUnderSeven(Date dataNascimento){
		if (dataNascimento == null)
			return false;
        
		LocalDate localDate = Instant.ofEpochMilli(dataNascimento.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();	
		return (Period.between(localDate, LocalDate.now()).getYears()) < 7;
	}
	
}
