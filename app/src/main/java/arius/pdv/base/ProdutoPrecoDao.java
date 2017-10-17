package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class ProdutoPrecoDao extends GenericDao<ProdutoPreco> {
	
	@Override	
	public void init() {
		tableName = "produtos_precos";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"empresa_id","produto_id","valor_venda"};
		
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, ProdutoPreco entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setValor_venda(resultSet.getDouble("valor_venda"));
	}
	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(ProdutoPreco entity) throws SQLException {
		stInsertUpdate.setDouble("valor_venda", entity.getValor_venda());

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}			
	}
}
