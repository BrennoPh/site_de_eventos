package sitedeeventos.service.strategy;

import sitedeeventos.model.Pedido;

public class CalculoComCupomDesconto implements ICalculoPrecoPedidoStrategy{
	private final double valorDesconto;
	
	public CalculoComCupomDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	
	@Override
	public double calcularPreco(Pedido pedido) {
		double valorBase = pedido.getValorBase();
	}

}
