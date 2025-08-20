package sitedeeventos.model.builder;

import sitedeeventos.model.Usuario;
import java.time.LocalDateTime;

public interface IUsuarioBuilder {
	public IUsuarioBuilder idUsuario(int idUsuario);
	public IUsuarioBuilder nome(String nome);
	public IUsuarioBuilder email(String email);
	public IUsuarioBuilder cpf(String cpf);
	public IUsuarioBuilder telefone(String telefone);
	public IUsuarioBuilder cidade(String cidade);
	public IUsuarioBuilder endereco(String endereco);
	public IUsuarioBuilder dataNascimento(LocalDateTime dataNascimento);
	public Usuario build();
}
