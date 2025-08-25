package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa a entidade Pedido no sistema.
 * <p>
 * Esta classe armazena os dados de uma transação de compra de ingressos,
 * ligando um {@link Usuario} a um {@link Evento}.
 * <p>
 * Os campos anotados com {@code @Expose} são para a conversão do objeto para 
 * o formato JSON e vice-versa.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class Pedido {
    /**
     * Identificador único do pedido.
     */
    @Expose
    private int idPedido;
    /**
     * O evento associado a este pedido.
     */
    @Expose
    private Evento evento;
    /**
     * Quantidade de ingressos comprados no pedido.
     */
    @Expose
    private int quantidadeIngressos;
    /**
     * Valor base do pedido (preço unitário * quantidade).
     */
    @Expose
    private double valorBase;
    /**
     * Valor total do pedido após descontos e taxas.
     */
    @Expose
    private double valorTotal;
    /**
     * Data e hora em que o pedido foi criado.
     */
    @Expose
    private LocalDateTime dataPedido;
    /**
     * Status atual do pedido (ex: PENDENTE, APROVADO, CANCELADO).
     */
    @Expose
    private String status;
    /**
     * Lista de ingressos gerados por este pedido.
     */
    @Expose
    private List<Ingresso> ingressos;

    /**
     * O usuário que realizou o pedido. Não é exposto no JSON para evitar
     * referências circulares.
     * @see Usuario
     */
    private Usuario usuario;
    	

    /**
     * Construtor principal para criar um novo pedido.
     * Define o status inicial como "PENDENTE" e a data do pedido como a data atual.
     * @param usuario ({@link Usuario}) O usuário que realiza o pedido.
     * @param evento ({@link Evento}) O evento para o qual os ingressos são comprados.
     * @param quantidade (int) A quantidade de ingressos.
     */
    public Pedido(Usuario usuario, Evento evento, int quantidade) {
        this.usuario = usuario;
        this.evento = evento;
        this.quantidadeIngressos = quantidade;
        this.dataPedido = LocalDateTime.now();
        this.status = "PENDENTE";
    }
    
    /**
     * Construtor padrão, sem argumentos. Necessário para bibliotecas de
     * serialização/desserialização.
     */
    public Pedido() {}
    
    /**
     * Obtém o ID do pedido.
     * @return O ID (int) do pedido.
     */
    public int getIdPedido() {
        return idPedido;
    }

    /**
     * Define o ID do pedido.
     * @param idPedido (int) O novo ID do pedido.
     */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /**
     * Obtém o usuário que realizou o pedido.
     * @return O objeto ({@link Usuario}) do usuário.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define o usuário que realizou o pedido.
     * @param usuario ({@link Usuario}) O usuário a ser associado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtém o evento do pedido.
     * @return O objeto ({@link Evento}) do evento.
     */
    public Evento getEvento() {
        return evento;
    }

    /**
     * Define o evento do pedido.
     * @param evento ({@link Evento}) O evento a ser associado.
     */
    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    /**
     * Obtém a lista de ingressos do pedido.
     * @return A lista (List) de {@link Ingresso}.
     */
    public List<Ingresso> getIngressos() {
        return ingressos;
    }

    /**
     * Define a lista de ingressos do pedido.
     * @param ingressos (List) de ({@link Ingresso}) a nova lista de ingressos.
     */
    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }

    /**
     * Obtém a quantidade de ingressos no pedido.
     * @return A quantidade (int) de ingressos.
     */
    public int getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    /**
     * Define a quantidade de ingressos no pedido.
     * @param quantidadeIngressos (int) A nova quantidade de ingressos.
     */
    public void setQuantidadeIngressos(int quantidadeIngressos) {
        this.quantidadeIngressos = quantidadeIngressos;
    }

    /**
     * Obtém o valor base do pedido.
     * @return O valor base (double) do pedido.
     */
    public double getValorBase() {
        return valorBase;
    }
    

    /**
     * Define o valor base do pedido.
     * @param valorBase (double) O novo valor base.
     */
    public void setValorBase(double valorBase) {
        this.valorBase = valorBase;
    }

    /**
     * Obtém o valor total do pedido.
     * @return O valor total (double) do pedido.
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * Define o valor total do pedido.
     * @param valorTotal (double) O novo valor total.
     */
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * Obtém a data de criação do pedido.
     * @return A data do pedido (LocalDateTime).
     */
    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    /**
     * Define a data de criação do pedido.
     * @param dataPedido (LocalDateTime) A nova data do pedido.
     */
    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    /**
     * Obtém o status do pedido.
     * @return O status (String) do pedido.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Define o status do pedido.
     * @param status (String) O novo status do pedido.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}