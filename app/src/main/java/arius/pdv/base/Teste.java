package arius.pdv.base;

import java.sql.SQLException;

import arius.pdv.core.UserException;

public class Teste {
	
	public static void main(String[] args) throws SQLException {

		if (PdvService.get().getPdv() == null)
			throw new UserException("PDV não cadastrado ou não está aberto");
		
		Usuario usuario = PdvService.get().getUsuarioDao().find(2);
		
		PdvService.get().fechaCaixa(usuario); 
		
	}	
}
