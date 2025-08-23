package io.github.site_de_eventos.sitedeeventos.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.UsuarioBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(String nome, String email, String cpf, String telefone, LocalDateTime dataNascimento,
                             String cidade, String endereco, boolean isOrganizador, String cnpj, String contaBancaria) {
        
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Erro: O email '" + email + "' já está cadastrado.");
        }

        if (isOrganizador) {
            IOrganizadorBuilder builder = new OrganizadorBuilderConcreto();
            Organizador novoOrganizador = ((OrganizadorBuilderConcreto) builder
                .nome(nome)
                .email(email)
                .telefone(telefone)
                .dataNascimento(dataNascimento)
                .cidade(cidade)
                .endereco(endereco))
                .cnpj(cnpj)
                .contaBancaria(contaBancaria)
                .build();
            return usuarioRepository.save((Organizador)novoOrganizador);
        } else {
            IUsuarioBuilder builder = new UsuarioBuilderConcreto();
            Usuario novoUsuario = builder
                .nome(nome)
                .email(email)
                .cpf(cpf)
                .telefone(telefone)
                .dataNascimento(dataNascimento)
                .cidade(cidade)
                .endereco(endereco)
                .build();
            return usuarioRepository.save(novoUsuario);
        }
    }

    public Optional<Usuario> autenticar(String email) {
        return usuarioRepository.findByEmail(email);
    }
    public Organizador obterOuCriarOrganizadorPadrao() {
        String emailPadrao = "contato@xogum.com";
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailPadrao);

        if (usuarioOpt.isPresent() && usuarioOpt.get() instanceof Organizador) {
            return (Organizador) usuarioOpt.get();
        } else {
            Organizador organizadorPadrao = (Organizador) new OrganizadorBuilderConcreto()
            		.nome("XOGUM Eventos")
            		.email(emailPadrao)
            		.build();
            return (Organizador) usuarioRepository.save(organizadorPadrao);
        }
    }
    
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}