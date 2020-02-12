package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="CepConverter")
public class CepConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        /*
         * Irá converter Cep formatado para um sem pontos, traço.
         * Ex.: 78.000-000 torna-se 78000000.
         */
         String cep = value;
         if (value!= null && !value.equals(""))
              cep = value.replaceAll("\\D", "");

         return cep;
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        /*
         * Irá converter cep não formatado para um com pontos, traço.
         * Ex.: 78000000 torna-se 78.000-000.
         */
         String cep = value.toString();
         
         if (cep != null && !cep.equals("")){        	 
        	 cep = cep.substring(0, 2) + "." + cep.substring(2, 5) + "-" + cep.substring(5, 8);
         }else{
        	 cep="";
         }

         return cep;
    }

}
