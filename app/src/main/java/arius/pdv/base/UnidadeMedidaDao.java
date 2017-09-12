package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class UnidadeMedidaDao extends GenericDao<UnidadeMedida> {
	
	@Override
	public void init() {
		tableName = "unidades_medidas";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"descricao","sigla","fracionada"};

		super.init();		
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, UnidadeMedida entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setSigla(resultSet.getString("sigla"));
		entity.setFracionada(resultSet.getBoolean("fracionada"));				
	}

	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(UnidadeMedida entity) throws SQLException {
		stInsertUpdate.setString(1, entity.getDescricao());
		stInsertUpdate.setString(2, entity.getSigla());
		stInsertUpdate.setBoolean(3, entity.isFracionada());		
	}
		
}
