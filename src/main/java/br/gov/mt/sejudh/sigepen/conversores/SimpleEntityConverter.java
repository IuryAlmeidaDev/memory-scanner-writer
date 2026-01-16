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
            
            if (codigo >= 0) {  
                return String.valueOf(codigo);  
            }  
        }  
  
        return value == null ? null : value.toString();  
    }
    
    protected long addAttribute(UIComponent component, Object o) {
    	long retorno = -1;
    	
    	try {
            Method metodo = o.getClass().getDeclaredMethod("getId", new Class[] {});
            Object idObj = metodo.invoke(o, new Object[] {});
            if (idObj == null) {
                return -1;
            }
            long key = Long.parseLong(idObj.toString());
            this.getAttributesFrom(component).put(String.valueOf(key), o);
            retorno = key;
        } catch (NoSuchMethodException e) {
            // Se não tem getId, talvez seja um enum ou outra coisa, vamos tentar tratar como ordinal se for Enum
            if (o instanceof Enum) {
                long key = ((Enum<?>) o).ordinal();
                this.getAttributesFrom(component).put(String.valueOf(key), o);
                return key;
            }
            // Se não é enum e não tem getId, não adicionamos aos atributos
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return retorno;
    }  
  
    protected Map<String, Object> getAttributesFrom(UIComponent component) {  
        return component.getAttributes();  
    } 
}
