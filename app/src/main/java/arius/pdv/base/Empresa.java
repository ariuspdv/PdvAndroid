package arius.pdv.base;

import arius.pdv.core.Entity;

public class Empresa extends Entity {

	private String razao_social;
	private String nome_fantasia;

	public String getRazao_social() {
		return razao_social;
	}

	public void setRazao_social(String razao_social) {
		this.razao_social = razao_social;
	}

	public String getNome_fantasia() {
		return nome_fantasia;
	}

	public void setNome_fantasia(String nome_fantasia) {
		this.nome_fantasia = nome_fantasia;
	}
}
