package io.github.site_de_eventos.sitedeeventos.model.builder;

import java.time.LocalDateTime;
import java.util.List;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;

/**
 * Define o contrato para a construção de objetos {@link Usuario} utilizando o padrão de projeto Builder.
 * <p>
 * Esta interface estabelece os métodos que uma implementação de builder de usuário
 * deve fornecer, permitindo a criação de um objeto {@code Usuario} passo a passo.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 * @see io.github.site_de_eventos.sitedeeventos.model.UsuarioBuilderConcreto
 */
public interface IUsuarioBuilder {

    /**
     * Define o ID do usuário.
     * @param idUsuario (int) O ID a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder idUsuario(int idUsuario);

    /**
     * Define o nome do usuário.
     * @param nome (String) O nome a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder nome(String nome);

    /**
     * Define o e-mail do usuário.
     * @param email (String) O e-mail a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder email(String email);

    /**
     * Define o e-mail do usuário.
     * @param senha (String) A senha a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder senha(String senha);

    /**
     * Define o CPF do usuário.
     * @param cpf (String) O CPF a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder cpf(String cpf);

    /**
     * Define o telefone do usuário.
     * @param telefone (String) O telefone a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder telefone(String telefone);

    /**
     * Define a cidade do usuário.
     * @param cidade (String) A cidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder cidade(String cidade);

    /**
     * Define o endereço do usuário.
     * @param endereco (String) O endereço a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder endereco(String endereco);

    /**
     * Define a data de nascimento do usuário.
     * @param dataNascimento (LocalDateTime) A data de nascimento a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder dataNascimento(LocalDateTime dataNascimento);

    /**
     * Adiciona um pedido à lista de pedidos do usuário.
     * @param pedidos ({@link Pedido}) O pedido a ser adicionado.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder pedidos(Pedido pedidos);
    /**
     * Define a lista completa de pedidos para o usuário.
     * Útil para carregar um usuário já existente com todos os seus pedidos.
     * @param pedidos (List<Pedido>) A lista de pedidos a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder pegaPedidos(List<Pedido> pedidos);
    /**
     * Finaliza a construção e retorna o objeto {@link Usuario} completo.
     * @return O objeto {@link Usuario} construído.
     */
    public Usuario build();
}
