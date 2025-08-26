package io.github.site_de_eventos.sitedeeventos.repository;

import java.util.List;
import java.util.Optional;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;

/**
 * Interface que define o contrato para operações de persistência de dados para a entidade {@link Organizador}.
 * Esta interface estende as funcionalidades básicas de um repositório para atender às necessidades específicas
 * da entidade Organizador, que é uma especialização de {@link io.github.site_de_eventos.sitedeeventos.model.Usuario}.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public interface OrganizadorRepository {

	/**
	 * Salva ou atualiza um organizador no repositório.
	 * Se o organizador já existir, seus dados são atualizados. Se for novo, é adicionado.
	 *
	 * @param organizador O objeto {@link Organizador} a ser salvo.
	 * @return O organizador salvo.
	 */
	Organizador save(Organizador organizador);
	
	/**
	 * Busca um organizador pelo seu identificador único.
	 *
	 * @param id O ID (int) do organizador a ser buscado.
	 * @return Um {@link Optional} contendo o organizador encontrado, ou um Optional vazio se não for encontrado.
	 */
	Optional<Organizador> findById(int id);
	
	/**
	 * Busca um organizador pelo seu endereço de e-mail. A busca não diferencia maiúsculas de minúsculas.
	 *
	 * @param email O e-mail (String) do organizador a ser buscado.
	 * @return Um {@link Optional} contendo o organizador encontrado, ou um Optional vazio se não for encontrado.
	 */
	Optional<Organizador> findByEmail(String email);

	/**
	 * Retorna uma lista com todos os organizadores cadastrados no repositório.
	 *
	 * @return Uma {@link List} de {@link Organizador}.
	 */
	List<Organizador> findAll();

	/**
	 * Exclui um organizador do repositório com base no seu ID.
	 *
	 * @param id O ID (int) do organizador a ser excluído.
	 * @return {@code true} se o organizador foi encontrado e excluído com sucesso, {@code false} caso contrário.
	 */
	boolean deleteById(int id);
}
