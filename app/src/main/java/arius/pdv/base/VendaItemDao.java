package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class VendaItemDao extends GenericDao<VendaItem> {

	private VendaDao vendaDao;
	private ProdutoDao produtoDao;
	
	@Override
	public void init() {
		tableName = "vendas_itens";
		fields = new String[]{"venda_id","produto_id","qtde","valortotal","desconto","acrescimo"};
		
		vendaDao = AppContext.get().getDao(VendaDao.class);
		produtoDao = AppContext.get().getDao(ProdutoDao.class);
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, VendaItem entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setVenda(vendaDao.find(resultSet.getInt("venda_id")));
		entity.setProduto(produtoDao.find(resultSet.getInt("produto_id")));
		entity.setQtde(resultSet.getDouble("qtde"));
		entity.setValorTotal(resultSet.getDouble("valortotal"));
		entity.setDesconto(resultSet.getDouble("desconto"));
		entity.setAcrescimo(resultSet.getDouble("acrescimo"));
	}
	
	@Override
	protected void bindFields(VendaItem entity) throws SQLException {		
		stInsertUpdate.setInt(1, entity.getVenda().getId());			
		stInsertUpdate.setInt(2, entity.getProduto().getId());
		stInsertUpdate.setDouble(3, entity.getQtde());
		stInsertUpdate.setDouble(4, entity.getValorTotal());		
		stInsertUpdate.setDouble(5, entity.getDesconto());
		stInsertUpdate.setDouble(6, entity.getAcrescimo());		
	}
	
	@Override
	public void insert(VendaItem entity) {
		super.insert(entity);
		entity.getVenda().getItens().add(entity);
	}

}
