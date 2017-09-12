package arius.pdv.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import arius.pdv.core.ApplicationException;
import arius.pdv.core.AriusPreparedStatement;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.DbType;

public class AndroidPreparedStatement implements AriusPreparedStatement {
	
	private SQLiteStatement preparedStatement;
	private SQLiteDatabase connection;
	private String[] parameters;
	private Boolean insert;
	
	AndroidPreparedStatement(SQLiteStatement preparedStatement, SQLiteDatabase connection) {
		this.preparedStatement = preparedStatement;
		this.connection = connection;
	}

	@Override
	public void close() throws SQLException {
		preparedStatement.close();
	}

	@Override
	public void setInt(int parameterIndex, int value) throws SQLException {
		preparedStatement.bindLong(parameterIndex, value);
	}

	@Override
	public void setLong(int parameterIndex, long value) throws SQLException {
		preparedStatement.bindLong(parameterIndex, value);
	}

	@Override
	public void setDouble(int parameterIndex, double value) throws SQLException {
		preparedStatement.bindDouble(parameterIndex, value);
	}

	@Override
	public void setString(int parameterIndex, String value) throws SQLException {
		preparedStatement.bindString(parameterIndex, value);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean value) throws SQLException {
		if (value)
			preparedStatement.bindLong(parameterIndex, 1);
		else
			preparedStatement.bindLong(parameterIndex, 0);
	}

	@Override
	public void setTimestamp(int parameterIndex, Date value) throws SQLException {
		Timestamp ts = null;
		if (value != null) {
			ts = new Timestamp(value.getTime());
		}
		preparedStatement.bindLong(parameterIndex, ts.getTime());
	}

	@Override
	public void setObject(int parameterIndex, Object value) throws SQLException {
		if (value == null)
			preparedStatement.bindNull(parameterIndex);
	}

	@Override
	public AriusResultSet executeQuery() throws SQLException {
		//Usado o replace abaixom, pois no android os comnados são separados, diferentes do Jdbc;
		String cmdExecute = preparedStatement.toString().replaceAll("SQLiteProgram:","");
		return new AndroidResultSet(connection.rawQuery(cmdExecute,null));
	}
	
	@Override
	public int executeInsert() throws SQLException {
		Cursor rs = null;
		preparedStatement.executeInsert();

		if (ConnectionFactory.DB_TYPE == DbType.SQLITE){
			rs = connection.rawQuery("select last_insert_rowid()", null);
			rs.moveToFirst();
		}
		return  rs.getInt(0);
	}

	@Override
	public int executeUpdate() throws SQLException {
		return preparedStatement.executeUpdateDelete();
	}

	@Override
	public int executeDelete() throws SQLException {
		return preparedStatement.executeUpdateDelete();
	}

	private int getParameterIndex(String parameterName) {
		if (parameters == null) {
			throw new ApplicationException("Faltando definir os parâmetros do PreparedStatement.");
		}
		for(int i = 0; i < parameters.length; i++) {
			if (parameters[i].equals(parameterName)) {
				return i + 1;
			}
		}
		throw new ApplicationException("Parâmetro não definido: " + parameterName);		
	}
	
	@Override
	public void setInt(String parameterName, int value) throws SQLException {
		preparedStatement.bindLong(getParameterIndex(parameterName), value);
	}

	@Override
	public void setLong(String parameterName, long value) throws SQLException {
		preparedStatement.bindLong(getParameterIndex(parameterName), value);
	}

	@Override
	public void setDouble(String parameterName, double value) throws SQLException {
		preparedStatement.bindDouble(getParameterIndex(parameterName), value);
	}

	@Override
	public void setString(String parameterName, String value) throws SQLException {
		preparedStatement.bindString(getParameterIndex(parameterName), value);
	}

	@Override
	public void setBoolean(String parameterName, boolean value) throws SQLException {
		if (value)
			preparedStatement.bindLong(getParameterIndex(parameterName), 1);
		else
			preparedStatement.bindLong(getParameterIndex(parameterName), 0);
	}

	@Override
	public void setTimestamp(String parameterName, Date value) throws SQLException {
		setTimestamp(getParameterIndex(parameterName), value);
	}

	@Override
	public void setObject(String parameterName, Object value) throws SQLException {
		if (value == null)
			preparedStatement.bindNull(getParameterIndex(parameterName));
	}

	@Override
	public void setParametersIndexes(String...parameters) {
		this.parameters = parameters;
	}

	@Override
	public void setInsert(Boolean value) {
		this.insert = value;
	}

	@Override
	public Boolean getInsert() {
		return insert;
	}

}
