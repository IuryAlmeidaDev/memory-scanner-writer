package br.gov.mt.sejudh.sigepen.conversores;

import java.lang.reflect.Field;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.Id;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FacesConverter(value="EstadoConverter")
public class EstadoConverter implements Converter{

	private static final Logger log = LogManager.getLogger(EstadoConverter.class);


	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		
		if (value != null) {
			return component.getAttributes().get(value);
		}
		return null;
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		if (value != null && !"".equals(value)) {
			String id;
			try {
				id = this.getId(getClazz(context, component), value);
				if (id == null){
					id = "";
				}
				id = id.trim();
				component.getAttributes().put(id,
						getClazz(context, component).cast(value));
				return id;
			} catch (Exception e) {
				log.error("Erro em EstadoConverter.getAsString", e);
			}
		}
		return null;
    }
	
	private Class<?> getClazz(FacesContext facesContext, UIComponent component) {
		return component.getValueExpression("value").getType(
				facesContext.getELContext());
	}
	public String getId(Class<?> clazz, Object obj) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			if ((field.getAnnotation(Id.class)) != null) {
				Field privateField = clazz.getDeclaredField(field.getName());
				privateField.setAccessible(true);
				if (privateField.get(clazz.cast(obj)) != null) {
					return (String)field.getType()
							.cast(privateField.get(clazz.cast(obj))).toString();
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
