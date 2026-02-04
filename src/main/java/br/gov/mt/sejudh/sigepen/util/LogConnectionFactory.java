package br.gov.mt.sejudh.sigepen.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class LogConnectionFactory {

    public static Connection getConnection() throws SQLException {
        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup("java:/sigepen");
            return dataSource.getConnection();
        } catch (NamingException e) {
            throw new SQLException("Erro ao buscar JNDI java:/sigepen para o Log4j2", e);
        }
    }
}
