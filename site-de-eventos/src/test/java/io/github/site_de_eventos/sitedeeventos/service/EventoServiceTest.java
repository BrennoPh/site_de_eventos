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

/**
 * Classe de teste para a {@link EventoService}.
 * * Esta classe contém testes unitários que verificam o comportamento da classe
 * de serviço de eventos, utilizando Mockito para simular a camada de repositório.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
class EventoServiceTest {

    /**
     * Mock do repositório de eventos, injetado para simular o acesso a dados.
     */
    @Mock
    private EventoRepository eventoRepository;

    /**
     * Instância da classe de serviço a ser testada, com as dependências mockadas
     * injetadas automaticamente.
     */
    @InjectMocks
    private EventoService eventoService;

    /**
     * Configura o ambiente de teste antes da execução de cada método de teste.
     * Inicializa os mocks criados com a anotação {@code @Mock}.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa se o método {@code save} lança uma {@link IllegalArgumentException}
     * ao tentar salvar um evento nulo.
     */
    @Test
    void save_nullEvento_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventoService.save(null));
    }

    /**
     * Testa se o método {@code save} invoca o método {@code save} do repositório
     * quando um evento válido é fornecido.
     * Além disso, verifica se o evento retornado não é nulo.
     */
    @Test
    void save_validEvento_shouldCallRepositorySave() {
        Evento e = new Evento();
        when(eventoRepository.save(e)).thenReturn(e);

        Evento result = eventoService.save(e);

        assertNotNull(result);
        verify(eventoRepository, times(1)).save(e);
    }

    /**
     * Testa se o método {@code buscarTodos} retorna corretamente a lista de eventos
     * fornecida pelo repositório. Verifica o tamanho da lista e a chamada ao método
     * {@code findAll} do repositório.
     */
    @Test
    void buscarTodos_shouldReturnListFromRepository() {
        when(eventoRepository.findAll()).thenReturn(Arrays.asList(new Evento(), new Evento()));
        List<Evento> result = eventoService.buscarTodos();

        assertEquals(2, result.size());
        verify(eventoRepository).findAll();
    }

    /**
     * Testa se o método {@code buscarPorId} retorna um {@link Optional} vazio
     * quando um ID inválido (neste caso, 0) é fornecido, sem consultar o repositório.
     */
    @Test
    void buscarPorId_invalidId_shouldReturnEmptyOptional() {
        assertTrue(eventoService.buscarPorId(0).isEmpty());
    }

    /**
     * Testa se o método {@code buscarPorId} delega a chamada para o repositório
     * ao receber um ID válido e retorna o evento encapsulado em um {@link Optional}.
     */
    @Test
    void buscarPorId_validId_shouldDelegateToRepository() {
        Evento e = new Evento();
        when(eventoRepository.findById(1)).thenReturn(Optional.of(e));

        Optional<Evento> result = eventoService.buscarPorId(1);

        assertTrue(result.isPresent());
        verify(eventoRepository).findById(1);
    }

    /**
     * Testa se o método {@code buscarPorNome} delega a chamada para o método
     * {@code findByNomeContaining} do repositório e retorna a lista de eventos resultante.
     */
    @Test
    void buscarPorNome_shouldDelegateToRepository() {
        when(eventoRepository.findByNomeContaining("rock")).thenReturn(Arrays.asList(new Evento()));

        List<Evento> result = eventoService.buscarPorNome("rock");

        assertEquals(1, result.size());
        verify(eventoRepository).findByNomeContaining("rock");
    }
}
