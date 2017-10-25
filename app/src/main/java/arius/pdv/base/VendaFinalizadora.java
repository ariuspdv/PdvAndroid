package arius.pdv.base;

import arius.pdv.core.Entity;

public class VendaFinalizadora extends Entity {

	private Venda venda;
	private Finalizadora finalizadora;
	private double valor;
	private double desconto;
	private double juro;

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public Finalizadora getFinalizadora() {
		return finalizadora;
	}

	public void setFinalizadora(Finalizadora finalizadora) {
		this.finalizadora = finalizadora;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	public double getJuro() {
		return juro;
	}

	public void setJuro(double juro) {
		this.juro = juro;
	}

	public double getValorLiquido(){
		return this.valor + this.juro - this.desconto;
	}
}
