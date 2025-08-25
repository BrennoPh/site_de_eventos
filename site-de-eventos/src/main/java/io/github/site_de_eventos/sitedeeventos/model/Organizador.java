package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;

/**
 * Representa a entidade Organizador, uma especialização de {@link Usuario}.
 * <p>
 * Esta classe herda os atributos de {@code Usuario} e adiciona campos
 * específicos para um organizador de eventos, como CNPJ e conta bancária.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class Organizador extends Usuario {
    /**
     * Conta bancária do organizador para recebimento de pagamentos.
     */
    @Expose
    private String contaBancaria;
    /**
     * Cadastro Nacional da Pessoa Jurídica (CNPJ) do organizador.
     */
    @Expose
    private String cnpj;

    /**
     * O evento que o organizador está gerenciando. Não é exposto no JSON
     * para evitar referências circulares.
     * @see Evento
     */
    private Evento eventoOrganizado;

    /**
     * Construtor padrão que invoca o construtor da superclasse {@link Usuario}.
     */
    public Organizador() {
        super();
    }

    /**
     * Obtém a conta bancária do organizador.
     * @return A conta bancária (String) do organizador.
     */
    public String getContaBancaria() {
        return contaBancaria;
    }

    /**
     * Obtém o CNPJ do organizador.
     * @return O CNPJ (String) do organizador.
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * Obtém o evento organizado.
     * @return O objeto ({@link Evento}) que está sendo organizado.
     */
    public Evento getEventoOrganizado() {
        return eventoOrganizado;
    }

    /**
     * Define a conta bancária do organizador.
     * @param contaBancaria (String) A nova conta bancária.
     */
    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    /**
     * Define o CNPJ do organizador.
     * @param cnpj (String) O novo CNPJ.
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    /**
     * Define o evento que este organizador gerencia.
     * @param eventoOrganizado ({@link Evento}) O evento a ser associado.
     */
    public void setEventoOrganizado(Evento eventoOrganizado) {
        this.eventoOrganizado = eventoOrganizado;
    }
}