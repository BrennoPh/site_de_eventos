package io.github.site_de_eventos.sitedeeventos.model.builder;

import java.time.LocalDateTime;
import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;

/**
 * Define o contrato para a construção de objetos {@link Evento} utilizando o padrão de projeto Builder.
 * <p>
 * Esta interface especifica todos os métodos necessários para configurar um objeto {@code Evento}
 * de forma fluente e passo a passo.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto
 */
public interface IEventoBuilder {

    /**
     * Define o ID do evento.
     * @param idEvento (int) O ID a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder idEvento(int idEvento);

    /**
     * Define a capacidade máxima do evento.
     * @param capacidade (int) A capacidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder capacidade(int capacidade);

    /**
     * Define a quantidade de ingressos disponíveis.
     * @param ingressosDisponiveis (int) A quantidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder ingressosDisponiveis(int ingressosDisponiveis);

    /**
     * Define o nome do evento.
     * @param nomeEvento (String) O nome a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder nomeEvento(String nomeEvento);

    /**
     * Define o local do evento.
     * @param local (String) O local a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder local(String local);

    /**
     * Define a categoria do evento.
     * @param categoria (String) A categoria a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder categoria(String categoria);

    /**
     * Define o organizador responsável pelo evento.
     * @param organizador ({@link Organizador}) O organizador a ser associado.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder organizador(Organizador organizador);

    /**
     * Define a descrição do evento.
     * @param descricao (String) O texto descritivo a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder descricao(String descricao);

    /**
     * Define o preço do ingresso do evento.
     * @param preco (double) O preço a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder preco(double preco);

    /**
     * Define a data e hora do evento.
     * @param dataEvento (LocalDateTime) A data e hora a serem definidas.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder dataEvento(LocalDateTime dataEvento);

    /**
     * Define a URL da imagem de divulgação do evento.
     * @param imageUrl (String) A URL da imagem a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder imageUrl(String imageUrl);

    /**
     * Define o código do cupom de desconto para o evento.
     * @param cupomCode (String) O código do cupom a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder cupomCode(String cupomCode);

    /**
     * Define o valor do desconto oferecido pelo cupom.
     * @param cupomDiscountValue (double) O valor do desconto a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IEventoBuilder cupomDiscountValue(double cupomDiscountValue);

    /**
     * Finaliza a construção e retorna o objeto {@link Evento} completo.
     * @return O objeto {@link Evento} construído.
     */
    
    public IEventoBuilder organizadorId(int organizadorId);
    public Evento build();
}