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

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoService eventoService;

    @Test
    void index_shouldReturnIndexWithEventos() throws Exception {
        when(eventoService.buscarTodos()).thenReturn(Arrays.asList(new Evento()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("eventos"));
    }

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

    @Test
    void getEventosParaMapa_shouldReturnJson() throws Exception {
        when(eventoService.buscarTodos()).thenReturn(Arrays.asList(new Evento()));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
