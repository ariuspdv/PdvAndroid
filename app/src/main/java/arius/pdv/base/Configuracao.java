package arius.pdv.base;

import arius.pdv.core.Entity;

public class Configuracao extends Entity {

	private String permitir_fundo_troco_zerado = "F";

	public void setPermitir_fundo_troco_zerado(String permitir_fundo_troco_zerado) {
		this.permitir_fundo_troco_zerado = permitir_fundo_troco_zerado;
	}

	public String getPermitir_fundo_troco_zerado() {
		return permitir_fundo_troco_zerado;
	}
}
