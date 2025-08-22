package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

public class CalculoComTaxaServico implements ICalculoPrecoPedidoStrategy{
	private static final double TAXA_SERVICO = 0.05;
	
	@Override
	public double calcularPreco(Pedido pedido) {
		double valorBase = pedido.getValorBase();
		return valorBase * (1 + TAXA_SERVICO);
	}
}
