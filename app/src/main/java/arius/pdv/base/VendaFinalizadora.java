package arius.pdv.base;

import arius.pdv.core.Entity;

public class VendaFinalizadora extends Entity {

	private Venda venda;
	private Finalizadora finalizadora;
	private double valor;

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

}
