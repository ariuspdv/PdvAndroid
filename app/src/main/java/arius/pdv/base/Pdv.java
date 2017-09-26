package arius.pdv.base;

import java.util.Date;

import arius.pdv.core.Entity;

public class Pdv extends Entity {

	private int codigo_pdv;
	private boolean aberto;
	private Usuario operador; // operador ativo no momento
	private double saldoDinheiro;
	private Venda vendaAtiva;
	private Date dataAbertura;

	public int getCodigo_pdv() {
		return codigo_pdv;
	}
	
	public void setCodigo_pdv(int codigo_pdv) {
		this.codigo_pdv = codigo_pdv;
	}
	
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
	
	public Date getDataAbertura() {
		return dataAbertura;
	}
	
	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

}
