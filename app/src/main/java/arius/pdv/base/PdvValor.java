package arius.pdv.base;

import java.util.Date;

import arius.pdv.core.Entity;

public class PdvValor extends Entity{
	
	private Date dataHora;
	private PdvValorTipo tipo;
	private Pdv pdv;	
	private Finalizadora finalizadora;
	private Usuario usuario1;
	private Usuario usuario2;
	private double valor;
	
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	public PdvValorTipo getTipo() {
		return tipo;
	}
	public void setTipo(PdvValorTipo tipo) {
		this.tipo = tipo;
	}
	public Pdv getPdv() {
		return pdv;
	}
	public void setPdv(Pdv pdv) {
		this.pdv = pdv;
	}
	public Finalizadora getFinalizadora() {
		return finalizadora;
	}
	public void setFinalizadora(Finalizadora finalizadora) {
		this.finalizadora = finalizadora;
	}
	public Usuario getUsuario1() {
		return usuario1;
	}
	public void setUsuario1(Usuario usuario1) {
		this.usuario1 = usuario1;
	}
	public Usuario getUsuario2() {
		return usuario2;
	}
	public void setUsuario2(Usuario usuario2) {
		this.usuario2 = usuario2;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}	

}
