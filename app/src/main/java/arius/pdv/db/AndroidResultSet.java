package arius.pdv.db;

import android.database.Cursor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import arius.pdv.core.AriusResultSet;

public class AndroidResultSet implements AriusResultSet {
	
	private Cursor resultSet;
	
	AndroidResultSet(Cursor resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public boolean next() throws SQLException {
		return resultSet.moveToNext();
	}

	@Override
	public int getInt(String fieldName) throws SQLException {
		return resultSet.getInt(resultSet.getColumnIndex(fieldName));
	}

	@Override
	public double getDouble(String fieldName) throws SQLException {
		return resultSet.getDouble(resultSet.getColumnIndex(fieldName));
	}

	@Override
	public String getString(String fieldName) throws SQLException {
		return resultSet.getString(resultSet.getColumnIndex(fieldName));
	}

	@Override
	public Timestamp getTimestamp(String fieldName) throws SQLException {
		return new Timestamp(resultSet.getLong(resultSet.getColumnIndex(fieldName)));
	}

	@Override
	public boolean getBoolean(String fieldName) throws SQLException {
		return resultSet.getInt(resultSet.getColumnIndex(fieldName)) == 1;
	}

	@Override
	public long getLong(String fieldName) throws SQLException {
		return resultSet.getLong(resultSet.getColumnIndex(fieldName));
	}

	@Override
	public Object getObject(String fieldName) throws SQLException {
		//return resultSet.getObject(resultSet.getColumnIndex(fieldName));
		return null;
	}

	@Override
	public void close() throws SQLException {
		resultSet.close();
	}

}
