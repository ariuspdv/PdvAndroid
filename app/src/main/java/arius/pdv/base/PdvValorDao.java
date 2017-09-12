package arius.pdv.base;

import java.sql.SQLException;
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
		stInsertUpdate.setTimestamp(1, new Timestamp(entity.getDataHora().getTime()));
		stInsertUpdate.setInt(2, entity.getPdv().getId());
		stInsertUpdate.setInt(3, entity.getTipo().ordinal());
		stInsertUpdate.setInt(4, entity.getFinalizadora().getId());
		stInsertUpdate.setInt(5, entity.getUsuario1().getId());
		stInsertUpdate.setInt(6, entity.getUsuario2().getId());
		stInsertUpdate.setDouble(7, entity.getValor());
	}
}
