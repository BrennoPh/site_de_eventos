package sitedeeventos.model;

import java.time.LocalDateTime;

public class Ingresso {
	private int idInscricao;
	private int idEvento;
	private String nomeParticipante;
	private String emailParticipante;
	private LocalDateTime dataCompra;
	private double precoIngresso;
	
	public Ingresso(int idInscricao, int idEvento, String nomeParticipante, String emailParticipante, LocalDateTime dataCompra,
			double precoIngresso) {
		this.idInscricao = idInscricao;
		this.idEvento = idEvento;
		this.nomeParticipante = nomeParticipante;
		this.emailParticipante = emailParticipante;
		this.dataCompra = dataCompra;
		this.precoIngresso = precoIngresso;
	}

	public int getIdIncricao() {
		return idInscricao;
	}

	public int getIdEvento() {
		return idEvento;
	}

	public String getNomeParticipante() {
		return nomeParticipante;
	}

	public String getEmailParticipante() {
		return emailParticipante;
	}

	public LocalDateTime getDataCompra() {
		return dataCompra;
	}

	public double getPrecoIngresso() {
		return precoIngresso;
	}

	public void setIdIncricao(int idInscricao) {
		this.idInscricao = idInscricao;
	}

	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}

	public void setNomeParticipante(String nomeParticipante) {
		this.nomeParticipante = nomeParticipante;
	}

	public void setEmailParticipante(String emailParticipante) {
		this.emailParticipante = emailParticipante;
	}

	public void setDataCompra(LocalDateTime dataCompra) {
		this.dataCompra = dataCompra;
	}

	public void setPrecoIngresso(double precoIngresso) {
		this.precoIngresso = precoIngresso;
	}
	
	
	
	
}
