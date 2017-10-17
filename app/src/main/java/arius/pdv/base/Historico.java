package arius.pdv.base;

import arius.pdv.core.Entity;

public class Historico extends Entity {
	
	private HistoricoTipo tipo;
	private String descricao;

	public HistoricoTipo getTipo() {
		return tipo;
	}
	public void setTipo(HistoricoTipo tipo) {
		this.tipo = tipo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
