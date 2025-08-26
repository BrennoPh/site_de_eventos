package io.github.site_de_eventos.sitedeeventos.repository;

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define o contrato para operações de persistência de dados para a entidade {@link Usuario}.
 * <p>
 * Serve como uma abstração para a camada de acesso a dados de usuários, permitindo que a forma de armazenamento
 * seja trocada sem impactar a lógica de negócio da aplicação.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public interface UsuarioRepository {

	/**
	 * Salva ou atualiza um usuário no repositório.
	 * Se o usuário for novo (ID 0), um novo ID é gerado. Se já existir, é atualizado.
	 *
	 * @param usuario O objeto {@link Usuario} a ser salvo.
	 * @return O usuário salvo, possivelmente com um novo ID.
	 */
	Usuario save(Usuario usuario);

	/**
	 * Busca um usuário pelo seu identificador único.
	 *
	 * @param id O ID (int) do usuário a ser buscado.
	 * @return Um {@link Optional} contendo o usuário, ou vazio se não for encontrado.
	 */
	Optional<Usuario> findById(int id);

	/**
	 * Busca um usuário pelo seu endereço de e-mail. A busca deve ser insensível a maiúsculas/minúsculas.
	 *
	 * @param email O e-mail (String) do usuário a ser buscado.
	 * @return Um {@link Optional} contendo o usuário, ou vazio se não for encontrado.
	 */
	Optional<Usuario> findByEmail(String email);

	/**
	 * Retorna uma lista com todos os usuários cadastrados.
	 *
	 * @return Uma {@link List} de {@link Usuario}.
	 */
	List<Usuario> findAll();

	/**
	 * Exclui um usuário do repositório com base no seu ID.
	 *
	 * @param id O ID (int) do usuário a ser excluído.
	 * @return {@code true} se o usuário foi excluído com sucesso, {@code false} caso contrário.
	 */
	boolean deleteById(int id);
}
