package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="CpfConverter")
public class CpfConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		
         String cpf = value;
         if (cpf!= null && !cpf.equals(""))
              cpf = value.replaceAll("\\D", "");
         return (Object) cpf;
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
       
         String cpf = (String) value;
         if (cpf != null && !cpf.equals("")){
        	 cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9 ) + "-" + cpf.substring(9, 11);
         }else{
        	 cpf="";
         }

         return cpf;
    }

}
