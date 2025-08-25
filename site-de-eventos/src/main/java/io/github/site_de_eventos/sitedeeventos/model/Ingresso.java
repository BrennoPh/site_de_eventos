package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;

/**
 * Representa a entidade Ingresso no sistema.
 * <p>
 * Esta classe armazena as informações de um ingresso individual, que é
 * gerado a partir de um {@link Pedido} e pertence a um participante específico.
 * <p>
 * Os campos anotados com {@code @Expose} são para a conversão do objeto para 
 * o formato JSON e vice-versa.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class Ingresso {
    /**
     * Identificador único da inscrição/ingresso.
     */
    @Expose
    private String idInscricao;
    /**
     * ID do evento ao qual o ingresso pertence.
     */
    @Expose
    private int idEvento;
    /**
     * Nome do participante titular do ingresso.
     */
    @Expose
    private String nomeParticipante;
    /**
     * E-mail do participante titular do ingresso.
     */
    @Expose
    private String emailParticipante;
    /**
     * Data e hora da compra do ingresso.
     */
    @Expose
    private LocalDateTime dataCompra;
    /**
     * Preço final pago pelo ingresso.
     */
    @Expose
    private double precoIngresso;
    
    /**
     * Referência de volta para o Pedido ao qual este ingresso pertence.
     * Ajuda a navegar da entidade filha (Ingresso) para a entidade pai (Pedido).
     */
    private Pedido pedido;

    /**
     * Construtor completo para a criação de um objeto Ingresso.
     * @param idInscricao (String) O ID único da inscrição.
     * @param idEvento (int) O ID do evento.
     * @param nomeParticipante (String) O nome do participante.
     * @param emailParticipante (String) O e-mail do participante.
     * @param dataCompra (LocalDateTime) A data da compra.
     * @param precoIngresso (double) O preço pago pelo ingresso.
     */
    public Ingresso(String idInscricao, int idEvento, String nomeParticipante, String emailParticipante, LocalDateTime dataCompra, double precoIngresso) {
        this.idInscricao = idInscricao;
        this.idEvento = idEvento;
        this.nomeParticipante = nomeParticipante;
        this.emailParticipante = emailParticipante;
        this.dataCompra = dataCompra;
        this.precoIngresso = precoIngresso;
    }

    /**
     * Obtém o ID da inscrição.
     * @return O ID (String) da inscrição.
     */
    public String getIdIncricao() {
        return idInscricao;
    }

    /**
     * Obtém o ID do evento.
     * @return O ID (int) do evento.
     */
    public int getIdEvento() {
        return idEvento;
    }

    /**
     * Obtém o nome do participante.
     * @return O nome (String) do participante.
     */
    public String getNomeParticipante() {
        return nomeParticipante;
    }

    /**
     * Obtém o e-mail do participante.
     * @return O e-mail (String) do participante.
     */
    public String getEmailParticipante() {
        return emailParticipante;
    }

    /**
     * Obtém a data da compra.
     * @return A data da compra (LocalDateTime).
     */
    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    /**
     * Obtém o preço do ingresso.
     * @return O preço (double) do ingresso.
     */
    public double getPrecoIngresso() {
        return precoIngresso;
    }
    
    /**
     * Obtém o objeto Pedido ao qual este ingresso está associado.
     * @return O objeto (Pedido) pai.
     */
    public Pedido getPedido() {
        return pedido;
    }
    
    // --- Setters ---

    /**
     * Define o objeto Pedido pai para este ingresso.
     * @param pedido (Pedido) O objeto Pedido a ser associado.
     */
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Define o ID da inscrição.
     * @param idInscricao (String) O novo ID da inscrição.
     */
    public void setIdIncricao(String idInscricao) {
        this.idInscricao = idInscricao;
    }

    /**
     * Define o ID do evento.
     * @param idEvento (int) O novo ID do evento.
     */
    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    /**
     * Define o nome do participante.
     * @param nomeParticipante (String) O novo nome do participante.
     */
    public void setNomeParticipante(String nomeParticipante) {
        this.nomeParticipante = nomeParticipante;
    }

    /**
     * Define o e-mail do participante.
     * @param emailParticipante (String) O novo e-mail do participante.
     */
    public void setEmailParticipante(String emailParticipante) {
        this.emailParticipante = emailParticipante;
    }

    /**
     * Define a data da compra.
     * @param dataCompra (LocalDateTime) A nova data da compra.
     */
    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    /**
     * Define o preço do ingresso.
     * @param precoIngresso (double) O novo preço do ingresso.
     */
    public void setPrecoIngresso(double precoIngresso) {
        this.precoIngresso = precoIngresso;
    }
}