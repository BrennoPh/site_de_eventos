package io.github.site_de_eventos.sitedeeventos.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Evento save(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("O objeto de evento não pode ser nulo.");
        }
        return eventoRepository.save(evento);
    }

    public List<Evento> buscarTodos() {
        List<Evento> eventos = eventoRepository.findAll();
        eventos.sort(Comparator.comparingInt(Evento::getIdEvento).reversed());
        return eventos;
    }

    public Optional<Evento> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return eventoRepository.findById(id);
    }

    public List<Evento> buscarPorNome(String termo) {
        return eventoRepository.findByNomeContaining(termo);
    }

    /**
     * Cancela um evento, atualiza seu status e o status de todos os pedidos
     * associados.
     * <p>
     * Este método implementa a lógica de negócio para o cancelamento de um evento,
     * garantindo que apenas o organizador correto possa realizar a ação e que o
     * evento não tenha sido cancelado previamente.
     *
     * @param eventoId    (int) O ID do evento a ser cancelado.
     * @param organizador (Organizador) O organizador que está tentando realizar a
     *                    ação.
     * @throws RuntimeException      Se o evento não for encontrado.
     * @throws SecurityException     Se o organizador não for o dono do evento.
     * @throws IllegalStateException Se o evento já estiver cancelado.
     */
    public void cancelarEvento(int eventoId, Organizador organizador) {
        // Busca o evento no repositório pelo ID. Se não encontrar, lança uma exceção.
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        // Validação de segurança: verifica se o ID do organizador logado é o mesmo
        // associado ao evento.
        if (evento.getOrganizadorId() != organizador.getIdUsuario()) {
            // Se não for, lança uma exceção de segurança para impedir a ação.
            throw new SecurityException("Você não tem permissão para cancelar este evento.");
        }

        // Validação de estado: verifica se o evento já possui o status "CANCELADO".
        if ("CANCELADO".equals(evento.getStatus())) {
            // Se sim, lança uma exceção para evitar processamento redundante.
            throw new IllegalStateException("Este evento já foi cancelado.");
        }

        // Atualiza os atributos do evento para refletir o cancelamento.
        evento.setStatus("CANCELADO");
        evento.setIngressosDisponiveis(0); // Zera os ingressos disponíveis.
        // Salva o evento modificado de volta no banco de dados.
        eventoRepository.save(evento);

        // --- LÓGICA PARA CANCELAR TODOS OS PEDIDOS ASSOCIADOS ---
        // Busca todos os usuários do sistema. (Obs: pode ser otimizado em um sistema
        // real).
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        // Itera sobre cada usuário encontrado.
        for (Usuario usuario : todosUsuarios) {
            // Flag para controlar se precisamos salvar este usuário.
            boolean usuarioModificado = false;
            // Verifica se a lista de pedidos do usuário não é nula.
            if (usuario.getPedidos() != null) {
                // Itera sobre cada pedido do usuário.
                for (Pedido pedido : usuario.getPedidos()) {
                    // Verifica se o pedido pertence ao evento que foi cancelado e se o pedido ainda
                    // não está cancelado.
                    if (pedido.getEvento().getIdEvento() == eventoId
                            && !"CANCELADO_PELO_ORGANIZADOR".equals(pedido.getStatus())) {
                        // Se as condições forem atendidas, atualiza o status do pedido.
                        pedido.setStatus("CANCELADO_PELO_ORGANIZADOR");
                        // Marca que o objeto 'usuario' foi modificado.
                        usuarioModificado = true;
                    }
                }
            }
            // Se algum pedido do usuário foi modificado...
            if (usuarioModificado) {
                // Salva o objeto 'usuario' no repositório. O JPA atualizará os pedidos
                // associados.
                usuarioRepository.save(usuario);
            }
        }
    }

    /**
     * Busca eventos com base em uma query. Se a query for nula ou vazia,
     * retorna todos os eventos. Caso contrário, filtra pelo nome.
     *
     * @param query (String) O termo de busca para o nome do evento.
     * @return Uma lista de {@link Evento}.
     */
    public List<Evento> buscarEventos(String query) {
        List<Evento> eventos;
        if (query != null && !query.trim().isEmpty()) {
            eventos = eventoRepository.findByNomeContaining(query);
        } else {
            eventos = eventoRepository.findAll();
        }
        eventos.sort(Comparator.comparingInt(Evento::getIdEvento).reversed());
        return eventos;
    }

    /**
     * Busca todos os eventos criados por um organizador específico.
     *
     * @param organizador ({@link Organizador}) O organizador cujos eventos serão
     *                    buscados.
     * @return Uma lista de {@link Evento} pertencentes ao organizador.
     */
    public List<Evento> buscarPorOrganizador(Organizador organizador) {
        List<Evento> eventos = eventoRepository.findAll().stream()
                .filter(evento -> evento.getOrganizadorId() == organizador.getIdUsuario())
                .collect(Collectors.toList());
        eventos.sort(Comparator.comparingInt(Evento::getIdEvento).reversed());
        return eventos;
    }

    /**
     * Cria e salva um novo evento com base nos parâmetros individuais do
     * formulário.
     * <p>
     * Este método abstrai a lógica de construção de um objeto {@link Evento},
     * utilizando o padrão Builder para montar a instância de forma segura e legível
     * antes de persisti-la no banco de dados.
     *
     * @param nomeEvento         (String) Nome do evento.
     * @param dataEvento         (LocalDateTime) Data do evento.
     * @param local              (String) Local do evento.
     * @param descricao          (String) Descrição detalhada do evento.
     * @param categoria          (String) Categoria na qual o evento se enquadra.
     * @param preco              (double) Preço base do ingresso.
     * @param capacidade         (int) Capacidade máxima de participantes.
     * @param imageUrl           (String) URL da imagem de divulgação.
     * @param cupomCode          (String) Código do cupom de desconto.
     * @param cupomDiscountValue (double) Valor do desconto do cupom.
     * @param organizador        (Organizador) O organizador que está criando o
     *                           evento.
     * @return O objeto {@link Evento} que foi criado e salvo.
     */
    public Evento criarNovoEvento(String nomeEvento, LocalDateTime dataEvento, String local,
            String descricao, String categoria, double preco,
            int capacidade, String imageUrl, String cupomCode,
            double cupomDiscountValue, Organizador organizador) {

        // Garante que ao criar um evento novo, não seja permitido datas anteriores ao
        // do dia atual.
        if (dataEvento.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data do evento não pode ser anterior à data atual.");
        }

        // Instancia a implementação concreta do Builder para começar a construir o
        // evento.
        IEventoBuilder builder = new EventoBuilderConcreto();

        // Utiliza a API fluente do Builder para configurar todos os atributos do novo
        // evento.
        Evento novoEvento = builder
                .nomeEvento(nomeEvento)
                .dataEvento(dataEvento)
                .local(local)
                .descricao(descricao)
                .categoria(categoria)
                .preco(preco)
                .capacidade(capacidade)
                .ingressosDisponiveis(capacidade) // Regra de negócio: a quantidade inicial de ingressos é a capacidade
                                                  // total.
                .imageUrl(imageUrl)
                .cupomCode(cupomCode)
                .cupomDiscountValue(cupomDiscountValue)
                .organizador(organizador) // Associa o objeto completo do organizador.
                .organizadorId(organizador.getIdUsuario()) // Armazena também o ID para checagens de permissão rápidas.
                .build(); // Finaliza a construção e retorna o objeto Evento completo.

        // Chama o método 'save' do próprio serviço (ou repositório) para persistir o
        // objeto no banco.
        return this.save(novoEvento);
    }

}
