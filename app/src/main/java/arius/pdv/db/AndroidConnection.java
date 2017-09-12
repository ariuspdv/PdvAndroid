package arius.pdv.db;

import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import arius.pdv.core.AriusConnection;
import arius.pdv.core.AriusPreparedStatement;

public class AndroidConnection implements AriusConnection {

	private static SQLiteDatabase connection;
	
	AndroidConnection(SQLiteDatabase connection) {
		this.connection = connection;
	}

	public static SQLiteDatabase getConnection() {
		return connection;
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	@Override
	public AriusPreparedStatement prepareStatement(String sql) throws SQLException {
		return new AndroidPreparedStatement(connection.compileStatement(sql),connection);
	}

}
