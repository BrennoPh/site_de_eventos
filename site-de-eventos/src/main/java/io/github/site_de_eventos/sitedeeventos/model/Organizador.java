package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;

public class Organizador extends Usuario {
    @Expose
    private String contaBancaria;
    @Expose
    private String cnpj;
    
    // O campo eventoOrganizado não deve ser exposto para evitar referências circulares
    private Evento eventoOrganizado;

    public Organizador() { // Modificado para ser public
        super();
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
