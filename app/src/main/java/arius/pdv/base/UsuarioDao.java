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
		stInsertUpdate.setString("nome", entity.getNome());
		stInsertUpdate.setString("login", entity.getLogin());
		stInsertUpdate.setString("senha", entity.getSenha());
		stInsertUpdate.setInt("tipo", entity.getTipo().ordinal());	

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}	
	}
}
