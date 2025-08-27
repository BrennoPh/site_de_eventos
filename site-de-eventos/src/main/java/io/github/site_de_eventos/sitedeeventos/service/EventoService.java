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

/**
 * Classe de serviço responsável por encapsular a lógica de negócio relacionada à entidade {@link Evento}.
 * Esta classe atua como uma camada intermediária entre os controladores ({@code Controller}) e os repositórios ({@code Repository}),
 * garantindo que as regras de negócio, validações e orquestração de operações sejam mantidas de forma coesa e centralizada.
 * A anotação {@code @Service} indica ao Spring que esta é uma classe de serviço e deve ser gerenciada por ele.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construtor para injeção de dependências. O Spring injetará automaticamente as instâncias
     * de {@link EventoRepository} e {@link UsuarioRepository} quando esta classe for criada.
     * Esta é a forma recomendada de injeção de dependência.
     *
     * @param eventoRepository Repositório para acesso aos dados de eventos.
     * @param usuarioRepository Repositório para acesso aos dados de usuários, necessário para operações como cancelamento de pedidos.
     */
    @Autowired
    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Salva ou atualiza um evento no repositório.
     * Realiza uma validação para garantir que o objeto de evento não seja nulo antes de prosseguir.
     *
     * @param evento O objeto {@link Evento} a ser persistido.
     * @return O evento que foi salvo, possivelmente com um ID atualizado pelo repositório.
     * @throws IllegalArgumentException se o evento fornecido for nulo.
     */
    public Evento save(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("O objeto de evento não pode ser nulo.");
        }
        return eventoRepository.save(evento);
    }

    /**
     * Busca todos os eventos disponíveis e os retorna ordenados por ID em ordem decrescente.
     * A ordenação garante que os eventos criados mais recentemente apareçam primeiro nas listagens.
     *
     * @return Uma {@link List} de {@link Evento} ordenada dos mais novos para os mais antigos.
     */
    public List<Evento> buscarTodos() {
        List<Evento> eventos = eventoRepository.findAll();
        eventos.sort(Comparator.comparingInt(Evento::getIdEvento).reversed());
        return eventos;
    }

    /**
     * Busca um evento específico pelo seu ID.
     * Inclui uma verificação para retornar um Optional vazio imediatamente se o ID for inválido (menor ou igual a zero),
     * evitando uma consulta desnecessária ao repositório.
     *
     * @param id O identificador único do evento.
     * @return Um {@link Optional} contendo o {@link Evento} se encontrado, ou um Optional vazio caso contrário.
     */
    public Optional<Evento> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return eventoRepository.findById(id);
    }

    /**
     * Busca uma lista de eventos cujo nome contenha um determinado termo de busca.
     * Delega a chamada diretamente para o repositório.
     *
     * @param termo A string a ser procurada nos nomes dos eventos.
     * @return Uma {@link List} de {@link Evento} que correspondem ao critério.
     */
    public List<Evento> buscarPorNome(String termo) {
        return eventoRepository.findByNomeContaining(termo);
    }

    /**
     * Orquestra o processo de cancelamento de um evento.
     * Este método implementa uma lógica de negócio crítica que inclui:
     * 1. Validação de existência do evento.
     * 2. Validação de permissão (apenas o organizador do evento pode cancelá-lo).
     * 3. Validação de estado (impede o cancelamento de um evento que já está cancelado).
     * 4. Atualização do status do evento e zeramento de ingressos.
     * 5. Propagação do cancelamento para todos os {@link Pedido} associados a este evento,
     * atualizando o status de cada um e salvando as alterações nos respectivos usuários.
     *
     * @param eventoId O ID do evento a ser cancelado.
     * @param organizador O {@link Organizador} que está tentando realizar a ação.
     * @throws RuntimeException se o evento não for encontrado.
     * @throws SecurityException se o organizador não for o dono do evento.
     * @throws IllegalStateException se o evento já estiver com o status "CANCELADO".
     */
    public void cancelarEvento(int eventoId, Organizador organizador) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        if (evento.getOrganizadorId() != organizador.getIdUsuario()) {
            throw new SecurityException("Você não tem permissão para cancelar este evento.");
        }

        if ("CANCELADO".equals(evento.getStatus())) {
            throw new IllegalStateException("Este evento já foi cancelado.");
        }

        evento.setStatus("CANCELADO");
        evento.setIngressosDisponiveis(0);
        eventoRepository.save(evento);

        // Itera sobre todos os usuários para encontrar e atualizar os pedidos relacionados.
        // Nota: Em uma aplicação de grande escala, esta operação seria otimizada com uma consulta direta aos pedidos.
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        for (Usuario usuario : todosUsuarios) {
            boolean usuarioModificado = false;
            if (usuario.getPedidos() != null) {
                for (Pedido pedido : usuario.getPedidos()) {
                    if (pedido.getEvento().getIdEvento() == eventoId
                            && !"CANCELADO_PELO_ORGANIZADOR".equals(pedido.getStatus())) {
                        pedido.setStatus("CANCELADO_PELO_ORGANIZADOR");
                        usuarioModificado = true;
                    }
                }
            }
            if (usuarioModificado) {
                usuarioRepository.save(usuario);
            }
        }
    }

    /**
     * Realiza uma busca de eventos com base em um termo (query).
     * Se o termo for nulo ou vazio, retorna todos os eventos. Caso contrário, filtra pelo nome.
     * Em ambos os casos, a lista resultante é ordenada para mostrar os eventos mais recentes primeiro.
     *
     * @param query O termo de busca para o nome do evento. Pode ser nulo.
     * @return Uma {@link List} de {@link Evento} ordenada e, se aplicável, filtrada.
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
     * Filtra a lista completa de eventos para encontrar aqueles cujo {@code organizadorId}
     * corresponde ao ID do organizador fornecido e retorna a lista ordenada.
     *
     * @param organizador O {@link Organizador} cujos eventos serão buscados.
     * @return Uma lista de {@link Evento} pertencentes ao organizador, ordenada pela data de criação.
     */
    public List<Evento> buscarPorOrganizador(Organizador organizador) {
        List<Evento> eventos = eventoRepository.findAll().stream()
                .filter(evento -> evento.getOrganizadorId() == organizador.getIdUsuario())
                .collect(Collectors.toList());
        eventos.sort(Comparator.comparingInt(Evento::getIdEvento).reversed());
        return eventos;
    }

    /**
     * Cria e salva um novo evento a partir dos dados fornecidos.
     * Este método centraliza a criação de eventos, aplicando regras de negócio importantes:
     * 1. Valida se a data do evento não é anterior à data atual.
     * 2. Utiliza o padrão de projeto Builder ({@link IEventoBuilder}) para construir o objeto {@link Evento}
     * de forma segura e legível, garantindo que o objeto esteja em um estado consistente.
     * 3. Define a quantidade inicial de ingressos como sendo igual à capacidade total do evento.
     *
     * @param nomeEvento O nome do evento.
     * @param dataEvento A data e hora em que o evento ocorrerá.
     * @param local O endereço ou local do evento.
     * @param descricao Uma descrição detalhada sobre o evento.
     * @param categoria A categoria do evento (ex: Show, Festa).
     * @param preco O preço unitário do ingresso.
     * @param capacidade A capacidade máxima de público do evento.
     * @param imageUrl URL de uma imagem de divulgação (opcional).
     * @param cupomCode Código de um cupom de desconto (opcional).
     * @param cupomDiscountValue Valor do desconto associado ao cupom (opcional).
     * @param organizador O {@link Organizador} que está criando o evento.
     * @return O objeto {@link Evento} que foi criado e persistido no repositório.
     * @throws IllegalArgumentException se a data do evento for anterior à data atual.
     */
    public Evento criarNovoEvento(String nomeEvento, LocalDateTime dataEvento, String local,
            String descricao, String categoria, double preco,
            int capacidade, String imageUrl, String cupomCode,
            double cupomDiscountValue, Organizador organizador) {

        if (dataEvento.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data do evento não pode ser anterior à data atual.");
        }
        
        if (cupomDiscountValue > (preco * 0.5)) {
            throw new IllegalArgumentException("O valor do desconto do cupom não pode exceder 50% do preço do ingresso.");
        }

        IEventoBuilder builder = new EventoBuilderConcreto();

        Evento novoEvento = builder
                .nomeEvento(nomeEvento)
                .dataEvento(dataEvento)
                .local(local)
                .descricao(descricao)
                .categoria(categoria)
                .preco(preco)
                .capacidade(capacidade)
                .ingressosDisponiveis(capacidade)
                .imageUrl(imageUrl)
                .cupomCode(cupomCode)
                .cupomDiscountValue(cupomDiscountValue)
                .organizador(organizador)
                .organizadorId(organizador.getIdUsuario())
                .build();

        return this.save(novoEvento);
    }
}
