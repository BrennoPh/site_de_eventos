package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

/**
 * Define o contrato para a construção de algoritmos de cálculo de preço para um {@link Pedido}.
 * <p>
 * Esta interface utiliza o padrão de projeto Strategy, permitindo que diferentes
 * formas de cálculo (com descontos, taxas, etc.) sejam definidas e utilizadas
 * de forma intercambiável pelo sistema.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see CalculoComCupomDesconto
 * @see CalculoComTaxaServico
 */
public interface ICalculoPrecoPedidoStrategy {

    /**
     * Calcula o preço final de um pedido com base na estratégia específica implementada.
     *
     * @param pedido ({@link Pedido}) O pedido sobre o qual o cálculo de preço será realizado.
     * @return O preço final (double) calculado para o pedido.
     */
    double calcularPreco(Pedido pedido);
}