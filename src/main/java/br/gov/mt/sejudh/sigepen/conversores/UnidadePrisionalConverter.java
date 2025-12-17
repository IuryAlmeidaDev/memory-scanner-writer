package br.gov.mt.sejudh.sigepen.conversores;

import javax.faces.convert.Converter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;


@FacesConverter(value="UnidadePrisionalConverter")
public class UnidadePrisionalConverter implements Converter {
	
	 @Override
	    public Object getAsObject(FacesContext contexto, UIComponent componente, String str) {
	        Object ret = null;  
	        if (componente instanceof PickList) {  
	            Object dualList = ((PickList) componente).getValue();  
	            @SuppressWarnings("rawtypes")
	            DualListModel dl = (DualListModel) dualList;  
	            for (Object o : dl.getSource()) {  
	                String id = "" + ((UnidadePrisional) o).getId();  
	                if (str.equals(id)) {  
	                    ret = o;  
	                    break;  
	                }  
	            }  
	            if (ret == null)  
	                for (Object o : dl.getTarget()) {  
	                    String id = "" + ((UnidadePrisional) o).getId();  
	                    if (str.equals(id)) {  
	                        ret = o;  
	                        break;  
	                    }  
	                }  
	        }  
	        return ret;  
	    }

	    @Override
	    public String getAsString(FacesContext contexto, UIComponent componente, Object objeto) {
	        String str = "";  
	        if (objeto instanceof UnidadePrisional) {  
	            str = "" + ((UnidadePrisional) objeto).getId();  
	        }  
	        return str;  
	    }

}
