package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

public class CalculoComCupomDesconto implements ICalculoPrecoPedidoStrategy{
	private final double valorDesconto;
	
	public CalculoComCupomDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	
	@Override
	public double calcularPreco(Pedido pedido) {
		double valorBase = pedido.getValorBase();
		return Math.max(0, valorBase - valorDesconto);
	}

}
