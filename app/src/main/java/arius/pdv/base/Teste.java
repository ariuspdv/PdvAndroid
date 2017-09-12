package arius.pdv.base;

import arius.pdv.core.AppContext;
import arius.pdv.core.UserException;

public class Teste {
	
//	public static void main(String[] args) throws SQLException {
	public void iniciaTeste() {

		Produto produto = new Produto();
		produto.setDescricao("Produto 1");

		AppContext.get().getDao(ProdutoDao.class).insert(produto);

		produto.setDescricao("Produto 2");

		AppContext.get().getDao(ProdutoDao.class).insert(produto);

		if (PdvService.get().getPdv(1) == null)
			throw new UserException("PDV não cadastrado ou não está aberto");
		
		PdvService.get().setOperadorAtual(PdvService.get().getUsuarioDao().find(2));
		
		PdvService.get().fechaCaixa(PdvService.get().getOperadorAtual());
	}	
}
