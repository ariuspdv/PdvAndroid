package arius.pdv.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arius.pdv.core.Entity;

public class Venda extends Entity {

	private Pdv pdv;
	private Usuario operador;
	private VendaSituacao situacao = VendaSituacao.ABERTA;
	private Date dataHora;
	private String cpfCnpj;
	private double valorTroco;
	private List<VendaItem> itens = new ArrayList<>();
	private List<VendaFinalizadora> finalizadoras = new ArrayList<>();
	
	public Pdv getPdv() {
		return pdv;
	}

	public void setPdv(Pdv pdv) {
		this.pdv = pdv;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}

	public VendaSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(VendaSituacao situacao) {
		this.situacao = situacao;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public double getValorTroco() {
		return valorTroco;
	}

	public void setValorTroco(double valorTroco) {
		this.valorTroco = valorTroco;
	}

	public List<VendaItem> getItens() {
		return itens;
	}

	public List<VendaFinalizadora> getFinalizadoras() {
		return finalizadoras;
	}

	public double getValorLiquido() {
		double ret = 0;
		for(VendaItem vi: itens) {
			ret += vi.getValorLiquido();
		}
		return ret;
	}
	
	public double getValorPago() {
		double ret = 0;
		for(VendaFinalizadora vf: finalizadoras) {
			ret += vf.getValor();
		}
		return ret;
	}
	
	public double getValorRestante() {
		return getValorLiquido() - getValorPago() + valorTroco;
	}
	
}
