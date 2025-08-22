package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;

public class Evento {
	private int idEvento;
	private int capacidade;
	private int ingressosDisponiveis;
	private String nomeEvento;
	private String local;
	private String categoria;
	private Organizador organizador;
	private String descrição;
	private double preco;
	private LocalDateTime dataEvento;
	
	Evento() {
	}
	
	
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
