package io.github.site_de_eventos.sitedeeventos.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

/**
 * Classe de teste para a {@link UsuarioService}.
 * <p>
 * Esta classe contém testes unitários para a camada de serviço de usuários,
 * utilizando Mockito para simular as dependências do repositório e validar
 * a lógica de negócio de forma isolada.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
class UsuarioServiceTest {

    /**
     * Mock do repositório de usuários, usado para simular o comportamento
     * da camada de persistência de dados.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /**
     * Instância do serviço a ser testado. O Mockito injetará o mock
     * do {@code usuarioRepository} nesta instância.
     */
    @InjectMocks
    private UsuarioService usuarioService;

    /**
     * Método de configuração executado antes de cada teste.
     * Ele inicializa os mocks anotados nesta classe de teste.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa se o método {@code registrar} lança uma {@link RuntimeException}
     * quando se tenta registrar um usuário com um e-mail que já existe no sistema.
     */
    @Test
    void registrar_whenEmailAlreadyExists_shouldThrowRuntimeException() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(
                "Nome", "teste@email.com", "12345678", "12345678900", "9999-9999",
                LocalDateTime.now(), "Cidade", "Endereço", false, null, null
        ));
    }

    /**
     * Testa o fluxo de sucesso do registro de um novo usuário.
     * Verifica se o método {@code save} do repositório é chamado quando
     * um novo usuário é registrado com um e-mail único.
     */
    @Test
    void registrar_newUser_shouldSaveUsuario() {
        when(usuarioRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        Usuario u = new Usuario();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(u);

        Usuario result = usuarioService.registrar(
                "Nome", "novo@email.com", "12345678", "12345678900", "9999-9999",
                LocalDateTime.now(), "Cidade", "Endereço", false, null, null
        );

        assertNotNull(result);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    /**
     * Testa a funcionalidade de autenticação.
     * Verifica se o método {@code autenticar} retorna um {@link Optional} contendo
     * o usuário correto quando um e-mail existente é fornecido.
     */
    @Test
    void autenticar_existingEmail_shouldReturnUsuario() {
        Usuario u = new Usuario();
        u.setEmail("login@email.com");
        when(usuarioRepository.findByEmail("login@email.com")).thenReturn(Optional.of(u));

        Optional<Usuario> result = usuarioService.autenticar("login@email.com");

        assertTrue(result.isPresent());
        assertEquals("login@email.com", result.get().getEmail());
    }

    /**
     * Testa a lógica de criação do organizador padrão do sistema.
     * Garante que, se o organizador padrão não existir, ele será criado
     * e salvo no repositório.
     */
    @Test
    void obterOuCriarOrganizadorPadrao_whenNotExists_shouldCreateAndSave() {
        when(usuarioRepository.findByEmail("contato@xogum.com")).thenReturn(Optional.empty());
        Organizador o = new Organizador();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(o);

        Organizador result = usuarioService.obterOuCriarOrganizadorPadrao();

        assertNotNull(result);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    /**
     * Testa se o método {@code save} da classe de serviço delega
     * corretamente a chamada para o método {@code save} do repositório.
     */
    @Test
    void save_shouldDelegateToRepository() {
        Usuario u = new Usuario();
        when(usuarioRepository.save(u)).thenReturn(u);

        Usuario result = usuarioService.save(u);

        assertSame(u, result);
        verify(usuarioRepository).save(u);
    }
}
