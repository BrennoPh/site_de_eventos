package io.github.site_de_eventos.sitedeeventos.model.builder;

import java.time.LocalDateTime;

import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;

public interface IUsuarioBuilder {
	public IUsuarioBuilder idUsuario(int idUsuario);
	public IUsuarioBuilder nome(String nome);
	public IUsuarioBuilder email(String email);
	public IUsuarioBuilder cpf(String cpf);
	public IUsuarioBuilder telefone(String telefone);
	public IUsuarioBuilder cidade(String cidade);
	public IUsuarioBuilder endereco(String endereco);
	public IUsuarioBuilder dataNascimento(LocalDateTime dataNascimento);
	public IUsuarioBuilder pedidos(Pedido pedidos);
	public Usuario build();
}
