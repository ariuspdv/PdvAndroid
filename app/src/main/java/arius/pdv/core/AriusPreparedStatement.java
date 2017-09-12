package arius.pdv.core;

import java.sql.SQLException;
import java.util.Date;

public interface AriusPreparedStatement extends AutoCloseable {

	public void setInt(int parameterIndex, int value) throws SQLException;

	public void setLong(int parameterIndex, long value) throws SQLException;

	public void setDouble(int parameterIndex, double value) throws SQLException;

	public void setString(int parameterIndex, String value) throws SQLException;

	public void setBoolean(int parameterIndex, boolean value) throws SQLException;

	public void setTimestamp(int parameterIndex, Date value) throws SQLException;

	public void setObject(int parameterIndex, Object value) throws SQLException;
	
	public void setInt(String parameterName, int value) throws SQLException;

	public void setLong(String parameterName, long value) throws SQLException;

	public void setDouble(String parameterName, double value) throws SQLException;

	public void setString(String parameterName, String value) throws SQLException;

	public void setBoolean(String parameterName, boolean value) throws SQLException;

	public void setTimestamp(String parameterName, Date value) throws SQLException;

	public void setObject(String parameterName, Object value) throws SQLException;	
	
	public AriusResultSet executeQuery() throws SQLException;
	
	public int executeInsert() throws SQLException;
	
	public int executeUpdate() throws SQLException;
	
	public int executeDelete() throws SQLException;
	
	public void setParametersIndexes(String...parameters);
	
	public void setInsert(Boolean value);
	
	public Boolean getInsert();
	
	@Override
	public void close() throws SQLException;

}
