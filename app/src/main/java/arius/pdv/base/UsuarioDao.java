package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class UsuarioDao extends GenericDao<Usuario> {

	@Override
	protected void init() {
		tableName = "usuarios";
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"nome","login","senha","tipo"};
	
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Usuario entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setNome(resultSet.getString("nome"));
		entity.setSenha(resultSet.getString("senha"));
		entity.setLogin(resultSet.getString("login"));
		entity.setTipo(UsuarioTipo.values()[resultSet.getInt("tipo")]);
	}
	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Usuario entity) throws SQLException {
		stInsertUpdate.setString(1, entity.getNome());
		stInsertUpdate.setString(2, entity.getLogin());
		stInsertUpdate.setString(3, entity.getSenha());
		stInsertUpdate.setInt(4, entity.getTipo().ordinal());		
	}
}
