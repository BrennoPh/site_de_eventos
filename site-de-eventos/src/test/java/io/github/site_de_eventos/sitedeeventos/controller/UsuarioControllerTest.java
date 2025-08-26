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

/**
 * Classe de teste para o {@link UsuarioController}.
 * <p>
 * Testa a camada web para as funcionalidades de usuário, como cadastro, login e logout.
 * Utiliza {@code @WebMvcTest} para testar o controller de forma isolada, com
 * a camada de serviço sendo mockada através de {@code @MockBean}.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    /**
     * Instância do MockMvc para simular requisições HTTP aos endpoints do
     * {@link UsuarioController} e validar as respostas.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock do {@link UsuarioService} para simular a lógica de negócio
     * e as interações com a camada de dados, isolando o controller nos testes.
     */
    @MockBean
    private UsuarioService usuarioService;

    /**
     * Testa a exibição do formulário de cadastro.
     * Verifica se uma requisição GET para "/cadastro" retorna o status HTTP 200 (OK)
     * e renderiza a view "cadastro".
     *
     * @throws Exception se ocorrer um erro durante a requisição.
     */
    @Test
    void exibirFormularioCadastro_shouldReturnCadastroView() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    /**
     * Testa o processamento de um cadastro de usuário com dados válidos.
     * Verifica se, após um POST para "/cadastro", o usuário é direcionado
     * para a view "login" com uma mensagem de sucesso no modelo.
     *
     * @throws Exception se ocorrer um erro durante a requisição.
     */
    @Test
    void processarCadastro_validData_shouldReturnLoginView() throws Exception {
        when(usuarioService.registrar(any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean(), any(), any()))
                .thenReturn(new Usuario());

        mockMvc.perform(post("/cadastro")
                        .param("nome", "Teste")
                        .param("email", "teste@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("sucesso"));
    }

    /**
     * Testa o processo de login para um usuário existente e válido.
     * Verifica se, após um POST para "/login" com credenciais corretas,
     * o usuário é redirecionado para a página inicial ("/").
     *
     * @throws Exception se ocorrer um erro durante a requisição.
     */
    @Test
    void processarLogin_usuarioExistente_shouldRedirectIndex() throws Exception {
        Usuario u = new Usuario();
        when(usuarioService.autenticar("teste@email.com")).thenReturn(Optional.of(u));

        mockMvc.perform(post("/login")
                        .param("email", "teste@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    /**
     * Testa o processo de login para um usuário não existente.
     * Verifica se a tentativa de login com um e-mail inválido retorna
     * para a view "login" com uma mensagem de erro no modelo.
     *
     * @throws Exception se ocorrer um erro durante a requisição.
     */
    @Test
    void processarLogin_usuarioNaoEncontrado_shouldReturnLoginView() throws Exception {
        when(usuarioService.autenticar("naoexiste@email.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("email", "naoexiste@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("erro"));
    }

    /**
     * Testa o processo de logout do usuário.
     * Simula uma sessão ativa e verifica se a requisição GET para "/logout"
     * invalida a sessão e redireciona o usuário para a página inicial.
     *
     * @throws Exception se ocorrer um erro durante a requisição.
     */
    @Test
    void processarLogout_shouldInvalidateSessionAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogado", new Usuario());

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
