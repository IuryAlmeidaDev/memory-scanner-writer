package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="convertCnpj")
public class CnpjConverter implements Converter {
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        if (value == null || value.toString().trim().isEmpty()) {
            return "";
        }

        String cnpj = value.toString().toUpperCase(); 
         
        while (cnpj.length() < 14) {
            cnpj = "0" + cnpj;
        }

        return cnpj.substring(0, 2) + "." + 
               cnpj.substring(2, 5) + "." + 
               cnpj.substring(5, 8) + "/" + 
               cnpj.substring(8, 12) + "-" + 
               cnpj.substring(12, 14);
    }
}