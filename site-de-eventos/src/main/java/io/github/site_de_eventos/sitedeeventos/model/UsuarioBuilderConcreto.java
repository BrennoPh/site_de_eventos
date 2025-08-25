package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;

/**
 * Implementação do padrão de projeto Builder para a classe {@link Usuario}.
 * <p>
 * Fornece uma API fluente para construir um objeto {@code Usuario} de forma
 * segura e legível.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class UsuarioBuilderConcreto implements IUsuarioBuilder {
    /**
     * A instância do Usuario que está sendo construída.
     */
    private Usuario usuario;

    /**
     * Construtor que inicializa um novo objeto {@link Usuario} para ser configurado.
     */
    public UsuarioBuilderConcreto() {
        this.usuario = new Usuario();
    }

    /**
     * Define o ID do usuário.
     * @param idUsuario (int) O ID a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder idUsuario(int idUsuario) {
        this.usuario.setIdUsuario(idUsuario);
        return this;
    }

    /**
     * Define o nome do usuário.
     * @param nome (String) O nome a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder nome(String nome) {
        this.usuario.setNome(nome);
        return this;
    }

    /**
     * Define o e-mail do usuário.
     * @param email (String) O e-mail a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder email(String email) {
        this.usuario.setEmail(email);
        return this;
    }

    /**
     * Define o CPF do usuário.
     * @param cpf (String) O CPF a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder cpf(String cpf) {
        this.usuario.setCpf(cpf);
        return this;
    }

    /**
     * Define o telefone do usuário.
     * @param telefone (String) O telefone a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder telefone(String telefone) {
        this.usuario.setTelefone(telefone);
        return this;
    }

    /**
     * Define a cidade do usuário.
     * @param cidade (String) A cidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder cidade(String cidade) {
        this.usuario.setCidade(cidade);
        return this;
    }

    /**
     * Define o endereço do usuário.
     * @param endereco (String) O endereço a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder endereco(String endereco) {
        this.usuario.setEndereco(endereco);
        return this;
    }

    /**
     * Define a data de nascimento do usuário.
     * @param dataNascimento (LocalDateTime) A data a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder dataNascimento(LocalDateTime dataNascimento) {
        this.usuario.setDataNascimento(dataNascimento);
        return this;
    }

    /**
     * Adiciona um pedido à lista de pedidos do usuário.
     * @param pedidos ({@link Pedido}) O pedido a ser adicionado.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    public IUsuarioBuilder pedidos(Pedido pedidos) {
        this.usuario.adicionarPedido(pedidos);
        return this;
    }

    /**
     * Constrói e retorna o objeto {@link Usuario} final.
     * @return O objeto {@link Usuario} configurado.
     */
    public Usuario build() {
        return this.usuario;
    }
}