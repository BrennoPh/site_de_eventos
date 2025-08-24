package io.github.site_de_eventos.sitedeeventos.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;

class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_nullEvento_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventoService.save(null));
    }

    @Test
    void save_validEvento_shouldCallRepositorySave() {
        Evento e = new Evento();
        when(eventoRepository.save(e)).thenReturn(e);

        Evento result = eventoService.save(e);

        assertNotNull(result);
        verify(eventoRepository, times(1)).save(e);
    }

    @Test
    void buscarTodos_shouldReturnListFromRepository() {
        when(eventoRepository.findAll()).thenReturn(Arrays.asList(new Evento(), new Evento()));
        List<Evento> result = eventoService.buscarTodos();

        assertEquals(2, result.size());
        verify(eventoRepository).findAll();
    }

    @Test
    void buscarPorId_invalidId_shouldReturnEmptyOptional() {
        assertTrue(eventoService.buscarPorId(0).isEmpty());
    }

    @Test
    void buscarPorId_validId_shouldDelegateToRepository() {
        Evento e = new Evento();
        when(eventoRepository.findById(1)).thenReturn(Optional.of(e));

        Optional<Evento> result = eventoService.buscarPorId(1);

        assertTrue(result.isPresent());
        verify(eventoRepository).findById(1);
    }

    @Test
    void buscarPorNome_shouldDelegateToRepository() {
        when(eventoRepository.findByNomeContaining("rock")).thenReturn(Arrays.asList(new Evento()));

        List<Evento> result = eventoService.buscarPorNome("rock");

        assertEquals(1, result.size());
        verify(eventoRepository).findByNomeContaining("rock");
    }
}
