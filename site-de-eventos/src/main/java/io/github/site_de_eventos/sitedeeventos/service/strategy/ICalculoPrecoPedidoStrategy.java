package io.github.site_de_eventos.sitedeeventos.service.strategy;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;

public interface ICalculoPrecoPedidoStrategy {
	double calcularPreco(Pedido pedido);
}
