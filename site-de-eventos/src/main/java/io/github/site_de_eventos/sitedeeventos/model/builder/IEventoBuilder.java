package io.github.site_de_eventos.sitedeeventos.model.builder;

import java.time.LocalDateTime;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;


public interface IEventoBuilder {

	public IEventoBuilder idEvento(int idEvento);
	public IEventoBuilder capacidade(int capacidade);
	public IEventoBuilder ingressosDisponiveis(int ingressosDisponiveis);
	public IEventoBuilder nomeEvento(String nomeEvento);
	public IEventoBuilder local(String local);
	public IEventoBuilder categoria(String categoria);
	public IEventoBuilder organizador(Organizador organizador);
	public IEventoBuilder descricao(String descricao);
	public IEventoBuilder preco(double preco);
	public IEventoBuilder dataEvento(LocalDateTime dataEvento);
	public Evento build();
	
}
