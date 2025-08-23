package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;

public class Evento {
    @Expose
    private int idEvento;
    @Expose
    private int capacidade;
    @Expose
    private int ingressosDisponiveis;
    @Expose
    private String nomeEvento;
    @Expose
    private String local;
    @Expose
    private String categoria;
    
    // Não exponha o organizador inteiro para evitar loops. Salvaremos apenas o ID.
    private Organizador organizador;
    
    @Expose
    private String descrição;
    @Expose
    private double preco;
    @Expose
    private LocalDateTime dataEvento;

    public Evento() {} // Modificado para ser public

	
	//Getters
	public int getIdEvento() {
		return idEvento;
	}
	public int getCapacidade() {
		return capacidade;
	}
	public int getIngressosDisponiveis() {
		return ingressosDisponiveis;
	}
	public String getNomeEvento() {
		return nomeEvento;
	}
	public String getLocal() {
		return local;
	}
	public String getCategoria() {
		return categoria;
	}
	public Organizador getOrganizador() {
		return organizador;
	}
	public String getDescrição() {
		return descrição;
	}
	public double getPreco() {
		return preco;
	}
	public LocalDateTime getDataEvento() {
		return dataEvento;
	}

	//Setters
	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}
	public void setIngressosDisponiveis(int ingressosDisponiveis) {
		this.ingressosDisponiveis = ingressosDisponiveis;
	}
	public void setNomeEvento(String nomeEvento) {
		this.nomeEvento = nomeEvento;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}
	public void setDescrição(String descrição) {
		this.descrição = descrição;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}
	public void setDataEvento(LocalDateTime dataEvento) {
		this.dataEvento = dataEvento;
	}


}
