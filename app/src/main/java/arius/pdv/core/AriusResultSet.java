package arius.pdv.core;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface AriusResultSet extends AutoCloseable {

	public boolean next() throws SQLException;

	public int getInt(String fieldName) throws SQLException;

	public double getDouble(String fieldName) throws SQLException;

	public String getString(String fieldName) throws SQLException;

	public Timestamp getTimestamp(String fieldName) throws SQLException;

	public boolean getBoolean(String fieldName) throws SQLException;

	public long getLong(String fieldName) throws SQLException;

	public Object getObject(String fieldName) throws SQLException;

	@Override
	public void close() throws SQLException;
}
