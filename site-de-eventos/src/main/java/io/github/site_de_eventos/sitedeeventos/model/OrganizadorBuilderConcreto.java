package io.github.site_de_eventos.sitedeeventos.model;

import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;

public class OrganizadorBuilderConcreto extends UsuarioBuilderConcreto implements IOrganizadorBuilder{
	private Organizador organizador;

	public OrganizadorBuilderConcreto() {
		this.organizador = new Organizador();
	}
	
	public IOrganizadorBuilder contaBancaria(String contaBancaria) {
		this.organizador.setContaBancaria(contaBancaria);
		return this;
	}
	
	public IOrganizadorBuilder cnpj(String cnpj) {
		this.organizador.setCnpj(cnpj);
		return this;
	}
	
	public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado) {
		this.organizador.setEventoOrganizado(eventoOrganizado);
		return this;
	}
	
	public Organizador build() {
		return this.organizador;
	}
}
