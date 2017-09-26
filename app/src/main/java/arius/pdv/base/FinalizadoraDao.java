package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class FinalizadoraDao extends GenericDao<Finalizadora> {
	
	@Override
	public void init(){
		tableName = "finalizadoras";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"descricao","tipo","permitetroco","aceitasangria"};
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Finalizadora entity) throws SQLException {
		// campos direto
		entity.setId(resultSet.getInt("id"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setTipo(FinalizadoraTipo.values()[resultSet.getInt("tipo")]);
		entity.setPermiteTroco(resultSet.getBoolean("permitetroco"));
		entity.setAceitaSangria(resultSet.getBoolean("aceitasangria"));
	}

	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Finalizadora entity) throws SQLException {
		stInsertUpdate.setString("descricao", entity.getDescricao());
		stInsertUpdate.setInt("tipo", entity.getTipo().ordinal());
		stInsertUpdate.setBoolean("permitetroco", entity.isPermiteTroco());
		stInsertUpdate.setBoolean("aceitasangria", entity.isAceitaSangria());	
		
		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}		
	}
	
}
