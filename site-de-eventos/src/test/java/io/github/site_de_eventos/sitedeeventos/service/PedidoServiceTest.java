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

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Evento evento;

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

    @Test
    void criarPedido_DeveFuncionar_ComIngressosDisponiveis() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        List<String> nomes = List.of("Participante 1");
        List<String> emails = List.of("p1@email.com");

        Pedido pedido = pedidoService.criarPedido(1, 1, nomes, emails, null);

        assertNotNull(pedido);
        assertEquals(1, pedido.getQuantidadeIngressos());
        assertEquals(9, evento.getIngressosDisponiveis()); 
        verify(usuarioRepository, times(1)).save(usuario);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    void criarPedido_DeveLancarExcecao_QuandoNaoHaIngressosSuficientes() {
        evento.setIngressosDisponiveis(1); 
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));


        List<String> nomes = List.of("Participante 1", "Participante 2");
        List<String> emails = List.of("p1@email.com", "p2@email.com");


        Exception exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.criarPedido(1, 1, nomes, emails, null);
        });

        assertEquals("Não há ingressos suficientes. Disponíveis: 1", exception.getMessage());
    }
}