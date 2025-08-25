package io.github.site_de_eventos.sitedeeventos.model.builder;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;

/**
 * Define o contrato para a construção de objetos {@link Organizador}.
 * <p>
 * Esta interface estende {@link IUsuarioBuilder}, herdando seus métodos, e adiciona
 * métodos específicos para configurar os atributos de um {@code Organizador}.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto
 */
public interface IOrganizadorBuilder extends IUsuarioBuilder {

    /**
     * Define a conta bancária do organizador.
     * @param contaBancaria (String) A conta bancária a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IOrganizadorBuilder contaBancaria(String contaBancaria);

    /**
     * Define o CNPJ do organizador.
     * @param cnpj (String) O CNPJ a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IOrganizadorBuilder cnpj(String cnpj);

    /**
     * Define o evento que está sendo gerenciado pelo organizador.
     * @param eventoOrganizado ({@link Evento}) O evento a ser associado.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado);

    /**
     * Finaliza a construção e retorna o objeto {@link Organizador} completo.
     * <p>
     * Sobrescreve o método {@code build} de {@link IUsuarioBuilder} para retornar
     * o tipo específico {@code Organizador}.
     *
     * @return O objeto {@link Organizador} construído.
     */
    public Organizador build();
}