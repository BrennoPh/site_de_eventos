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
import java.util.concurrent.atomic.AtomicInteger; // Importar

@Service
public class PedidoService {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private static final AtomicInteger pedidoIdGenerator = new AtomicInteger(0); 


    public PedidoService(UsuarioRepository usuarioRepository, EventoRepository eventoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
    }

    public Pedido criarPedido(int usuarioId, int eventoId, List<String> nomes, List<String> emails, String cupomCode) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        int quantidade = nomes.size();
        if (evento.getIngressosDisponiveis() < quantidade) {
            throw new RuntimeException("Não há ingressos suficientes. Disponíveis: " + evento.getIngressosDisponiveis());
        }

        Pedido pedido = new Pedido(usuario, evento, quantidade);
        pedido.setIdPedido(pedidoIdGenerator.incrementAndGet());

        // 1. Define o valor base (preço original * quantidade).
        double valorBase = evento.getPreco() * quantidade;
        pedido.setValorBase(valorBase);
        
        // --- INÍCIO DO CÁLCULO COM STRATEGY ---
        
        // Inicializa o valor final com o valor base. Ele será modificado pelas estratégias.
        double valorFinal = valorBase;

        // 2. Aplica a ESTRATÉGIA de cupom de desconto, se um cupom válido for fornecido.
        if (cupomCode != null && !cupomCode.isEmpty() && cupomCode.equalsIgnoreCase(evento.getCupomCode())) {
            // Instancia a estratégia de desconto com o valor de desconto do evento.
            ICalculoPrecoPedidoStrategy cupomStrategy = new CalculoComCupomDesconto(evento.getCupomDiscountValue());
            
            // Cria um pedido temporário para o cálculo, para não modificar o valor base do pedido real.
            Pedido pedidoParaCalculo = new Pedido();
            pedidoParaCalculo.setValorBase(valorFinal); // O valor base para o cálculo é o valor atual.
            
            // Calcula o novo valor final aplicando o desconto.
            valorFinal = cupomStrategy.calcularPreco(pedidoParaCalculo);
        }

        // 3. Aplica a ESTRATÉGIA de taxa de serviço sobre o valor (já com o possível desconto).
        ICalculoPrecoPedidoStrategy taxaStrategy = new CalculoComTaxaServico();
        
        // Usa-se outro pedido temporário para o cálculo da taxa.
        Pedido pedidoParaCalculo = new Pedido();
        pedidoParaCalculo.setValorBase(valorFinal); // O valor base para este cálculo é o valor após o desconto.
        
        // Calcula o valor final definitivo aplicando a taxa.
        valorFinal = taxaStrategy.calcularPreco(pedidoParaCalculo);

        // 4. ETAPA CRUCIAL: Define o valor total calculado no objeto de pedido principal.
        pedido.setValorTotal(valorFinal);
        
        confirmarPedido(pedido, nomes, emails);
        usuario.adicionarPedido(pedido);
        usuarioRepository.save(usuario);

        return pedido;
    }
    
    // MÉTODO ATUALIZADO
    private void confirmarPedido(Pedido pedido, List<String> nomes, List<String> emails) {
        Evento evento = pedido.getEvento();
        int quantidadeComprada = pedido.getQuantidadeIngressos();
        
        int primeiroIngressoNum = evento.getCapacidade() - evento.getIngressosDisponiveis() + 1;
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() - quantidadeComprada);
        eventoRepository.save(evento);

        List<Ingresso> ingressosComprados = new ArrayList<>();
        for (int i = 0; i < quantidadeComprada; i++) {
            String novoIdIngresso = evento.getIdEvento() + "-" + (primeiroIngressoNum + i);
            
            // Usa os nomes e e-mails da lista para criar cada ingresso
            Ingresso novoIngresso = new Ingresso(novoIdIngresso, evento.getIdEvento(), 
                nomes.get(i), emails.get(i), LocalDateTime.now(), evento.getPreco());
            
            novoIngresso.setPedido(pedido); // Associa o ingresso ao pedido
            ingressosComprados.add(novoIngresso);
        }
        pedido.setIngressos(ingressosComprados);
        pedido.setStatus("CONCLUIDO");
    }
    
    /**
     * Cancela um pedido específico de um usuário.
     * <p>
     * Implementa as regras de negócio para o cancelamento de um pedido pelo próprio usuário,
     * como a devolução dos ingressos ao estoque do evento e a verificação do status
     * do pedido e do evento.
     *
     * @param usuarioId (int) O ID do usuário que está solicitando o cancelamento.
     * @param pedidoId (int) O ID do pedido a ser cancelado.
     * @throws RuntimeException Se o usuário, pedido ou evento associado não forem encontrados.
     * @throws IllegalStateException Se o pedido não estiver em um estado que permita o cancelamento
     * ou se o evento já foi cancelado pelo organizador.
     */
    public void cancelarPedido(int usuarioId, int pedidoId) {
        // Busca o usuário no repositório. Se não encontrar, lança uma exceção.
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));

        // Procura na lista de pedidos do usuário aquele que corresponde ao ID fornecido.
        Pedido pedidoParaCancelar = usuario.getPedidos().stream() // Converte a lista para um Stream para usar a API de coleções.
            .filter(p -> p.getIdPedido() == pedidoId) // Filtra a lista, mantendo apenas o pedido com o ID correto.
            .findFirst() // Pega o primeiro (e único) resultado.
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado ou não pertence a este usuário.")); // Se não encontrar, lança exceção.
        
        // --- VALIDAÇÃO DE STATUS DO PEDIDO ---
        // Regra de negócio: Garante que apenas pedidos com status "CONCLUIDO" podem ser cancelados pelo usuário.
        if (!"CONCLUIDO".equals(pedidoParaCancelar.getStatus())) {
            // Se o status for diferente, lança uma exceção informando o motivo.
            throw new IllegalStateException("Este pedido não pode ser cancelado, pois seu status é: " + pedidoParaCancelar.getStatus());
        }

        // Busca o evento associado ao pedido que será cancelado.
        Evento evento = eventoRepository.findById(pedidoParaCancelar.getEvento().getIdEvento())
            .orElseThrow(() -> new RuntimeException("Evento associado ao pedido não foi encontrado."));

        // Regra de negócio: Impede o cancelamento se o evento em si já foi cancelado pelo organizador.
        if ("CANCELADO".equals(evento.getStatus())) {
            throw new IllegalStateException("Não é possível cancelar um ingresso de um evento que já foi cancelado pelo organizador.");
        }

        // Devolve os ingressos do pedido cancelado de volta ao "estoque" do evento.
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() + pedidoParaCancelar.getQuantidadeIngressos());
        // Salva o objeto 'evento' com a quantidade de ingressos atualizada.
        eventoRepository.save(evento);

        // Atualiza o status do pedido para indicar que foi o usuário quem cancelou.
        pedidoParaCancelar.setStatus("CANCELADO_PELO_USUARIO");
        // Salva o objeto 'usuario'. O JPA/Hibernate entende que o 'pedidoParaCancelar' dentro da lista foi modificado e persiste a alteração.
        usuarioRepository.save(usuario);
    }
}