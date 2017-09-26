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
		fields = new String[]{"saldodinheiro","operador_id","vendaativa_id","aberto","codigo_pdv","dataabertura"};
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
		entity.setCodigo_pdv(resultSet.getInt("codigo_pdv"));
		entity.setDataAbertura(resultSet.getTimestamp("dataabertura"));
	}	
	
	@Override
	protected void bindFields(Pdv entity) throws SQLException {		
		stInsertUpdate.setDouble("saldodinheiro", entity.getSaldoDinheiro());
		
		if (entity.getOperador() == null) {
			//Neste caso o operador n�o poderia ser nulo, como iremos tratar?
			stInsertUpdate.setObject("operador_id", null);
		} else {
			stInsertUpdate.setInt("operador_id", entity.getOperador().getId());			
		}
		
		if (entity.getVendaAtiva() != null) {
			stInsertUpdate.setInt("vendaativa_id", entity.getVendaAtiva().getId());
		} else {
			stInsertUpdate.setObject("vendaativa_id", null);
		}
		
		stInsertUpdate.setBoolean("aberto", entity.isAberto());
		stInsertUpdate.setInt("codigo_pdv", entity.getCodigo_pdv());
		if (entity.getDataAbertura() != null)
			stInsertUpdate.setTimestamp("dataabertura", entity.getDataAbertura());
		else
			stInsertUpdate.setObject("dataabertura", null);
				
		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}
	}	
}
