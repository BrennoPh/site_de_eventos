package sitedeeventos.model.builder;

import sitedeeventos.model.Organizador;
import sitedeeventos.model.Evento;

public interface IOrganizadorBuilder extends IUsuarioBuilder{
	public IOrganizadorBuilder contaBancaria(String contaBancaria);
	public IOrganizadorBuilder cnpj(String cnpj);
	public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado);
	public Organizador build();
}
