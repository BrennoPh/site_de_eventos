package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    @Expose
    private int idPedido;
    @Expose
    private Evento evento;
    @Expose
    private int quantidadeIngressos;
    @Expose
    private double valorBase;
    @Expose
    private double valorTotal;
    @Expose
    private LocalDateTime dataPedido;
    @Expose
    private String status;
    @Expose
    private List<Ingresso> ingressos;

    // IMPORTANTE: O campo 'usuario' não deve ter @Expose para quebrar o loop infinito.
    private Usuario usuario;

    // Construtor principal
    public Pedido(Usuario usuario, Evento evento, int quantidade) {
        this.usuario = usuario;
        this.evento = evento;
        this.quantidadeIngressos = quantidade;
        this.dataPedido = LocalDateTime.now();
        this.status = "PENDENTE";
    }
    
    public Pedido() {} // Construtor padrão para o Gson
    
    
	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public List<Ingresso> getIngressos() {
		return ingressos;
	}

	public void setIngressos(List<Ingresso> ingressos) {
		this.ingressos = ingressos;
	}

	public int getQuantidadeIngressos() {
		return quantidadeIngressos;
	}

	public void setQuantidadeIngressos(int quantidadeIngressos) {
		this.quantidadeIngressos = quantidadeIngressos;
	}

	public double getValorBase() {
		return valorBase;
	}

	public void setValorBase(double valorBase) {
		this.valorBase = valorBase;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public LocalDateTime getDataPedido() {
		return dataPedido;
	}

	public void setDataPedido(LocalDateTime dataPedido) {
		this.dataPedido = dataPedido;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
