package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.AppContext;
import arius.pdv.core.AriusResultSet;
import arius.pdv.core.GenericDao;

public class VendaDao extends GenericDao<Venda> {
	
	private PdvDao pdvDao;
	private UsuarioDao usuarioDao;
	private VendaItemDao vendaItemDao;
	private VendaFinalizadoraDao vendaFinalizadoraDao;

	@Override
	public void init(){
		tableName =  "vendas";
		fields = new String[]{"pdv_id","operador_id","situacao","data_hora","cpf_cnpj","valor_troco",
								"desconto","acrescimo"};
		cacheable = true;

		pdvDao = AppContext.get().getDao(PdvDao.class);
		usuarioDao = AppContext.get().getDao(UsuarioDao.class);
		vendaItemDao = AppContext.get().getDao(VendaItemDao.class);
		vendaFinalizadoraDao = AppContext.get().getDao(VendaFinalizadoraDao.class);
		super.init();
	}

	@Override
	protected void resultSetToEntity(AriusResultSet resultSet, Venda entity) throws SQLException {
		//campos diretos
		entity.setId(resultSet.getInt("id"));
		entity.setPdv(pdvDao.find(resultSet.getInt("pdv_id")));
		entity.setOperador(usuarioDao.find(resultSet.getInt("operador_id")));
		entity.setSituacao(VendaSituacao.values()[resultSet.getInt("situacao")]);
		entity.setDataHora(resultSet.getTimestamp("data_hora"));
		entity.setCpfCnpj(resultSet.getString("cpf_cnpj"));
		entity.setValorTroco(resultSet.getDouble("valor_troco"));
		entity.setDesconto(resultSet.getDouble("desconto"));
		entity.setAcrescimo(resultSet.getDouble("acrescimo"));
		
		//coleções
		atualizaItens(entity);
		atualizaFinalizadora(entity);
	}

	@Override
	protected void bindFields(Venda entity) throws SQLException {		
		if (entity.getPdv() != null) {
			stInsertUpdate.setInt("pdv_id", entity.getPdv().getId());			
		} else {
			stInsertUpdate.setObject("pdv_id", null);
		}		
		if (entity.getOperador() != null) {
			stInsertUpdate.setInt("operador_id", entity.getOperador().getId());
		} else {
			stInsertUpdate.setObject("operador_id", null);
		}		
		stInsertUpdate.setInt("situacao", entity.getSituacao().ordinal());
		stInsertUpdate.setTimestamp("data_hora", entity.getDataHora());
		if (entity.getCpfCnpj() != null) {
			stInsertUpdate.setString("cpf_cnpj", entity.getCpfCnpj());
		} else {
			stInsertUpdate.setObject("cpf_cnpj", null);
		}
		stInsertUpdate.setDouble("valor_troco", entity.getValorTroco());

		stInsertUpdate.setDouble("desconto", entity.getDesconto());

		stInsertUpdate.setDouble("acrescimo", entity.getAcrescimo());
		
		//Deixar sempre por ultimo este campo, pois é usado no momento de montar a condição para o update
		if (!stInsertUpdate.getInsert()){
			stInsertUpdate.setInt("id", entity.getId());
		}			
	}

	public void atualizaItens(Venda entity){
		entity.getItens().clear();
		entity.getItens().addAll(vendaItemDao.listDatabase("venda_id = " + entity.getId()));		
	}
	
	public void atualizaFinalizadora(Venda entity){
		entity.getFinalizadoras().clear();
		entity.getFinalizadoras().addAll(vendaFinalizadoraDao.listDatabase("venda_id = " + entity.getId()));	
	}
		
}
