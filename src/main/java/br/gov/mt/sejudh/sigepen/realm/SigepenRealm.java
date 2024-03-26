package br.gov.mt.sejudh.sigepen.realm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;

import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class SigepenRealm extends JdbcRealm {

	protected String jndiDataSourceName;

	public SigepenRealm() {
		super.setSaltStyle(SaltStyle.COLUMN);
	}

	public String getJndiDataSourceName() {
		return jndiDataSourceName;
	}

	public void setJndiDataSourceName(String jndiDataSourceName) {
		this.jndiDataSourceName = jndiDataSourceName;
		this.dataSource = getDataSourceFromJNDI(jndiDataSourceName);
	}

	private DataSource getDataSourceFromJNDI(String jndiDataSourceName) {
		try {
			InitialContext ic = new InitialContext();
			return (DataSource) ic.lookup(jndiDataSourceName);
		} catch (NamingException e) {
			throw new AuthorizationException(e);
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken userToken = (UsernamePasswordToken)token;
		String usuario = userToken.getUsername();
		
		if (usuario == null) {
			return null;
		}
		
		UsuarioSenhaSalt uss = getSenhaSalt(usuario);

		if (uss == null) {
			return null;
		}
		
		SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(usuario, uss.getSenha(), getName());
		sai.setCredentialsSalt(ByteSource.Util.bytes(Base64.decode(uss.getSalt())));
		
		return sai;
	}
	
	@Override
	protected Set<String> getRoleNamesForUser(Connection conn, String username) throws SQLException {

		PreparedStatement ps = null;  
        ResultSet rs = null;  
        Set<String> roleNames = new LinkedHashSet<String>();  
  
        try {  
        	UnidadePrisional unidade = (UnidadePrisional) SecurityUtil.getUnidadeSelecionada(); 
        	
        	if (unidade == null && SecurityUtil.getUsuario().getId() > 0) {
        		return roleNames;
        	} else if (SecurityUtil.getUsuario().getId() == 0) {
        		unidade = new UnidadePrisional();
        	}
        	
        	String idUnidade = String.valueOf(unidade.getId());
        	
            ps = conn.prepareStatement(userRolesQuery);  
            ps.setString(1, username);  
            ps.setString(2, idUnidade);
  
            rs = ps.executeQuery();
  
            while (rs.next()) {  
  
                String roleName = rs.getString(1);  

                if (roleName != null) {  
                    roleNames.add(roleName);  
                }
            }  
        } finally {  
            JdbcUtils.closeResultSet(rs);  
            JdbcUtils.closeStatement(ps);  
        }  
  
        return roleNames;  
    }

	private UsuarioSenhaSalt getSenhaSalt(String usuario) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection conn = null;		
		
		try {
			conn = dataSource.getConnection();
			statement = conn.prepareStatement(authenticationQuery);
			statement.setString(1, usuario);

			resultSet = statement.executeQuery();

			boolean possuiRegistro = resultSet.next();
			if (!possuiRegistro)
				return null;

			String salt = null;
			String password = resultSet.getString(1);
			if (resultSet.getMetaData().getColumnCount() > 1)
				salt = resultSet.getString(2);

			resultSet.close();
			statement.close();
			
			statement = conn.prepareStatement("SELECT U.ID FROM " + Sigepen.SEJUDH_SCHEMA + ".USUARIO U WHERE U.LOGIN = ? AND U.STATUS = 1");
			statement.setString(1, usuario);
			
			resultSet = statement.executeQuery();
			
			if (!resultSet.next()) {
				throw new AuthenticationException("O usuário está inativo, contate o administrador do sistema.");
			}
			
			resultSet.close();
			statement.close();
			
			statement = conn.prepareStatement("SELECT U.ID FROM " + Sigepen.SEJUDH_SCHEMA + ".USUARIO U WHERE U.LOGIN = ? AND U.STATUS = 1 AND U.DATA_EXPIRACAO > TRUNC(SYSDATE)");
			statement.setString(1, usuario);
			
			resultSet = statement.executeQuery();
			
			if (!resultSet.next()) {
				throw new AuthenticationException("Acesso expirado. Envie e-mail corporativo ou pessoal para sigepen@sesp.mt.gov.br e aguarde!");
			}
			
			return new UsuarioSenhaSalt(password, salt);
		} catch (SQLException e) {
			e.printStackTrace();

			throw new AuthenticationException(e.getMessage(), e);
						
			
		} finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(statement);
			JdbcUtils.closeConnection(conn);
		}
	}

	@Override
	protected Set<String> getPermissions(Connection conn, String username, Collection<String> roleNames) throws SQLException {
		PreparedStatement ps = null;
		Set<String> permissions = new LinkedHashSet<String>();
		
        try {
        	ps = conn.prepareStatement(permissionsQuery);
        	
        	for (String roleName : roleNames) { 
        		ps.setString(1, roleName);
        		ResultSet rs = null;
 
                try {
                	rs = ps.executeQuery();
                	
                	while (rs.next()) {
                		String permissionString = rs.getString(1);
                		permissions.add(permissionString);
                     }
                 } finally {
                     JdbcUtils.closeResultSet(rs);
                 } 
             }
         } finally {
             JdbcUtils.closeStatement(ps);
         }
 
         return permissions;
	}
}
