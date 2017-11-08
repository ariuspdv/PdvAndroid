package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class ConfiguracaoDao extends GenericDao<Configuracao> {
	
	@Override
	public void init(){
		tableName = "configuracoes";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"permitir_fundo_troco_zerado"};
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Configuracao entity) throws SQLException {
		// campos direto
		entity.setPermitir_fundo_troco_zerado(resultSet.getString("permitir_fundo_troco_zerado"));
	}

	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Configuracao entity) throws SQLException {
		stInsertUpdate.setString("permitir_fundo_troco_zerado", entity.getPermitir_fundo_troco_zerado());

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}		
	}
	
}
