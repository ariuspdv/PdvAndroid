package arius.pdv.base;

import arius.pdv.core.Entity;

public class Pdv extends Entity {

	private boolean aberto;
	private Usuario operador; // operador ativo no momento
	private double saldoDinheiro;
	private Venda vendaAtiva;

	public boolean isAberto() {
		return aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}

	public double getSaldoDinheiro() {
		return saldoDinheiro;
	}

	public void setSaldoDinheiro(double saldoDinheiro) {
		this.saldoDinheiro = saldoDinheiro;
	}

	public Venda getVendaAtiva() {
		return vendaAtiva;
	}

	public void setVendaAtiva(Venda vendaAtiva) {
		this.vendaAtiva = vendaAtiva;
	}

}
