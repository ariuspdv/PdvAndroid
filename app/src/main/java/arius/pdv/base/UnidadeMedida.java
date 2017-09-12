package arius.pdv.base;

import arius.pdv.core.Entity;

public class UnidadeMedida extends Entity {

	private String descricao;
	private String sigla;
	private boolean fracionada;
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public boolean isFracionada() {
		return fracionada;
	}
	public void setFracionada(boolean fracionada) {
		this.fracionada = fracionada;
	}
	
	

}
