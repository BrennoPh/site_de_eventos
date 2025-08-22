package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;

import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;

public class UsuarioBuilderConcreto implements IUsuarioBuilder{
	private Usuario usuario;

	public UsuarioBuilderConcreto() {
		this.usuario = new Usuario();
	}
	
	public IUsuarioBuilder idUsuario(int idUsuario) {
		this.usuario.setIdUsuario(idUsuario);
		return this;
	}
	public IUsuarioBuilder nome(String nome) {
		this.usuario.setNome(nome);
		return this;
	}
	public IUsuarioBuilder email(String email) {
		this.usuario.setEmail(email);
		return this;
	}
	public IUsuarioBuilder cpf(String cpf) {
		this.usuario.setCpf(cpf);
		return this;
	}
	public IUsuarioBuilder telefone(String telefone) {
		this.usuario.setTelefone(telefone);
		return this;
	}
	public IUsuarioBuilder cidade(String cidade) {
		this.usuario.setCidade(cidade);
		return this;
	}
	public IUsuarioBuilder endereco(String endereco) {
		this.usuario.setEndereco(endereco);
		return this;
	}
	public IUsuarioBuilder dataNascimento(LocalDateTime dataNascimento) {
		this.usuario.setDataNascimento(dataNascimento);
		return this;
	}
	public IUsuarioBuilder pedidos(Pedido pedidos) {
		this.usuario.adicionarPedido(pedidos);
		return this;
	}
	public Usuario build() {
		return this.usuario;
	}
	

}
