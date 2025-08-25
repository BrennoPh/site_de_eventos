package io.github.site_de_eventos.sitedeeventos.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void exibirFormularioCadastro_shouldReturnCadastroView() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    @Test
    void processarCadastro_validData_shouldReturnLoginView() throws Exception {
        when(usuarioService.registrar(any(), any(),any(), any(), any(), any(), any(), any(), anyBoolean(), any(), any()))
                .thenReturn(new Usuario());

        mockMvc.perform(post("/cadastro")
                        .param("nome", "Teste")
                        .param("email", "teste@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("sucesso"));
    }

    @Test
    void processarLogin_usuarioExistente_shouldRedirectIndex() throws Exception {
        Usuario u = new Usuario();
        when(usuarioService.autenticar("teste@email.com")).thenReturn(Optional.of(u));

        mockMvc.perform(post("/login")
                        .param("email", "teste@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void processarLogin_usuarioNaoEncontrado_shouldReturnLoginView() throws Exception {
        when(usuarioService.autenticar("naoexiste@email.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("email", "naoexiste@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    void processarLogout_shouldInvalidateSessionAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogado", new Usuario());

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
