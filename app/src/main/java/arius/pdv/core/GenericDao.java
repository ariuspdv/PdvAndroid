package arius.pdv.core;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arius.pdv.db.ConnectionFactory;

public abstract class GenericDao<T extends Entity> {
	
	private AriusConnection conn;
	private Class<T> entityClass;
	private Map<Integer, T> cache = new HashMap<>();

	protected AriusPreparedStatement stInsertUpdate;
	private AriusPreparedStatement stDelete;
	private AriusPreparedStatement stFind;
	
	protected String tableName; //definir no construtor
	protected String[] fields; //definir no construtor
	protected boolean cacheable = false; //definir no construtor
		
	//cache do último objeto
	private int lastId;
	private T lastEntity;	
	
	abstract protected void resultSetToEntity(AriusResultSet resultSet, T entity) throws SQLException;

	@SuppressWarnings("unchecked")
	public GenericDao() {
		this.conn = AppContext.get().getConnection();
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected void init(){
		carregaChache();
	}
	
	public void carregaChache(){
		if (cacheable){
			String sql = "select * from " + tableName ;
			try {
				AriusPreparedStatement stList = conn.prepareStatement(sql);
				AriusResultSet rs = stList.executeQuery();
				while (rs.next()) {
					T e = entityClass.newInstance();
					int id = rs.getInt("id");
					cache.put(id, e);
					resultSetToEntity(rs, e);
				}
				
				stList.close();
			} catch (SQLException | InstantiationException | IllegalAccessException e) {
				throw new ApplicationException(e);
			}						
		}	
	}
	
	public void close() {
		try {
			if (stInsertUpdate != null) {
				stInsertUpdate.close();
			}
			if (stDelete != null) {
				stDelete.close();
			}
			if (stFind != null) {
				stFind.close();
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void bindFields(T entity) throws SQLException {
		//sobreescrever se a entidade tiver suporte a alteração
		throw new ApplicationException("Operação não suportada para a tabela: " + tableName);
	}

	private void prepareInsert() throws SQLException {
		if (stInsertUpdate == null || !stInsertUpdate.getInsert()) {
			String fieldsComma = "";
			String params = "";
			for(int i = 0; i < fields.length; i++) {
				if (i > 0){
					fieldsComma += ",";
					params += ",";
				}	
				fieldsComma += fields[i];
				params += "?";
			}
			
			String sql = "insert into " + tableName + " (" + fieldsComma + ") values (" + params + ")";
			if (ConnectionFactory.DB_TYPE == DbType.POSTGRES) {
				sql += " returning id";
			}
			stInsertUpdate = conn.prepareStatement(sql);
			stInsertUpdate.setParametersIndexes(fields);
		}
		if (stInsertUpdate != null)
			stInsertUpdate.setInsert(true);
	}
	
	private void prepareUpdate() throws SQLException {
		if (stInsertUpdate == null || stInsertUpdate.getInsert()) {
			String set = null;
			for(String f: fields) {
				if (set == null) {
					set = f + "=?";
				} else {
					set += ", " + f + "=?";					
				}
			}
			String sql = "update " + tableName + " set " + set + " where id = ?";
			
			stInsertUpdate = conn.prepareStatement(sql);
			String[] parameters = new String[fields.length + 1];
			System.arraycopy(fields, 0, parameters, 0, fields.length);
			parameters[parameters.length - 1] = "id";
			stInsertUpdate.setParametersIndexes(parameters);
		}
		if (stInsertUpdate != null)
			stInsertUpdate.setInsert(false);
	}
		
	private void prepareDelete() throws SQLException {
		if (stDelete == null) {
			String sql = "delete from " + tableName + " where id = ?";
			stDelete = conn.prepareStatement(sql);
		}
		if (stDelete != null)
			stDelete.setInsert(false);
	}	

	private void prepareFind(int id) throws SQLException {
		//if (stFind == null) {
			String sql = "select * from " + tableName + " where id = " + id;
			stFind = conn.prepareStatement(sql);
		//}
	}
		
	public void insert(T entity) {
		try {
			prepareInsert();
			bindFields(entity);
			entity.setId(stInsertUpdate.executeInsert());

			//adiciona no cache
			if (cacheable) {
				cache.put(entity.getId(), entity);
			}
		} catch (SQLException e) {
			throw new UserException(e);
		}
	}
	
	public void update(T entity) {
		try {
			prepareUpdate();
			bindFields(entity);
			stInsertUpdate.executeUpdate();
		} catch (SQLException e) {
			throw new UserException(e);
		}		
	}
	
	public void delete(T entity) {
		try {
			prepareDelete();
			stDelete.setInt(1, entity.getId());
			stDelete.executeDelete();
			
			//remove do cache
			if (cacheable) {
				cache.remove(entity.getId());
			}
		} catch (SQLException e) {
			throw new UserException(e);
		}
	}

	private T findDatabase(int id) {
		try {
			if (id != lastId) {
				prepareFind(id);
				//stFind.setInt(1, id);
				AriusResultSet rs =	stFind.executeQuery();
				T ret = null;
				if (rs.next()) {
					ret = entityClass.newInstance();
					
					//adiciona no cache
					if (cacheable) {
						cache.put(id, ret);
					}
					
					resultSetToEntity(rs, ret);					
				}
				rs.close();
				
				//salva no último cache
				if (ret != null){
					lastId = id;
					lastEntity = ret;
				}
				return ret;
			} else {
				return lastEntity;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}
	}
	
	public T find(int id) {
		if (cacheable) {
			return cache.get(id);
		} else {
			return findDatabase(id);
		}
	}

	public List<T> listDatabase(String filter) {
		if (cacheable) {
			throw new ApplicationException("Tabela possui cache interno, utilizar listCache para: " + tableName);
		}
		
		String sql = "select * from " + tableName + " where " + filter;
		try {
			AriusPreparedStatement stList = conn.prepareStatement(sql);
			AriusResultSet rs = stList.executeQuery();
			List<T> ret = new ArrayList<>();
			while (rs.next()) {
				T entity = entityClass.newInstance();
				resultSetToEntity(rs, entity);
				ret.add(entity);
			}
			stList.close();
			return ret;
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new ApplicationException(e);
		}				
	}

	public List<T> listCache(FuncionaisFilters<T> filter) {
		if (!cacheable) {
			throw new ApplicationException("Tabela não possui cache interno, utilizar listDatabase para: " + tableName);
		}

		List<T> ret = new ArrayList<>();
		for(T e: cache.values()) {
			if (filter.test(e)) {
				ret.add(e);
			}
		}
		
		return ret;
	}

}
