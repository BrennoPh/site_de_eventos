package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;

public class Ingresso {
    @Expose
    private String idInscricao;
    @Expose
    private int idEvento;
    @Expose
    private String nomeParticipante;
    @Expose
    private String emailParticipante;
    @Expose
    private LocalDateTime dataCompra;
    @Expose
    private double precoIngresso;

    public Ingresso(String idInscricao, int idEvento, String nomeParticipante, String emailParticipante, LocalDateTime dataCompra, double precoIngresso) {
        this.idInscricao = idInscricao;
        this.idEvento = idEvento;
        this.nomeParticipante = nomeParticipante;
        this.emailParticipante = emailParticipante;
        this.dataCompra = dataCompra;
        this.precoIngresso = precoIngresso;
    }

	public String getIdIncricao() {
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

	public void setIdIncricao(String idInscricao) {
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
