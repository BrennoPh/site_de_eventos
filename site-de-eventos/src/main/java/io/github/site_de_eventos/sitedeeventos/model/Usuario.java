package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
	private int idUsuario;
	private String nome;
	private String email;
	private String cpf;
	private String telefone;
	private String cidade;
	private String endereco;
	private LocalDateTime dataNascimento;
	private List<Pedido> pedidos;
	
	Usuario() {
	 this.pedidos = new ArrayList<>();
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getCpf() {
		return cpf;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getCidade() {
		return cidade;
	}

	public String getEndereco() {
		return endereco;
	}

	public LocalDateTime getDataNascimento() {
		return dataNascimento;
	}

    public List<Pedido> getPedidos() {
        return this.pedidos;
    }

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public void setDataNascimento(LocalDateTime dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public void adicionarPedido(Pedido pedido) {
        this.pedidos.add(pedido);
    }
	
	

}
