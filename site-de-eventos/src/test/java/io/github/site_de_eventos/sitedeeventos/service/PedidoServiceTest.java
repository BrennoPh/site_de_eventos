package io.github.site_de_eventos.sitedeeventos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Classe de teste para a {@link PedidoService}.
 * <p>
 * Utiliza o JUnit 5 e Mockito para realizar testes unitários no serviço de pedidos,
 * focando na lógica de criação de pedidos e validação de ingressos.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    /**
     * Mock do repositório de usuários para simular o acesso aos dados de usuários
     * sem a necessidade de um banco de dados real.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /**
     * Mock do repositório de eventos para simular o acesso aos dados de eventos.
     */
    @Mock
    private EventoRepository eventoRepository;

    /**
     * Instância do serviço de pedidos que será testada.
     * As dependências mockadas (repositórios) são injetadas nesta instância.
     */
    @InjectMocks
    private PedidoService pedidoService;

    /**
     * Objeto de usuário utilizado como base para os testes.
     */
    private Usuario usuario;

    /**
     * Objeto de evento utilizado como base para os testes.
     */
    private Evento evento;

    /**
     * Método de configuração executado antes de cada teste.
     * Inicializa os objetos {@code usuario} e {@code evento} com dados padrão
     * para garantir um estado limpo e consistente para cada cenário de teste.
     */
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");

        evento = new Evento();
        evento.setIdEvento(1);
        evento.setNomeEvento("Test Event");
        evento.setPreco(50.0);
        evento.setIngressosDisponiveis(10);
        evento.setCapacidade(10);
    }

    /**
     * Testa o cenário de sucesso da criação de um pedido.
     * Verifica se um pedido é criado corretamente quando há ingressos disponíveis,
     * se a quantidade de ingressos do evento é decrementada e se os métodos
     * de salvar dos repositórios são invocados.
     */
    @Test
    void criarPedido_DeveFuncionar_ComIngressosDisponiveis() {
        // Configuração do mock
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        List<String> nomes = List.of("Participante 1");
        List<String> emails = List.of("p1@email.com");

        // Execução do método a ser testado
        Pedido pedido = pedidoService.criarPedido(1, 1, nomes, emails, null);

        // Verificações
        assertNotNull(pedido);
        assertEquals(1, pedido.getQuantidadeIngressos());
        assertEquals(9, evento.getIngressosDisponiveis());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(eventoRepository, times(1)).save(evento);
    }

    /**
     * Testa o cenário de falha ao tentar criar um pedido sem ingressos suficientes.
     * Garante que uma {@link RuntimeException} seja lançada com a mensagem de erro
     * apropriada quando a quantidade de ingressos solicitada excede a disponibilidade.
     */
    @Test
    void criarPedido_DeveLancarExcecao_QuandoNaoHaIngressosSuficientes() {
        // Configuração do cenário
        evento.setIngressosDisponiveis(1);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));


        List<String> nomes = List.of("Participante 1", "Participante 2");
        List<String> emails = List.of("p1@email.com", "p2@email.com");

        // Execução e verificação da exceção
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(1, 1, nomes, emails, null);
        });

        assertEquals("Não há ingressos suficientes. Disponíveis: 1", exception.getMessage());
    }
}
