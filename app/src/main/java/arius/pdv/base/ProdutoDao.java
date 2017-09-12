package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class ProdutoDao extends GenericDao<Produto> {

	private UnidadeMedidaDao unidadeMedidaDao; 
	
	@Override	
	public void init() {
		tableName = "produtos";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"codigo","descricao","descricaoreduzida","unidademedida_id"};
		
		unidadeMedidaDao = AppContext.get().getDao(UnidadeMedidaDao.class);
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Produto entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setDescricaoReduzida(resultSet.getString("descricaoreduzida"));
		entity.setUnidadeMedida(unidadeMedidaDao.find(resultSet.getInt("unidademedida_id")));	
		entity.setCodigo(resultSet.getLong("codigo"));
	}
	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Produto entity) throws SQLException {
		stInsertUpdate.setLong(1, entity.getCodigo());
		stInsertUpdate.setString(2, entity.getDescricao());
		if (entity.getDescricaoReduzida() != null)
			stInsertUpdate.setString(3, entity.getDescricaoReduzida());
		else
			stInsertUpdate.setObject(3, null);
		if (entity.getUnidadeMedida() != null){
			stInsertUpdate.setInt(4, entity.getUnidadeMedida().getId());
		} else {
			stInsertUpdate.setObject(4, null);
		}

		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt(5, entity.getId());
		}
	}
}
