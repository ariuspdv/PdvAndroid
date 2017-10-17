package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class HistoricoDao extends GenericDao<Historico> {
	
	@Override
	public void init(){
		tableName = "historicos";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"descricao","tipo"};
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Historico entity) throws SQLException {
		// campos direto
		entity.setId(resultSet.getInt("id"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setTipo(HistoricoTipo.values()[resultSet.getInt("tipo")]);
	}

	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Historico entity) throws SQLException {
		stInsertUpdate.setString("descricao", entity.getDescricao());
		stInsertUpdate.setInt("tipo", entity.getTipo().ordinal());

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}		
	}
	
}
