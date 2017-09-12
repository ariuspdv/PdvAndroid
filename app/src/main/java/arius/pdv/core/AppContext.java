package arius.pdv.core;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import arius.pdv.db.ConnectionFactory;

public class AppContext {
	
	private static AppContext app;

	private AriusConnection connection;
	private Map<Class<GenericDao<?>>, GenericDao<?>> daos;
	
	private AppContext() {
		createConnection();
		daos = new HashMap<>();
	}	
	
	public static AppContext get() {
		if (app == null) {
			app = new AppContext();
		}
		return app;
	}

	private void createConnection() {
		try {
			connection = ConnectionFactory.createConnection();
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}
	}
	
	public AriusConnection getConnection() {
		return connection;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Entity, T extends GenericDao<E>> T getDao(Class<T> daoClass) {
		T ret = (T) daos.get(daoClass);
		if (ret == null) {
			try {
				ret = daoClass.newInstance();
				daos.put((Class<GenericDao<?>>) daoClass, ret);
				ret.init();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new ApplicationException(e);
			}
		}
		return ret;
	}

}
