package io.github.site_de_eventos.sitedeeventos.model;

import java.time.LocalDateTime;

import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;

public class OrganizadorBuilderConcreto extends UsuarioBuilderConcreto implements IOrganizadorBuilder{
	private Organizador organizador;

	public OrganizadorBuilderConcreto() {
		this.organizador = new Organizador();
	}

	  @Override
    public IOrganizadorBuilder idUsuario(int idUsuario) {
        this.organizador.setIdUsuario(idUsuario);
        return this;
    }

    @Override
    public IOrganizadorBuilder nome(String nome) {
        this.organizador.setNome(nome);
        return this;
    }

    @Override
    public IOrganizadorBuilder email(String email) {
        this.organizador.setEmail(email);
        return this;
    }

    @Override
    public IOrganizadorBuilder cpf(String cpf) {
        this.organizador.setCpf(cpf);
        return this;
    }

    @Override
    public IOrganizadorBuilder telefone(String telefone) {
        this.organizador.setTelefone(telefone);
        return this;
    }

    @Override
    public IOrganizadorBuilder cidade(String cidade) {
        this.organizador.setCidade(cidade);
        return this;
    }

    @Override
    public IOrganizadorBuilder endereco(String endereco) {
        this.organizador.setEndereco(endereco);
        return this;
    }

    @Override
    public IOrganizadorBuilder dataNascimento(LocalDateTime dataNascimento) {
        this.organizador.setDataNascimento(dataNascimento);
        return this;
    }
	@Override
	public IOrganizadorBuilder contaBancaria(String contaBancaria) {
		this.organizador.setContaBancaria(contaBancaria);
		return this;
	}
	@Override
	public IOrganizadorBuilder cnpj(String cnpj) {
		this.organizador.setCnpj(cnpj);
		return this;
	}
	@Override
	public IOrganizadorBuilder eventoOrganizado(Evento eventoOrganizado) {
		this.organizador.setEventoOrganizado(eventoOrganizado);
		return this;
	}
	@Override
	public Organizador build() {
		return this.organizador;
	}
}
