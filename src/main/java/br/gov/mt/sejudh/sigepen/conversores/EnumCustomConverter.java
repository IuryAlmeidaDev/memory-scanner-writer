package br.gov.mt.sejudh.sigepen.conversores;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.hibernate.usertype.DynamicParameterizedType;

@Converter(autoApply = true)
public class EnumCustomConverter implements DynamicParameterizedType, AttributeConverter<Object, Integer> {
	
	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> enumClass;

	@Override
	public Integer convertToDatabaseColumn(Object attribute) {
		Integer id = null;
		
		try {
			Method metodo = attribute.getClass().getDeclaredMethod("getId", new Class[] {});
			Object result = metodo.invoke(attribute, new Object[] {});
			id = Integer.parseInt(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}

	@Override
	public Object convertToEntityAttribute(Integer id) {
		Object result = null;
		
		try {
			Method metodo = enumClass.getDeclaredMethod("getValue", Integer.class);
			result = metodo.invoke(enumClass, new Object[] {id});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParameterValues(Properties parameters) {
		ParameterType params = (ParameterType) parameters.get( PARAMETER_TYPE );
		enumClass = params.getReturnedClass();
	}
}