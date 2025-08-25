package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;

/**
 * Implementação do padrão de projeto Builder para a classe {@link Evento}.
 * <p>
 * Fornece uma API fluente para construir um objeto {@code Evento} de forma
 * segura e legível.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class EventoBuilderConcreto implements IEventoBuilder {
    /**
     * A instância do Evento que está sendo construída.
     */
    private Evento evento;

    /**
     * Construtor que inicializa um novo objeto {@link Evento} para ser configurado.
     */
    public EventoBuilderConcreto() {
        this.evento = new Evento();
    }

    /**
     * Define o ID do evento.
     * @param idEvento (int) O ID a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder idEvento(int idEvento) {
        this.evento.setIdEvento(idEvento);
        return this;
    }

    /**
     * Define a capacidade do evento.
     * @param capacidade (int) A capacidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder capacidade(int capacidade) {
        this.evento.setCapacidade(capacidade);
        return this;
    }

    /**
     * Define o número de ingressos disponíveis.
     * @param ingressosDisponiveis (int) A quantidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder ingressosDisponiveis(int ingressosDisponiveis) {
        this.evento.setIngressosDisponiveis(ingressosDisponiveis);
        return this;
    }

    /**
     * Define o nome do evento.
     * @param nomeEvento (String) O nome a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder nomeEvento(String nomeEvento) {
        this.evento.setNomeEvento(nomeEvento);
        return this;
    }

    /**
     * Define o local do evento.
     * @param local (String) O local a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder local(String local) {
        this.evento.setLocal(local);
        return this;
    }

    /**
     * Define a categoria do evento.
     * @param categoria (String) A categoria a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder categoria(String categoria) {
        this.evento.setCategoria(categoria);
        return this;
    }

    /**
     * Define o organizador do evento.
     * @param organizador ({@link Organizador}) O organizador a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder organizador(Organizador organizador) {
        this.evento.setOrganizador(organizador);
        return this;
    }

    /**
     * Define a descrição do evento.
     * @param descricao (String) A descrição a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder descricao(String descricao) {
        this.evento.setDescrição(descricao);
        return this;
    }

    /**
     * Define o preço do evento.
     * @param preco (double) O preço a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder preco(double preco) {
        this.evento.setPreco(preco);
        return this;
    }

    /**
     * Define a data do evento.
     * @param dataEvento (LocalDateTime) A data a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder dataEvento(LocalDateTime dataEvento) {
        this.evento.setDataEvento(dataEvento);
        return this;
    }

    /**
     * Define a URL da imagem do evento.
     * @param imageUrl (String) A URL a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder imageUrl(String imageUrl) {
        this.evento.setImageUrl(imageUrl);
        return this;
    }

    /**
     * Define o código de cupom do evento.
     * @param cupomCode (String) O código de cupom a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder cupomCode(String cupomCode) {
        this.evento.setCupomCode(cupomCode);
        return this;
    }

    /**
     * Define o valor de desconto do cupom do evento.
     * @param cupomDiscountValue (double) O valor de desconto a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IEventoBuilder cupomDiscountValue(double cupomDiscountValue) {
        this.evento.setCupomDiscountValue(cupomDiscountValue);
        return this;
    }
    
    @Override
    public IEventoBuilder organizadorId(int organizadorId) {
        this.evento.setOrganizadorId(organizadorId);
        return this;
    }

    /**
     * Constrói e retorna o objeto {@link Evento} final.
     * @return O objeto {@link Evento} configurado.
     */
    @Override
    public Evento build() {
        //Define um status padrão (ATIVO) para o evento
        if (this.evento.getStatus() == null || this.evento.getStatus().isEmpty()) {
            this.evento.setStatus("ATIVO");
        }
        return this.evento;
    }
}