package de.dassit.nupama;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.db.PostgresDatabaseType;
import com.j256.ormlite.support.ConnectionSource;

public class DbConnection {
	private static ConnectionSource connectionSource;

	public synchronized static ConnectionSource getConnectionSource() {
		if (connectionSource == null) {
			if (connectionSource == null) {
				try {
					InitialContext cxt = new InitialContext();
					DataSource dataSource = (DataSource) cxt.lookup("java:/comp/env/jdbc/DbConnection");
					connectionSource = new DataSourceConnectionSource(dataSource, new PostgresDatabaseType());
				} catch (SQLException e) {
					Logger logger = Logger.getGlobal();
					logger.severe(e.getMessage());
					return null;
				} catch (NamingException e) {
					Logger logger = Logger.getGlobal();
					logger.severe(e.getMessage());
					return null;
				}
			}
		}
		return connectionSource;
	}
}
