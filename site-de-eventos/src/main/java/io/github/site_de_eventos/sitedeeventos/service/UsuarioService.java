package sitedeeventos.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sitedeeventos.model.Usuario;
import sitedeeventos.repository.UsuarioRepository;

/**
 * Camada de Serviço para a entidade Usuario.
 * Contém as regras de negócio e orquestra as operações com o repositório.
 * Segue o Princípio da Responsabilidade Única (SOLID).
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Salva um novo usuário no sistema após realizar validações.
     *
     * @param usuario O objeto Usuario a ser salvo.
     * @return O usuário salvo, geralmente com o ID preenchido.
     * @throws IllegalArgumentException se o usuário for nulo, ou se o email já estiver em uso.
     */
    public Usuario salvar(Usuario usuario) {
        // 1. Validação de Regras de Negócio
        if (usuario == null) {
            throw new IllegalArgumentException("O objeto de usuário não pode ser nulo.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email do usuário é obrigatório.");
        }

        // 2. Verifica se já existe um usuário com o mesmo email para evitar duplicatas
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("O email '" + usuario.getEmail() + "' já está cadastrado.");
        }
        
        // Em um sistema real, aqui seria o local ideal para criptografar a senha do usuário
        // Ex: usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // 3. Delega a persistência para a camada de Repositório
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca um usuário pelo seu email.
     *
     * @param email O email a ser buscado.
     * @return um Optional contendo o usuário se encontrado, ou um Optional vazio caso contrário.
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email);
    }
}