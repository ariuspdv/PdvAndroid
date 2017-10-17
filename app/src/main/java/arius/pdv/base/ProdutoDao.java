package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class ProdutoDao extends GenericDao<Produto> {

	private UnidadeMedidaDao unidadeMedidaDao;
	private ProdutoCategoriaDao produtoCategoriaDao;
	
	@Override	
	public void init() {
		tableName = "produtos";
		cacheable = true;
		/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
		fields = new String[]{"codigo","descricao","descricaoreduzida","unidademedida_id","produtocategoria_id","principal"};
		
		unidadeMedidaDao = AppContext.get().getDao(UnidadeMedidaDao.class);
		produtoCategoriaDao = AppContext.get().getDao(ProdutoCategoriaDao.class);

		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Produto entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setCodigo(resultSet.getLong("codigo"));
		entity.setDescricao(resultSet.getString("descricao"));
		entity.setDescricaoReduzida(resultSet.getString("descricaoreduzida"));
		entity.setUnidadeMedida(unidadeMedidaDao.find(resultSet.getInt("unidademedida_id")));
		entity.setProdutoCategoria(produtoCategoriaDao.find(resultSet.getInt("produtocategoria_id")));
		entity.setPrincipal(resultSet.getBoolean("principal"));
	}
	
	/*Colocado apenas para Teste, depois apagar, pois esta classe n�o ir� fazer o CRUD*/
	@Override
	protected void bindFields(Produto entity) throws SQLException {
		stInsertUpdate.setLong("codigo", entity.getCodigo());
		stInsertUpdate.setString("descricao", entity.getDescricao());
		stInsertUpdate.setString("descricaoreduzida", entity.getDescricaoReduzida());
		if (entity.getUnidadeMedida() != null){
			stInsertUpdate.setInt("unidademedida_id", entity.getUnidadeMedida().getId());
		} else {
			stInsertUpdate.setObject("unidademedida_id", null);
		}
		if (entity.getProdutoCategoria() != null)
			stInsertUpdate.setInt("produtocategoria_id", entity.getProdutoCategoria().getId());
		else
			stInsertUpdate.setObject("produtocategoria_id", null);

		stInsertUpdate.setBoolean("principal",entity.getPrincipal());

		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}			
	}
}
