package sitedeeventos.repository;

import sitedeeventos.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
	Usuario save(Usuario usuario);

	Optional<Usuario> findById(int id);

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findAll();

	boolean deleteById(int id);
}
