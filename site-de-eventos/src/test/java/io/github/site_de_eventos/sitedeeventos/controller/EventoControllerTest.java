package io.github.site_de_eventos.sitedeeventos.controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;

/**
 * Classe de teste para o {@link EventoController}.
 * <p>
 * Focada em testar a camada web (MVC) de forma isolada, utilizando
 * {@code @WebMvcTest}. As dependências de serviço são mockadas com
 * {@code @MockBean} para garantir que apenas o controller seja testado.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@WebMvcTest(EventoController.class)
class EventoControllerTest {

    /**
     * Instância do MockMvc, injetada pelo Spring Test, para simular
     * requisições HTTP para os endpoints do controller sem a necessidade
     * de um servidor web real.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Cria um mock do {@link EventoService} no contexto da aplicação.
     * Isso permite definir o comportamento esperado do serviço durante os testes
     * do controller, isolando-o da lógica de negócio.
     */
    @MockBean
    private EventoService eventoService;

    /**
     * Testa o endpoint da página inicial ("/").
     * Verifica se o controller retorna o status HTTP 200 (OK), renderiza a view "index"
     * e adiciona um atributo chamado "eventos" ao modelo.
     *
     * @throws Exception se ocorrer um erro durante a performance da requisição.
     */
    @Test
    void index_shouldReturnIndexWithEventos() throws Exception {
        when(eventoService.buscarTodos()).thenReturn(Arrays.asList(new Evento()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("eventos"));
    }

    /**
     * Testa o endpoint de exibição de detalhes de um evento ("/evento/{id}").
     * Simula o caso em que o evento é encontrado e verifica se o controller
     * retorna o status OK, a view "detalhes-evento" e o atributo "evento" no modelo.
     *
     * @throws Exception se ocorrer um erro durante a performance da requisição.
     */
    @Test
    void exibirDetalhesEvento_found_shouldReturnViewWithEvento() throws Exception {
        Evento evento = new Evento();
        evento.setNomeEvento("Show Rock");
        when(eventoService.buscarPorId(1)).thenReturn(Optional.of(evento));

        mockMvc.perform(get("/evento/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("detalhes-evento"))
                .andExpect(model().attributeExists("evento"));
    }

    /**
     * Testa o endpoint da API que retorna a lista de eventos em formato JSON ("/api/eventos").
     * Verifica se a resposta tem o status OK e se o corpo da resposta é um array JSON.
     *
     * @throws Exception se ocorrer um erro durante a performance da requisição.
     */
    @Test
    void getEventosParaMapa_shouldReturnJson() throws Exception {
        when(eventoService.buscarTodos()).thenReturn(Arrays.asList(new Evento()));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
