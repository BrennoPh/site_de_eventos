package sitedeeventos.service.strategy;

import sitedeeventos.model.Pedido;

public interface ICalculoPrecoPedidoStrategy {
	double calcularPreco(Pedido pedido);
}
