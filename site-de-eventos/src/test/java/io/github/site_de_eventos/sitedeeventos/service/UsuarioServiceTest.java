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

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrar_whenEmailAlreadyExists_shouldThrowRuntimeException() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(RuntimeException.class, () -> usuarioService.registrar(
                "Nome", "teste@email.com","12345678", "12345678900", "9999-9999",
                LocalDateTime.now(), "Cidade", "Endereço", false, null, null
        ));
    }

    @Test
    void registrar_newUser_shouldSaveUsuario() {
        when(usuarioRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        Usuario u = new Usuario();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(u);

        Usuario result = usuarioService.registrar(
                "Nome", "novo@email.com","12345678", "12345678900", "9999-9999",
                LocalDateTime.now(), "Cidade", "Endereço", false, null, null
        );

        assertNotNull(result);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void autenticar_existingEmail_shouldReturnUsuario() {
        Usuario u = new Usuario();
        u.setEmail("login@email.com");
        when(usuarioRepository.findByEmail("login@email.com")).thenReturn(Optional.of(u));

        Optional<Usuario> result = usuarioService.autenticar("login@email.com");

        assertTrue(result.isPresent());
        assertEquals("login@email.com", result.get().getEmail());
    }

    @Test
    void obterOuCriarOrganizadorPadrao_whenNotExists_shouldCreateAndSave() {
        when(usuarioRepository.findByEmail("contato@xogum.com")).thenReturn(Optional.empty());
        Organizador o = new Organizador();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(o);

        Organizador result = usuarioService.obterOuCriarOrganizadorPadrao();

        assertNotNull(result);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void save_shouldDelegateToRepository() {
        Usuario u = new Usuario();
        when(usuarioRepository.save(u)).thenReturn(u);

        Usuario result = usuarioService.save(u);

        assertSame(u, result);
        verify(usuarioRepository).save(u);
    }
}
