package arius.pdv.base;

import arius.pdv.core.Entity;

public class Usuario extends Entity {
	
	private String nome;
	private String login;
	private String senha;
	private UsuarioTipo tipo;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public UsuarioTipo getTipo() {
		return tipo;
	}
	public void setTipo(UsuarioTipo tipo) {
		this.tipo = tipo;
	}		

}
