
package io.github.site_de_eventos.sitedeeventos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Ingresso;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import io.github.site_de_eventos.sitedeeventos.service.strategy.ICalculoPrecoPedidoStrategy;

@Service
public class PedidoService {

	private final UsuarioRepository usuarioRepository;
	private final EventoRepository eventoRepository;
	private final Map<String, ICalculoPrecoPedidoStrategy> calculoStrategies;

	public PedidoService(UsuarioRepository usuarioRepository, EventoRepository eventoRepository,
			Map<String, ICalculoPrecoPedidoStrategy> calculoStrategies) {
		this.usuarioRepository = usuarioRepository;
		this.eventoRepository = eventoRepository;
		this.calculoStrategies = calculoStrategies;
	}

	public Pedido criarPedido(int usuarioId, int eventoId, int quantidade, String nomeStrategy) {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));

		Evento evento = eventoRepository.findById(eventoId)
				.orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

		// VALIDAÇÃO: Verificar se há ingressos suficientes
		if (evento.getIngressosDisponiveis() < quantidade) {
			throw new RuntimeException(
					"Não há ingressos suficientes. Disponíveis: " + evento.getIngressosDisponiveis());
		}

		Pedido pedido = new Pedido(usuario, evento, quantidade);

		// CÁLCULO: Calcular o valor base antes de aplicar a strategy
		double valorBase = evento.getPreco() * quantidade;
		pedido.setValorBase(valorBase);

		// STRATEGY: Selecionar e aplicar a estratégia de cálculo de preço.
		ICalculoPrecoPedidoStrategy strategy = calculoStrategies.getOrDefault(nomeStrategy, p -> p.getValorBase());

		//  STRATEGY: Chamamos a strategy e guardamos o valor final que ela retorna.
		double valorFinalCalculado = strategy.calcularPreco(pedido);

		// ATRIBUIÇÃO: Definimos o valor final calculado no objeto pedido.
		pedido.setValorTotal(valorFinalCalculado);
		
		// CONFIRMAÇÃO: Finalizar o pedido, gerando ingressos e atualizando o estoque.
		confirmarPedido(pedido);

		// ASSOCIAÇÃO: Adicionar o pedido à lista do usuário e salvar.
		usuario.adicionarPedido(pedido);
		usuarioRepository.save(usuario);

		return pedido;
	}

	private void confirmarPedido(Pedido pedido) {
		Evento evento = pedido.getEvento();

		// Atualiza o número de ingressos disponíveis no evento.
		int ingressosRestantes = evento.getIngressosDisponiveis() - pedido.getQuantidadeIngressos();
		evento.setIngressosDisponiveis(ingressosRestantes);
		eventoRepository.save(evento); // Persiste a alteração no evento.

		// Gera os ingressos para o pedido.
		List<Ingresso> ingressosComprados = new ArrayList<>();
		for (int i = 0; i < pedido.getQuantidadeIngressos(); i++) {
			Ingresso novoIngresso = new Ingresso(0, evento.getIdEvento(), pedido.getUsuario().getNome(),
					pedido.getUsuario().getEmail(), LocalDateTime.now(), evento.getPreco());
			ingressosComprados.add(novoIngresso);
		}
		pedido.setIngressos(ingressosComprados);
		pedido.setStatus("CONCLUIDO");
	}
}
