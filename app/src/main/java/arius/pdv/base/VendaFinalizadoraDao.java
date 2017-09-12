package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class VendaFinalizadoraDao extends GenericDao<VendaFinalizadora> {

	private VendaDao vendaDao;
	private FinalizadoraDao finalizadoraDao;
	
	@Override
	public void init() {
		tableName = "vendas_finalizadoras";
		fields = new String[]{"venda_id","finalizadora_id","valor"};
		
		vendaDao = AppContext.get().getDao(VendaDao.class);
		finalizadoraDao = AppContext.get().getDao(FinalizadoraDao.class);
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, VendaFinalizadora entity) throws SQLException {
		// campo direto
		entity.setId(resultSet.getInt("id"));
		entity.setVenda(vendaDao.find(resultSet.getInt("venda_id")));
		entity.setFinalizadora(finalizadoraDao.find(resultSet.getInt("finalizadora_id")));
		entity.setValor(resultSet.getDouble("valor"));		
	}
	
	@Override
	public void bindFields(VendaFinalizadora entity) throws SQLException {
		stInsertUpdate.setInt(1, entity.getVenda().getId());
		stInsertUpdate.setInt(2, entity.getFinalizadora().getId());
		stInsertUpdate.setDouble(3, entity.getValor());
	}
}
