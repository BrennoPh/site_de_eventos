package io.github.site_de_eventos.sitedeeventos.service;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Ingresso;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import io.github.site_de_eventos.sitedeeventos.service.strategy.CalculoComCupomDesconto;
import io.github.site_de_eventos.sitedeeventos.service.strategy.CalculoComTaxaServico;
import io.github.site_de_eventos.sitedeeventos.service.strategy.ICalculoPrecoPedidoStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    // A injeção do Map de strategies não será mais necessária com esta abordagem dinâmica
    public PedidoService(UsuarioRepository usuarioRepository, EventoRepository eventoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
    }

    public Pedido criarPedido(int usuarioId, int eventoId, int quantidade, String cupomCode) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        if (evento.getIngressosDisponiveis() < quantidade) {
            throw new RuntimeException("Não há ingressos suficientes. Disponíveis: " + evento.getIngressosDisponiveis());
        }

        Pedido pedido = new Pedido(usuario, evento, quantidade);

        // 1. Define o valor base (preço original sem taxas ou descontos)
        double valorBase = evento.getPreco() * quantidade;
        pedido.setValorBase(valorBase);

        // Inicia o valor final com o valor base
        double valorFinal = valorBase;

        // 2. Aplica o cupom de desconto, se for válido
        if (cupomCode != null && !cupomCode.isEmpty() && cupomCode.equalsIgnoreCase(evento.getCupomCode())) {
            // Usa a strategy de desconto sobre o valor atual
            ICalculoPrecoPedidoStrategy cupomStrategy = new CalculoComCupomDesconto(evento.getCupomDiscountValue());
            // Aqui, passamos um pedido temporário para a strategy, para não modificar o valor base do pedido real ainda
            Pedido pedidoParaDesconto = new Pedido();
            pedidoParaDesconto.setValorBase(valorFinal);
            valorFinal = cupomStrategy.calcularPreco(pedidoParaDesconto);
        }

        // 3. Aplica a taxa de serviço sobre o valor já com desconto
        ICalculoPrecoPedidoStrategy taxaStrategy = new CalculoComTaxaServico();
        // Passamos um pedido temporário com o valor atualizado para a strategy
        Pedido pedidoParaTaxa = new Pedido();
        pedidoParaTaxa.setValorBase(valorFinal);
        valorFinal = taxaStrategy.calcularPreco(pedidoParaTaxa);

        // 4. Define o valor total final no pedido
        pedido.setValorTotal(valorFinal);
        
        confirmarPedido(pedido);
        usuario.adicionarPedido(pedido);
        usuarioRepository.save(usuario);

        return pedido;
    }

    private void confirmarPedido(Pedido pedido) {
        Evento evento = pedido.getEvento();
        int quantidadeComprada = pedido.getQuantidadeIngressos();
        
        int primeiroIngressoNum = evento.getCapacidade() - evento.getIngressosDisponiveis() + 1;

        int ingressosRestantes = evento.getIngressosDisponiveis() - quantidadeComprada;
        evento.setIngressosDisponiveis(ingressosRestantes);
        eventoRepository.save(evento);

        List<Ingresso> ingressosComprados = new ArrayList<>();
        for (int i = 0; i < quantidadeComprada; i++) {
            String novoIdIngresso = evento.getIdEvento() + "-" + (primeiroIngressoNum + i);
            
            Ingresso novoIngresso = new Ingresso(novoIdIngresso, evento.getIdEvento(), 
                pedido.getUsuario().getNome(), pedido.getUsuario().getEmail(), 
                LocalDateTime.now(), evento.getPreco());
            
            ingressosComprados.add(novoIngresso);
        }
        pedido.setIngressos(ingressosComprados);
        pedido.setStatus("CONCLUIDO");
    }
}