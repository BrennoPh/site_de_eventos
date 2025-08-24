package io.github.site_de_eventos.sitedeeventos.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import io.github.site_de_eventos.sitedeeventos.service.PedidoService;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private EventoService eventoService;

    @Test
    void exibirPaginaPedido_eventoExistente_shouldReturnPedidoView() throws Exception {
        when(eventoService.buscarPorId(1)).thenReturn(Optional.of(new Evento()));

        mockMvc.perform(get("/pedidos/evento/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido"))
                .andExpect(model().attributeExists("evento"));
    }

    @Test
    void processarPedido_usuarioNaoLogado_shouldRedirectLogin() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .param("eventoId", "1")
                        .param("usuarioId", "1")
                        .param("quantidade", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void processarPedido_sucesso_shouldRedirectMeusEventos() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Usuario u = new Usuario();
        u.setIdUsuario(1);
        session.setAttribute("usuarioLogado", u);

        mockMvc.perform(post("/pedidos")
                        .param("eventoId", "1")
                        .param("usuarioId", "1")
                        .param("quantidade", "2")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meus-eventos"));
    }
}
