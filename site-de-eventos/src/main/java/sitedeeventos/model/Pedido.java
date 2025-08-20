package sitedeeventos.model;

import java.time.LocalDateTime;

public class Pedido {
	private int idIngresso;
	private int idUsuario;
	private int idEvento;
	private int quantidadeIngressos;
	private double valorTotal;
	private LocalDateTime dataPedido;
	private String status;
	
	
	public Pedido(int idIngresso, int idUsuario, int idEvento, int quantidadeIngressos, double valorTotal,
			LocalDateTime dataPedido, String status) {
		this.idIngresso = idIngresso;
		this.idUsuario = idUsuario;
		this.idEvento = idEvento;
		this.quantidadeIngressos = quantidadeIngressos;
		this.valorTotal = valorTotal;
		this.dataPedido = dataPedido;
		this.status = status;
	}


	public int getIdIngresso() {
		return idIngresso;
	}


	public int getIdUsuario() {
		return idUsuario;
	}


	public int getIdEvento() {
		return idEvento;
	}


	public int getQuantidadeIngressos() {
		return quantidadeIngressos;
	}


	public double getValorTotal() {
		return valorTotal;
	}


	public LocalDateTime getDataPedido() {
		return dataPedido;
	}


	public String getStatus() {
		return status;
	}


	public void setIdIngresso(int idIngresso) {
		this.idIngresso = idIngresso;
	}


	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}


	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}


	public void setQuantidadeIngressos(int quantidadeIngressos) {
		this.quantidadeIngressos = quantidadeIngressos;
	}


	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}


	public void setDataPedido(LocalDateTime dataPedido) {
		this.dataPedido = dataPedido;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	

}
