package io.github.site_de_eventos.sitedeeventos.model;

public class Organizador extends Usuario {
	private String contaBancaria;
	private String cnpj;
	private Evento eventoOrganizado;
	
	
	Organizador() {
	}


	public String getContaBancaria() {
		return contaBancaria;
	}


	public String getCnpj() {
		return cnpj;
	}


	public Evento getEventoOrganizado() {
		return eventoOrganizado;
	}


	public void setContaBancaria(String contaBancaria) {
		this.contaBancaria = contaBancaria;
	}


	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}


	public void setEventoOrganizado(Evento eventoOrganizado) {
		this.eventoOrganizado = eventoOrganizado;
	}
	
	

}
