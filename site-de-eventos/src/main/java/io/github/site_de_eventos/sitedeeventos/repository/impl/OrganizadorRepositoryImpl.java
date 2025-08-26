package io.github.site_de_eventos.sitedeeventos.repository.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.repository.OrganizadorRepository;

/**
 * Implementação do {@link OrganizadorRepository} que utiliza um mapa em memória como banco de dados.
 * Esta classe gerencia uma coleção de organizadores e, no seu construtor, cria um organizador
 * "Admin" inicial para garantir que o sistema sempre tenha pelo menos um organizador disponível.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Repository
public class OrganizadorRepositoryImpl implements OrganizadorRepository {
	
    private final Map<Integer, Organizador> database = new ConcurrentHashMap<>();
	
    /**
     * Construtor que inicializa o repositório.
     * Cria e salva um organizador padrão ("Admin Organizador") para fins de demonstração e
     * para garantir que o sistema tenha um organizador inicial.
     */
    public OrganizadorRepositoryImpl(){
        Organizador organizadorInicial = ((OrganizadorBuilderConcreto) new OrganizadorBuilderConcreto()
                .idUsuario(1)
                .nome("Admin Organizador")
                .email("admin@evento.com")
                .dataNascimento(LocalDateTime.now()))
                .cnpj("00.111.222/0001-33")
                .contaBancaria("12345-6")
                .eventoOrganizado(null)
                .build();

            this.save(organizadorInicial);
    }
    
	/**
	 * Este método implementa uma operação "upsert": se um organizador com o mesmo ID já existir
	 * no mapa em memória, ele será substituído. Se não existir, será inserido.
	 * A chave utilizada no mapa é o ID do usuário do organizador.
	 *
	 * @param organizador O objeto {@link Organizador} a ser salvo no banco de dados em memória.
	 * @return O mesmo objeto {@link Organizador} que foi passado como parâmetro, após ser salvo.
	 */
	@Override
	public Organizador save(Organizador organizador) {
		database.put(organizador.getIdUsuario(), organizador);
		return organizador;
	}

    /**
     * A busca é realizada diretamente no {@code ConcurrentHashMap} usando o método {@code get(id)},
     * que é uma operação de alta performance (tempo de acesso constante). O resultado é envolvido
     * em um {@code Optional.ofNullable} para tratar de forma segura os casos em que o ID não
     * corresponde a nenhum organizador, evitando assim o risco de {@code NullPointerException}.
     *
     * @param id O identificador numérico único do organizador a ser localizado.
     * @return Um {@link Optional} contendo o {@link Organizador} se encontrado, ou um Optional vazio caso contrário.
     */
    @Override
    public Optional<Organizador> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

	/**
	 * Este método percorre a coleção de todos os organizadores armazenados em memória.
	 * Ele utiliza a API de Streams do Java para filtrar a coleção, comparando o e-mail de cada
	 * organizador com o e-mail fornecido. A comparação {@code equalsIgnoreCase} garante que a busca
	 * não diferencie letras maiúsculas de minúsculas. {@code findFirst()} encerra a busca
	 * assim que o primeiro resultado correspondente é encontrado, otimizando a performance.
	 *
	 * @param email O endereço de e-mail a ser utilizado como critério de busca.
	 * @return Um {@link Optional} contendo o primeiro {@link Organizador} encontrado com o e-mail correspondente,
	 * ou um Optional vazio se nenhum for encontrado.
	 */
	@Override
	public Optional<Organizador> findByEmail(String email) {
		// Procura em todos os valores do mapa um usuário com o e-mail correspondente.
		return database.values().stream().filter(organizador -> organizador.getEmail().equalsIgnoreCase(email)).findFirst();
	}

    /**
     * O método cria e retorna uma <strong>nova</strong> instância de {@code ArrayList} a partir dos valores
     * contidos no mapa do banco de dados. A criação de uma nova lista é uma boa prática de
     * encapsulamento, pois impede que modificações na lista retornada afetem
     * a coleção de dados original do repositório.
     *
     * @return Uma {@link List} contendo todos os objetos {@link Organizador} presentes no repositório.
     */
    @Override
    public List<Organizador> findAll() {
        return new ArrayList<>(database.values());
    }

    /**
     * Este método utiliza o retorno do método {@code database.remove(id)}. Este método
     * não apenas remove o par chave-valor do mapa, mas também retorna o valor que estava
     * associado à chave. Se a chave não existia, ele retorna {@code null}.
     * Portanto, a verificação {@code != null} é uma forma concisa e eficiente de confirmar
     * se um organizador foi de fato encontrado e removido da base de dados.
     *
     * @param id O ID do organizador a ser removido.
     * @return {@code true} se um organizador com o ID especificado foi encontrado e removido,
     * e {@code false} caso contrário.
     */
    @Override
    public boolean deleteById(int id) {
        return database.remove(id) != null;
    }

}
