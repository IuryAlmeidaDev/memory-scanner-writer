package br.gov.mt.sejudh.sigepen.conversores;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumCustomType implements DynamicParameterizedType, UserType {

	private static final Logger log = LogManager.getLogger(EnumCustomType.class);

	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> enumClass;

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	@Override
	public int hashCode(Object value) throws HibernateException {
		return value.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner) throws HibernateException,
			SQLException {
		String id = rs.getString(names[0]);
		Object result = null;
		
		if (!rs.wasNull()) {
			result = setEnumValue(Integer.parseInt(id));
		}
		
		return result;
	}

	@Override
	public void nullSafeSet(PreparedStatement stmt, Object value, int index,
			SessionImplementor impl) throws HibernateException, SQLException {
		if (value == null) {
			stmt.setNull(index, Types.INTEGER);
		} else {
			stmt.setInt(index, getEnumValue(value));
		}
		
	}
	
	private Object setEnumValue(Integer id) {
		Object result = null;
		
		try {
			Method metodo = enumClass.getDeclaredMethod("getValue", Integer.class);
			result = metodo.invoke(enumClass, new Object[] {id});
		} catch (Exception e) {
			log.error("Erro em EnumCustomType (reflection)", e);
		}
		
		return result;
	}
	
	private Integer getEnumValue(Object en) {
		Integer id = null;
		
		try {
			Method metodo = en.getClass().getDeclaredMethod("getId", new Class[] {});
			Object result = metodo.invoke(en, new Object[] {});
			id = Integer.parseInt(result.toString());
		} catch (Exception e) {
			log.error("Erro em EnumCustomType (reflection)", e);
		}
		
		return id;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return deepCopy(original);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends Enum> returnedClass() {
		return enumClass;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.INTEGER };
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParameterValues(Properties parameters) {
		ParameterType params = (ParameterType) parameters.get( PARAMETER_TYPE );
		enumClass = params.getReturnedClass();
		
	}
}
