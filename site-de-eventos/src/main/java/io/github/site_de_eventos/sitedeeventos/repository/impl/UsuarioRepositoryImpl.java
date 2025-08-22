package io.github.site_de_eventos.sitedeeventos.repository.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
//A anotação Repository indica ao Spring que esta classe é um componente da camada de persistência.

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

	// Simula uma base de dados.
	private final Map<Integer, Usuario> database = new ConcurrentHashMap<>();
	public UsuarioRepositoryImpl() {
	}

	@Override
	public Usuario save(Usuario usuario) {
		database.put(usuario.getIdUsuario(), usuario);
		return usuario;
	}

	@Override
	public Optional<Usuario> findById(int id) {
		return Optional.ofNullable(database.get(id));
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		// Procura em todos os valores do mapa um usuário com o e-mail correspondente.
		return database.values().stream().filter(usuario -> usuario.getEmail().equalsIgnoreCase(email)).findFirst();
	}

	@Override
	public List<Usuario> findAll() {
		// Retorna uma nova lista contendo todos os usuários para evitar modificações externas na base de dados.
		return new ArrayList<>(database.values());
	}

	@Override
	public boolean deleteById(int id) {
		// O método remove retorna o objeto removido ou null se o id não existir.
		return database.remove(id) != null;
	}
}
