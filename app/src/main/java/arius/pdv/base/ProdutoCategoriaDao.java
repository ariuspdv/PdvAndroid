package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class ProdutoCategoriaDao extends GenericDao<ProdutoCategoria> {

	private ProdutoCategoriaDao auxprodutoCategoriaDaoDao;

	@Override	
	public void init() {
		tableName = "produtos_categorias";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"descricao","produtocategoria_id"};

		auxprodutoCategoriaDaoDao = AppContext.get().getDao(this.getClass());

		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, ProdutoCategoria entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setProdutoCategoria(auxprodutoCategoriaDaoDao.find(resultSet.getInt("produtocategoria_id")));
	}
	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(ProdutoCategoria entity) throws SQLException {
		stInsertUpdate.setString("descricao", entity.getDescricao());
		if (entity.getProdutoCategoria() != null)
			stInsertUpdate.setInt("produtocategoria_id",entity.getProdutoCategoria().getId());
		else
			stInsertUpdate.setObject("produtocategoria_id", null);

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}			
	}
}
