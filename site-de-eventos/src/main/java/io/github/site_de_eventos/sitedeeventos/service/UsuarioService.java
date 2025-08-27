package io.github.site_de_eventos.sitedeeventos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

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
    * Valida um CPF de acordo com o algoritmo oficial da Receita Federal.
    * Esta versão foi refinada para garantir a validação explícita de ambos os dígitos.
    * @param cpf O CPF a ser validado, podendo conter ou não pontuação.
    * @return {@code true} se o CPF for válido, {@code false} caso contrário.
    */
    private boolean isCpfValido(String cpf) {
        // Remove caracteres não numéricos (pontos e traço)
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica condições de invalidez conhecidas (tamanho e dígitos repetidos)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            // O peso vai de 10 a 2 para os 9 primeiros dígitos
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
        
            int resto = soma % 11;
            int digitoVerificador1 = (resto < 2) ? 0 : (11 - resto);

            // --- VERIFICAÇÃO DO PRIMEIRO DÍGITO ---
            // Compara o dígito calculado com o dígito real do CPF (posição 10, índice 9)
            if (digitoVerificador1 != (cpf.charAt(9) - '0')) {
                return false; // Se o primeiro dígito já for inválido, não precisa continuar.
            }

            // --- CÁLCULO DO SEGUNDO DÍGITO VERIFICADOR ---
            soma = 0;
            // O peso vai de 11 a 2 para os 10 primeiros dígitos (incluindo o primeiro dígito verificador)
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
        
            resto = soma % 11;
            int digitoVerificador2 = (resto < 2) ? 0 : (11 - resto);

            // --- VERIFICAÇÃO FINAL DO SEGUNDO DÍGITO ---
            // Compara o segundo dígito calculado com o dígito real do CPF (posição 11, índice 10)
            // O retorno final do método depende desta última comparação.
            return digitoVerificador2 == (cpf.charAt(10) - '0');

        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Verifica se o formato de uma string de e-mail é sintaticamente válido.
     * @param email A string contendo o e-mail a ser validado.
     * @return {@code true} se o e-mail tiver um formato válido;
     * {@code false} caso contrário (incluindo se for nulo ou em branco).
    */


    private boolean isEmailValido(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        // Regex padrão para validação de e-mail. Existem variações, mas esta cobre a maioria dos casos.
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * Registra um novo usuário ou organizador no sistema.
     * <p>
     * Utiliza o padrão Builder para construir o objeto apropriado ({@link Usuario} ou {@link Organizador})
     * com base no parâmetro {@code isOrganizador}. Antes de criar, verifica se o e-mail
     * fornecido já está em uso, lançando uma exceção em caso afirmativo.
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
     * @return                 O objeto {@link Usuario} ou {@link Organizador} que foi salvo no banco de dados.
     * @throws RuntimeException se o e-mail já estiver cadastrado.
    */

    public Usuario registrar(String nome, String email, String senha, String cpf, String telefone, LocalDateTime dataNascimento,
                             String cidade, String endereco, boolean isOrganizador, String cnpj, String contaBancaria) {

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Erro: O email '" + email + "' já está cadastrado.");
        }

        if (cpf == null || cpf.isBlank() || !isCpfValido(cpf)) {
                throw new RuntimeException("CPF inválido. Verifique os dados e tente novamente.");
        }

        if (!isEmailValido(email)) {
            throw new RuntimeException("Erro: E-mail inválido.");
        }
        
        String telefonePadronizado = "";
        if (telefone != null && !telefone.isBlank()) {
            String digitosTelefone = telefone.replaceAll("[^0-9]", ""); // Remove tudo que não for número
            if (digitosTelefone.length() < 10 || digitosTelefone.length() > 11) { // Aceita DDD + 8 ou 9 dígitos
                throw new RuntimeException("Telefone inválido. Forneça o DDD e o número com 8 ou 9 dígitos.");
            }
            telefonePadronizado = digitosTelefone;
        }
        if (dataNascimento != null) {
            if (dataNascimento.toLocalDate().isAfter(LocalDate.now().minusYears(18))) {
                throw new RuntimeException("É necessário ter no mínimo 18 anos para se cadastrar.");
            } else if(dataNascimento.toLocalDate().isBefore(LocalDate.now().minusYears(117))) {
            	 throw new RuntimeException("Você não é tão velho assim Kalil");
            }
        } else {
            throw new RuntimeException("A data de nascimento é obrigatória.");
        }

        if (isOrganizador) {
            if (contaBancaria == null || !contaBancaria.matches("^[0-9\\-]+$") || contaBancaria.length() < 3) {
                throw new RuntimeException("Formato de conta bancária inválido. Use apenas números e hífen.");
            }
            IOrganizadorBuilder builder = new OrganizadorBuilderConcreto();
            Organizador novoOrganizador = ((OrganizadorBuilderConcreto) builder
                    .nome(nome)
                    .email(email)
                    .senha(senha)
                    .cpf(cpf)
                    .telefone(telefonePadronizado)
                    .dataNascimento(dataNascimento)
                    .cidade(cidade)
                    .endereco(endereco))
                    .cnpj(cnpj)
                    .contaBancaria(contaBancaria)
                    .build();
            return usuarioRepository.save(novoOrganizador);
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
