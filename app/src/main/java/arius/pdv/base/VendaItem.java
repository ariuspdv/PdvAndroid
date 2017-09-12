package arius.pdv.base;

import arius.pdv.core.Entity;

public class VendaItem extends Entity {

	private Venda venda;
	private Produto produto;
	private double qtde;
	private double valorTotal;
	private double desconto; //sobre total
	private double acrescimo; //sobre total

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public double getQtde() {
		return qtde;
	}

	public void setQtde(double qtde) {
		this.qtde = qtde;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	public double getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(double acrescimo) {
		this.acrescimo = acrescimo;
	}
	
	public double getValorLiquido() {
		return valorTotal - desconto + acrescimo;
	}
	
	public double getValorUnitario() {
		if (qtde == 0) {
			return 0;
		} else {
			return valorTotal / qtde;
		}
	}

}
