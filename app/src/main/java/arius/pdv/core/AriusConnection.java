package arius.pdv.core;

import java.sql.SQLException;

public interface AriusConnection extends AutoCloseable {

	public AriusPreparedStatement prepareStatement(String sql) throws SQLException;

}
