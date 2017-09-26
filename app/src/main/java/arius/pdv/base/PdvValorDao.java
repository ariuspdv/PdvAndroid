package arius.pdv.base;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class PdvValorDao extends GenericDao<PdvValor> {

	private PdvDao pdvDao;
	private FinalizadoraDao finalizadoraDao;
	private UsuarioDao usuario1Dao;
	private UsuarioDao usuario2Dao;
	
	@Override
	public void init(){
		tableName = "pdvs_valores";
		fields = new String[]{"data_hora","pdv_id","tipo","finalizadora_id","usuario1_id","usuario2_id","valor"};
	
		pdvDao = AppContext.get().getDao(PdvDao.class);
		finalizadoraDao = AppContext.get().getDao(FinalizadoraDao.class);
		usuario1Dao = AppContext.get().getDao(UsuarioDao.class);
		usuario2Dao = AppContext.get().getDao(UsuarioDao.class);
	}
	
	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, PdvValor entity) throws SQLException {
		// campo direto
		entity.setId(resultSet.getInt("id"));
		entity.setDataHora(resultSet.getTimestamp("data_hora"));
		entity.setTipo(PdvValorTipo.values()[resultSet.getInt("tipo")]);
		entity.setPdv(pdvDao.find(resultSet.getInt("pdv_id")));
		entity.setFinalizadora(finalizadoraDao.find(resultSet.getInt("finalizadora_id")));
		entity.setUsuario1(usuario1Dao.find(resultSet.getInt("usuario1_id")));
		entity.setUsuario2(usuario2Dao.find(resultSet.getInt("usuario2_id")));
		entity.setValor(resultSet.getDouble("valor"));		
	}
	
	@Override
	public void bindFields(PdvValor entity) throws SQLException {
		stInsertUpdate.setTimestamp("data_hora", new Timestamp(entity.getDataHora().getTime()));
		stInsertUpdate.setInt("pdv_id", entity.getPdv().getId());
		stInsertUpdate.setInt("tipo", entity.getTipo().ordinal());
		stInsertUpdate.setInt("finalizadora_id", entity.getFinalizadora().getId());
		stInsertUpdate.setInt("usuario1_id", entity.getUsuario1().getId());
		if (entity.getUsuario2() == null)
			stInsertUpdate.setObject("usuario2_id", null);
		else
			stInsertUpdate.setInt("usuario2_id", entity.getUsuario2().getId());
		stInsertUpdate.setDouble("valor", entity.getValor());
		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}			
	}
}
