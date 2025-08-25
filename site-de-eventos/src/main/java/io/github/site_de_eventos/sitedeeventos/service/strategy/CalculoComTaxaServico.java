package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

/**
 * Implementação da estratégia de cálculo de preço que adiciona uma taxa de serviço.
 * <p>
 * Esta classe calcula o valor final de um {@link Pedido} acrescentando uma
 * taxa percentual fixa sobre o valor base do pedido.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see ICalculoPrecoPedidoStrategy
 */
public class CalculoComTaxaServico implements ICalculoPrecoPedidoStrategy {

    /**
     * Taxa de serviço fixa de 5% (0.05) aplicada sobre o valor base do pedido.
     */
    private static final double TAXA_SERVICO = 0.05;

    /**
     * {@inheritDoc}
     * <p>
     * Este cálculo adiciona a taxa de serviço ({@value #TAXA_SERVICO}) ao valor base do pedido.
     */
    @Override
    public double calcularPreco(Pedido pedido) {
        double valorBase = pedido.getValorBase();
        return valorBase * (1 + TAXA_SERVICO);
    }
}