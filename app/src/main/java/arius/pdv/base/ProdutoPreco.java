package arius.pdv.base;

import arius.pdv.core.Entity;

public class ProdutoPreco extends Entity {
	
	private long empresa_id;
	private long produto_id;
	private double valor_venda;

	public long getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(long empresa_id) {
		this.empresa_id = empresa_id;
	}

	public long getProduto_id() {
		return produto_id;
	}

	public void setProduto_id(long produto_id) {
		this.produto_id = produto_id;
	}

	public double getValor_venda() {
		return valor_venda;
	}

	public void setValor_venda(double valor_venda) {
		this.valor_venda = valor_venda;
	}
}
