package arius.pdv.base;

import java.util.Date;

import arius.pdv.core.Entity;

public class Pdv extends Entity {

	private int codigo_pdv;
	private PdvTipo status;
	private Usuario operador; // operador ativo no momento
	private double saldoDinheiro;
	private Venda vendaAtiva;
	private Date dataAbertura;
	private Empresa empresa;

	public int getCodigo_pdv() {
		return codigo_pdv;
	}
	
	public void setCodigo_pdv(int codigo_pdv) {
		this.codigo_pdv = codigo_pdv;
	}

	public PdvTipo getStatus() {
		return status;
	}

	public void setStatus(PdvTipo status) {
		this.status = status;
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

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}
