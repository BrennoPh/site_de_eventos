package io.github.site_de_eventos.sitedeeventos.repository;

import java.util.List;
import java.util.Optional;

import io.github.site_de_eventos.sitedeeventos.model.Evento;

/**
 * Interface que define o contrato para operações de persistência de dados para a entidade {@link Evento}.
 * <p>
 * Esta abstração permite a implementação de diferentes estratégias de armazenamento (como banco de dados em memória,
 * arquivos JSON ou um banco de dados relacional) sem alterar a camada de serviço.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public interface EventoRepository {

	/**
	 * Salva ou atualiza um evento no repositório.
	 * Se o evento for novo (ID 0), um novo ID deve ser gerado e atribuído.
	 * Se o evento já existir (ID diferente de 0), seus dados devem ser atualizados.
	 *
	 * @param evento O objeto {@link Evento} a ser salvo.
	 * @return O evento salvo, possivelmente com o ID atualizado.
	 */
	Evento save(Evento evento);

	/**
	 * Busca um evento pelo seu identificador único.
	 *
	 * @param id O ID (int) do evento a ser buscado.
	 * @return Um {@link Optional} contendo o evento encontrado, ou um Optional vazio se nenhum evento for encontrado com o ID fornecido.
	 */
	Optional<Evento> findById(int id);

	/**
	 * Busca o primeiro evento que corresponde exatamente ao nome fornecido, ignorando diferenças de maiúsculas e minúsculas.
	 *
	 * @param nome O nome (String) exato do evento a ser buscado.
	 * @return Um {@link Optional} contendo o evento encontrado, ou um Optional vazio se nenhum corresponder ao nome.
	 */
	Optional<Evento> findByNome(String nome);

	/**
	 * Retorna uma lista com todos os eventos cadastrados no repositório.
	 *
	 * @return Uma {@link List} de {@link Evento}. A lista estará vazia se não houver eventos.
	 */
	List<Evento> findAll();

	/**
	 * Busca eventos cujo nome contenha o termo de busca fornecido. A busca não diferencia maiúsculas de minúsculas.
	 *
	 * @param termo O termo (String) a ser procurado no nome dos eventos.
	 * @return Uma {@link List} de {@link Evento} que correspondem ao critério de busca.
	 */
	List<Evento> findByNomeContaining(String termo);

	/**
	 * Exclui um evento do repositório com base no seu ID.
	 *
	 * @param id O ID (int) do evento a ser excluído.
	 * @return {@code true} se o evento foi encontrado e excluído com sucesso, {@code false} caso contrário.
	 */
	boolean deleteById(int id);

}
