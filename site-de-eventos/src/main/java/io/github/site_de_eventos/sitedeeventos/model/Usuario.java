package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;

/**
 * Representa a entidade genérica de um usuário no sistema.
 * <p>
 * Esta classe armazena informações cadastrais de um usuário e serve como
 * base para tipos mais específicos, como {@link Organizador}.
 * <p>
 * Os campos anotados com {@code @Expose} são para a conversão do objeto para 
 * o formato JSON e vice-versa.
 * * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */

public class Usuario {
    /**
     * Identificador único do usuário.
     */
    @Expose
    private int idUsuario;
    /**
     * Nome completo do usuário.
     */
    @Expose
    private String nome;
    /**
     * Endereço de e-mail do usuário.
     */
    @Expose
    private String email;
    /**
     * senha do usuário.
     */
    @Expose
    private String senha;
    
    /**
     * Cadastro de Pessoa Física (CPF) do usuário.
     */
    @Expose
    private String cpf;
    /**
     * Número de telefone para contato.
     */
    @Expose
    private String telefone;
    /**
     * Cidade de residência do usuário.
     */
    @Expose
    private String cidade;
    /**
     * Endereço residencial do usuário.
     */
    @Expose
    private String endereco;
    /**
     * Data de nascimento do usuário.
     */
    @Expose
    private LocalDateTime dataNascimento;
    /**
     * Lista de pedidos realizados pelo usuário.
     * A anotação {@code @JsonManagedReference} evita loops de serialização com a classe Pedido.
     */
    @Expose
    @JsonManagedReference
    private List<Pedido> pedidos;

    /**
     * Construtor padrão que inicializa a lista de pedidos para evitar NullPointerException.
     */
    public Usuario() { 
        this.pedidos = new ArrayList<>();
    }

    /**
     * Obtém o identificador único do usuário.
     * @return O ID (int) do usuário.
     */
	public int getIdUsuario() {
		return idUsuario;
	}

    /**
     * Obtém o nome completo do usuário.
     * @return O nome (String) do usuário.
     */
	public String getNome() {
		return nome;
	}
	
    /**
     * Obtém o e-mail do usuário.
     * @return O e-mail (String) do usuário.
     */
	public String getEmail() {
		return email;
	}
     /**
     * Obtém o e-mail do usuário.
     * @return A senha (String) do usuário.
     */
	public String getSenha() {
		return senha;
	}

    /**
     * Obtém o CPF do usuário.
     * @return O CPF (String) do usuário.
     */
	public String getCpf() {
		return cpf;
	}

    /**
     * Obtém o telefone do usuário.
     * @return O telefone (String) do usuário.
     */
	public String getTelefone() {
		return telefone;
	}

    /**
     * Obtém a cidade do usuário.
     * @return A cidade (String) do usuário.
     */
	public String getCidade() {
		return cidade;
	}

    /**
     * Obtém o endereço do usuário.
     * @return O endereço (String) do usuário.
     */
	public String getEndereco() {
		return endereco;
	}

    /**
     * Obtém a data de nascimento do usuário.
     * @return A data de nascimento (LocalDateTime) do usuário.
     */
	public LocalDateTime getDataNascimento() {
		return dataNascimento;
	}

    /**
     * Obtém a lista de pedidos do usuário.
     * @return A lista (List) de {@link Pedido} do usuário.
     */
    public List<Pedido> getPedidos() {
        return this.pedidos;
    }

    /**
     * Define o identificador único do usuário.
     * @param idUsuario (int) O novo ID do usuário.
     */
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

    /**
     * Define o nome do usuário.
     * @param nome (String) O novo nome do usuário.
     */
	public void setNome(String nome) {
		this.nome = nome;
	}

    /**
     * Define o e-mail do usuário.
     * @param email (String) O novo e-mail do usuário.
     */
	public void setEmail(String email) {
		this.email = email;
	}
    /**
     * Define a senha do usuário.
     * @param senha (String) A nova senha do usuário.
     */
	public void setSenha(String senha) {
		this.senha = senha;
	}

    /**
     * Define o CPF do usuário.
     * @param cpf (String) O novo CPF do usuário.
     */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

    /**
     * Define o telefone do usuário.
     * @param telefone (String) O novo telefone do usuário.
     */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

    /**
     * Define a cidade do usuário.
     * @param cidade (String) A nova cidade do usuário.
     */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

    /**
     * Define o endereço do usuário.
     * @param endereco (String) O novo endereço do usuário.
     */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

    /**
     * Define a data de nascimento do usuário.
     * @param dataNascimento (LocalDateTime) A nova data de nascimento.
     */
	public void setDataNascimento(LocalDateTime dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
    /**
     * Adiciona um novo pedido à lista de pedidos do usuário.
     * @param pedido (Pedido) O pedido a ser adicionado.
     */
    public void adicionarPedido(Pedido pedido) {
        // Medida de segurança: garante que a lista de pedidos seja inicializada caso seja nula.
        if (this.pedidos == null) {
            this.pedidos = new ArrayList<>();
        }
        // Adiciona o novo pedido à lista existente.
        this.pedidos.add(pedido);
    }
    
    /**
     * Remove um pedido da lista de pedidos do usuário com base no ID do pedido.
     * @param pedidoId (int) O ID do pedido a ser removido.
     */
    public void removerPedido(int pedidoId) {
        // Verifica se a lista de pedidos existe para evitar NullPointerException.
        if (this.pedidos != null) {
            // Utiliza o método 'removeIf' com uma expressão lambda para remover o pedido
            // cujo ID corresponda ao 'pedidoId' fornecido.
            this.pedidos.removeIf(p -> p.getIdPedido() == pedidoId);
        }
    }

    /**
     * Define (substitui) a lista de pedidos do usuário.
     * @param pedidos (List<Pedido>) A nova lista de pedidos.
     */
	public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

}
