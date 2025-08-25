package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;
import java.util.List;

import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;


/**
 * Implementação do padrão de projeto Builder para a classe {@link Organizador}.
 * <p>
 * Fornece uma API fluente para construir um objeto {@code Organizador} de forma
 * segura e legível, incluindo atributos herdados de {@link Usuario}.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
public class OrganizadorBuilderConcreto extends UsuarioBuilderConcreto implements IOrganizadorBuilder {
    /**
     * A instância do Organizador que está sendo construída.
     */
    private Organizador organizador;

    /**
     * Construtor que inicializa um novo objeto {@link Organizador} para ser configurado.
     */
    public OrganizadorBuilderConcreto() {
        this.organizador = new Organizador();
    }

    /**
     * Define o ID do organizador.
     * @param idUsuario (int) O ID a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder idUsuario(int idUsuario) {
        this.organizador.setIdUsuario(idUsuario);
        return this;
    }

    /**
     * Define o nome do organizador.
     * @param nome (String) O nome a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder nome(String nome) {
        this.organizador.setNome(nome);
        return this;
    }

    /**
     * Define o e-mail do organizador.
     * @param email (String) O e-mail a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder email(String email) {
        this.organizador.setEmail(email);
        return this;
    }
    /**
     * Define o e-mail do usuário.
     * @param senha (String) A senha a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IUsuarioBuilder senha(String senha) {
        this.organizador.setSenha(senha);
        return this;
    }

    /**
     * Define o CPF do organizador.
     * @param cpf (String) O CPF a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder cpf(String cpf) {
        this.organizador.setCpf(cpf);
        return this;
    }

    /**
     * Define o telefone do organizador.
     * @param telefone (String) O telefone a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder telefone(String telefone) {
        this.organizador.setTelefone(telefone);
        return this;
    }

    /**
     * Define a cidade do organizador.
     * @param cidade (String) A cidade a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder cidade(String cidade) {
        this.organizador.setCidade(cidade);
        return this;
    }

    /**
     * Define o endereço do organizador.
     * @param endereco (String) O endereço a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder endereco(String endereco) {
        this.organizador.setEndereco(endereco);
        return this;
    }

    /**
     * Define a data de nascimento do organizador.
     * @param dataNascimento (LocalDateTime) A data a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder dataNascimento(LocalDateTime dataNascimento) {
        this.organizador.setDataNascimento(dataNascimento);
        return this;
    }

    /**
     * Define a conta bancária do organizador.
     * @param contaBancaria (String) A conta bancária a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder contaBancaria(String contaBancaria) {
        this.organizador.setContaBancaria(contaBancaria);
        return this;
    }

    /**
     * Define o CNPJ do organizador.
     * @param cnpj (String) O CNPJ a ser definido.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder cnpj(String cnpj) {
        this.organizador.setCnpj(cnpj);
        return this;
    }

    /**
     * Define o evento organizado.
     * @param eventoOrganizado ({@link Evento}) O evento a ser associado.
     * @return A própria instância do builder para chamadas encadeadas.
     */
    @Override
    public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado) {
        this.organizador.setEventoOrganizado(eventoOrganizado);
        return this;
    }
    /**
     * Define a lista completa de pedidos para o organizador.
     * @param pedidos (List<Pedido>) A lista de pedidos a ser definida.
     * @return A própria instância do builder para chamadas encadeadas.
     */
     @Override	
	public IUsuarioBuilder pegaPedidos(List<Pedido> pedidos){
    	// Chama o setter que substitui a lista de pedidos do organizador.
    	this.organizador.setPedidos(pedidos);
    	// Retorna a si mesmo.
    	return this;
	}
    /**
     * Constrói e retorna o objeto {@link Organizador} final.
     * @return O objeto {@link Organizador} configurado.
     */
    @Override
    public Organizador build() {
        return this.organizador;
    }
}
