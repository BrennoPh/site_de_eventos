package sitedeeventos.model;

import java.time.LocalDateTime;

public class Pedido {
	private int idPedido;
	private int idUsuario;
	private Ingresso ingresso;
	private int quantidadeIngressos;
	private double valorBase; // Sem Cupom ou Taxa de Serviço
	private double valorTotal; // Com cupom ou taxa de serviço
	private LocalDateTime dataPedido;
	private String status;
	
	
	public Pedido(int idUsuario, Ingresso ingresso, int quantidadeIngressos) {
		this.idUsuario = idUsuario;
		this.ingresso = ingresso;
		this.quantidadeIngressos = quantidadeIngressos;
	}


	public Ingresso getIdIngresso() {
		return ingresso;
	}


	public int getIdUsuario() {
		return idUsuario;
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


	public void setIdIngresso(Ingresso ingresso) {
		this.ingresso = ingresso;
	}


	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
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


	public double getValorBase() {
		return valorBase;
	}


	public void setValorBase(double valorBase) {
		this.valorBase = valorBase;
	}


	public int getIdPedido() {
		return idPedido;
	}


	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	

}
