package sitedeeventos.service.strategy;

import sitedeeventos.model.Pedido;

public class CalculoComTaxaServico implements ICalculoPrecoPedidoStrategy{
	private static final double TAXA_SERVICO = 0.05;
	
	@Override
	public double calcularPreco(Pedido pedido) {
		double valorBase = pedido.getValorBase();
		return valorBase * (1 + TAXA_SERVICO);
	}
}
