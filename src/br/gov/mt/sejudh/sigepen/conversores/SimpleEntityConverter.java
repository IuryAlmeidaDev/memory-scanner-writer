package br.gov.mt.sejudh.sigepen.conversores;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("SimpleEntityConverter")
public class SimpleEntityConverter implements Converter{

	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {  
        if (value != null) {  
            return this.getAttributesFrom(component).get(value);  
        }  
        return null;  
    }  
  
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {  
  
        if (value != null && !"".equals(value)) {  
            long codigo = this.addAttribute(component, value);
            
            if (codigo > 0) {  
                return String.valueOf(codigo);  
            }  
        }  
  
        return (String) value;  
    }
    
    protected long addAttribute(UIComponent component, Object o) {
    	long retorno = 0;
    	
    	try {
	    	Method metodo = o.getClass().getDeclaredMethod("getId", new Class[] {});
	        long key = Long.parseLong(metodo.invoke(o, new Object[] {}).toString());  
	        this.getAttributesFrom(component).put(String.valueOf(key), o);
	        retorno = key;
    	} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
    	
    	return retorno;
    }  
  
    protected Map<String, Object> getAttributesFrom(UIComponent component) {  
        return component.getAttributes();  
    } 
}
