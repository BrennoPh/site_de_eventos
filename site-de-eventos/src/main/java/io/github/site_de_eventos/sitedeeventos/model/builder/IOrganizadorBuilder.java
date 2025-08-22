package io.github.site_de_eventos.sitedeeventos.model.builder;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;

public interface IOrganizadorBuilder extends IUsuarioBuilder{
	public IOrganizadorBuilder contaBancaria(String contaBancaria);
	public IOrganizadorBuilder cnpj(String cnpj);
	public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado);
	public Organizador build();
}
