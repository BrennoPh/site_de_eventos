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

/**
 * Classe de teste para o {@link PedidoController}.
 * <p>
 * Utiliza o {@code @WebMvcTest} para focar nos testes da camada web,
 * simulando requisições HTTP e verificando as respostas do controller.
 * As dependências de serviço são substituídas por mocks para isolar o controller.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    /**
     * Objeto principal para a execução de testes no Spring MVC.
     * Permite a simulação de requisições (GET, POST, etc.) e a verificação
     * das respostas (status, view, modelo).
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock do serviço de pedidos. É usado para simular o comportamento
     * da lógica de negócio de pedidos sem executá-la de fato.
     */
    @MockBean
    private PedidoService pedidoService;

    /**
     * Mock do serviço de eventos. Necessário porque o controller de pedidos
     * interage com este serviço para obter informações dos eventos.
     */
    @MockBean
    private EventoService eventoService;

    /**
     * Testa a exibição da página de criação de pedido para um evento existente.
     * Verifica se o status da resposta é OK, se a view "pedido" é renderizada
     * e se o modelo contém o atributo "evento".
     *
     * @throws Exception se ocorrer um erro durante a requisição simulada.
     */
    @Test
    void exibirPaginaPedido_eventoExistente_shouldReturnPedidoView() throws Exception {
        when(eventoService.buscarPorId(1)).thenReturn(Optional.of(new Evento()));

        mockMvc.perform(get("/pedidos/evento/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pedido"))
                .andExpect(model().attributeExists("evento"));
    }

    /**
     * Testa o processamento de um pedido por um usuário não autenticado.
     * Verifica se a tentativa de POST para "/pedidos" redireciona o usuário
     * para a página de login ("/login").
     *
     * @throws Exception se ocorrer um erro durante a requisição simulada.
     */
    @Test
    void processarPedido_usuarioNaoLogado_shouldRedirectLogin() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .param("eventoId", "1")
                        .param("usuarioId", "1")
                        .param("quantidade", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Testa o processamento de um pedido bem-sucedido por um usuário logado.
     * Simula uma sessão de usuário e verifica se, após o POST, o usuário
     * é redirecionado para a página "/meus-eventos".
     *
     * @throws Exception se ocorrer um erro durante a requisição simulada.
     */
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
