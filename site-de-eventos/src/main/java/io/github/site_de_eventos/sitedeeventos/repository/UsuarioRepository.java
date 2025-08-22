package io.github.site_de_eventos.sitedeeventos.repository;

import java.util.List;
import java.util.Optional;

import io.github.site_de_eventos.sitedeeventos.model.Usuario;

public interface UsuarioRepository {
	Usuario save(Usuario usuario);

	Optional<Usuario> findById(int id);

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findAll();

	boolean deleteById(int id);
}
