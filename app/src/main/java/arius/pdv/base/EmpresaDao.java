package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class EmpresaDao extends GenericDao<Empresa> {
	
	@Override
	public void init(){
		tableName = "empresas";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"razao_social","nome_fantasia"};
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Empresa entity) throws SQLException {
		// campos direto
		entity.setId(resultSet.getInt("id"));
		entity.setRazao_social(resultSet.getString("razao_social"));
		entity.setNome_fantasia(resultSet.getString("nome_fantasia"));
	}

	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Empresa entity) throws SQLException {
		stInsertUpdate.setString("razao_social", entity.getRazao_social());
		stInsertUpdate.setString("nome_fantasia", entity.getNome_fantasia());

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}		
	}
	
}
