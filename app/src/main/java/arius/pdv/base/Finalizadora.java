package arius.pdv.base;

import arius.pdv.core.Entity;

public class Finalizadora extends Entity {
	
	private FinalizadoraTipo tipo;
	private String descricao;
	private boolean permiteTroco;
	private boolean aceitaSangria;
	
	public FinalizadoraTipo getTipo() {
		return tipo;
	}
	public void setTipo(FinalizadoraTipo tipo) {
		this.tipo = tipo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public boolean isPermiteTroco() {
		return permiteTroco;
	}
	public void setPermiteTroco(boolean permiteTroco) {
		this.permiteTroco = permiteTroco;
	}
	public boolean isAceitaSangria() {
		return aceitaSangria;
	}
	public void setAceitaSangria(boolean aceitaSangria) {
		this.aceitaSangria = aceitaSangria;
	}

}
