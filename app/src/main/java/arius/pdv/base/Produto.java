package arius.pdv.base;

import arius.pdv.core.Entity;

public class Produto extends Entity {
	
	private long codigo; //podendo ser barras / interno
	private String descricao;
	private String descricaoReduzida;
	private UnidadeMedida unidadeMedida;
	public long getCodigo() {
		return codigo;
	}
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricaoReduzida() {
		return descricaoReduzida;
	}
	public void setDescricaoReduzida(String descricaoReduzida) {
		this.descricaoReduzida = descricaoReduzida;
	}
	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

}
