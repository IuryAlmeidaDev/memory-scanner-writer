package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="HoraConverter")
public class HoraConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		
         String hora = value;
         if (hora!= null && !hora.equals(""))
              hora = value.replaceAll("\\D", "");
         return (Object) hora;
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
       
         String hora = (String) value;
         if (hora != null && !hora.equals("")){
        	 hora = hora.substring(0, 2) + ":" + hora.substring(2, 4);
         }else{
        	 hora="";
         }

         return hora;
    }

}
