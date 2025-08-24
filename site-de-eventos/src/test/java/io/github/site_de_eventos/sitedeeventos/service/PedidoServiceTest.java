package io.github.site_de_eventos.sitedeeventos.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

class PedidoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPedido_validData_shouldReturnPedidoConcluidoAndDecreaseIngressos() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Evento evento = new Evento();
        evento.setIdEvento(10);
        evento.setPreco(50.0);
        evento.setCapacidade(100);
        evento.setIngressosDisponiveis(10);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(10)).thenReturn(Optional.of(evento));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Pedido pedido = pedidoService.criarPedido(1, 10, 2, null);

        assertNotNull(pedido);
        assertEquals("CONCLUIDO", pedido.getStatus());
        assertEquals(8, evento.getIngressosDisponiveis());
        assertEquals(2, pedido.getQuantidadeIngressos());
        assertTrue(pedido.getValorTotal() > 0);

        verify(usuarioRepository).save(usuario);
        verify(eventoRepository).save(evento);
    }

    @Test
    void criarPedido_notEnoughIngressos_shouldThrowRuntimeException() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);

        Evento evento = new Evento();
        evento.setIdEvento(10);
        evento.setPreco(50.0);
        evento.setCapacidade(100);
        evento.setIngressosDisponiveis(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(10)).thenReturn(Optional.of(evento));

        assertThrows(RuntimeException.class, () -> pedidoService.criarPedido(1, 10, 5, null));

        verify(usuarioRepository, never()).save(any());
        verify(eventoRepository, never()).save(any());
    }
}
