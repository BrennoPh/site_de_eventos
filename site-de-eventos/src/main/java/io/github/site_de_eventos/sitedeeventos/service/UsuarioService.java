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

/**
 * Classe de serviço para gerenciar a lógica de negócio de Usuários e Organizadores.
 * <p>
 * Responsável por operações como registro, autenticação e persistência de dados
 * de usuários, utilizando o padrão de projeto Builder para a construção dos objetos.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Registra um novo usuário ou organizador no sistema.
     * <p>
     * Utiliza o padrão Builder para construir o objeto apropriado ({@link Usuario} ou {@link Organizador})
     * com base no parâmetro {@code isOrganizador}. Antes de criar, verifica se o e-mail
     * fornecido já está em uso, lançando uma exceção em caso afirmativo.
     *
     * @param nome             O nome completo do usuário.
     * @param email            O e-mail do usuário (deve ser único).
     * @param senha            A senha para acesso.
     * @param cpf              O CPF do usuário (usado apenas para clientes).
     * @param telefone         O número de telefone de contato.
     * @param dataNascimento   A data de nascimento do usuário.
     * @param cidade           A cidade de residência.
     * @param endereco         O endereço de residência.
     * @param isOrganizador    Um booleano que indica se o registro é para um organizador.
     * @param cnpj             O CNPJ (usado apenas para organizadores).
     * @param contaBancaria    A conta bancária (usada apenas para organizadores).
     * @return O objeto {@link Usuario} ou {@link Organizador} que foi salvo no banco de dados.
     * @throws RuntimeException se o e-mail já estiver cadastrado.
     */
    public Usuario registrar(String nome, String email, String senha, String cpf, String telefone, LocalDateTime dataNascimento,
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
                    .endereco(endereco)
                    .senha(senha))
                    .cnpj(cnpj)
                    .contaBancaria(contaBancaria)
                    .build();
            return usuarioRepository.save((Organizador) novoOrganizador);
        } else {
            IUsuarioBuilder builder = new UsuarioBuilderConcreto();
            Usuario novoUsuario = builder
                    .nome(nome)
                    .email(email)
                    .senha(senha)
                    .cpf(cpf)
                    .telefone(telefone)
                    .dataNascimento(dataNascimento)
                    .cidade(cidade)
                    .endereco(endereco)
                    .build();
            return usuarioRepository.save(novoUsuario);
        }
    }

    /**
     * Autentica um usuário com base no e-mail.
     *
     * @param email O e-mail do usuário a ser autenticado.
     * @return um {@link Optional} contendo o {@link Usuario} se encontrado, ou um Optional vazio caso contrário.
     */
    public Optional<Usuario> autenticar(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtém o organizador padrão do sistema. Se ele não existir, cria e salva um novo.
     * <p>
     * Este método garante a existência de um organizador "fallback" para o sistema,
     * com dados pré-definidos.
     *
     * @return O objeto {@link Organizador} padrão do sistema.
     */
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

    /**
     * Salva ou atualiza uma entidade de usuário no banco de dados.
     *
     * @param usuario O objeto {@link Usuario} a ser salvo.
     * @return O objeto {@link Usuario} após a operação de salvamento.
     */
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
