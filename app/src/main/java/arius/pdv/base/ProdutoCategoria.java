package arius.pdv.base;

import arius.pdv.core.Entity;

public class ProdutoCategoria extends Entity {
	
	private String descricao;
	private ProdutoCategoria produtoCategoria;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ProdutoCategoria getProdutoCategoria() {
		return produtoCategoria;
	}

	public void setProdutoCategoria(ProdutoCategoria produtoCategoria) {
		this.produtoCategoria = produtoCategoria;
	}
}
