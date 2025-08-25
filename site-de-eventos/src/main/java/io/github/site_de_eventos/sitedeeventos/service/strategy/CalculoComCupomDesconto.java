package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

/**
 * Implementação da estratégia de cálculo de preço que aplica um cupom de desconto fixo.
 * <p>
 * Esta classe calcula o valor final de um {@link Pedido} subtraindo um valor
 * de desconto pré-definido do valor base do pedido.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see ICalculoPrecoPedidoStrategy
 */
public class CalculoComCupomDesconto implements ICalculoPrecoPedidoStrategy {

    /**
     * O valor fixo do desconto a ser subtraído do valor base do pedido.
     */
    private final double valorDesconto;

    /**
     * Construtor que inicializa a estratégia com um valor de desconto específico.
     *
     * @param valorDesconto (double) O valor do desconto a ser aplicado.
     */
    public CalculoComCupomDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Este cálculo subtrai o valor do desconto do valor base do pedido.
     * O método garante que o preço final nunca seja menor que zero.
     */
    @Override
    public double calcularPreco(Pedido pedido) {
        double valorBase = pedido.getValorBase();
        return Math.max(0, valorBase - valorDesconto);
    }
}