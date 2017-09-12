package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class PdvDao extends GenericDao<Pdv> {

	private UsuarioDao usuarioDao;
	private VendaDao vendaDao;
	
	@Override
	protected void init() {
		tableName = "pdvs";		
		fields = new String[]{"saldodinheiro","operador_id","vendaativa_id","aberto"};
		cacheable = true;
		
		usuarioDao = AppContext.get().getDao(UsuarioDao.class);
		vendaDao = AppContext.get().getDao(VendaDao.class);
		super.init();
	}	
	
	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Pdv entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setSaldoDinheiro(resultSet.getDouble("saldodinheiro"));
		entity.setOperador(usuarioDao.find(resultSet.getInt("operador_id")));
		entity.setVendaAtiva(vendaDao.find(resultSet.getInt("vendaativa_id")));
		entity.setAberto(resultSet.getBoolean("aberto"));
	}	
	
	@Override
	protected void bindFields(Pdv entity) throws SQLException {		
		stInsertUpdate.setDouble(1, entity.getSaldoDinheiro());
		
		if (entity.getOperador() == null) {
			//Neste caso o operador n�o poderia ser nulo, como iremos tratar?
			stInsertUpdate.setObject(2, null);
		} else {
			stInsertUpdate.setInt(2, entity.getOperador().getId());			
		}
		
		if (entity.getVendaAtiva() != null) {
			stInsertUpdate.setInt(3, entity.getVendaAtiva().getId());
		} else {
			stInsertUpdate.setObject(3, null);
		}
		
		stInsertUpdate.setBoolean(4, entity.isAberto());
		
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt(5, entity.getId());
		}
	}	
}
